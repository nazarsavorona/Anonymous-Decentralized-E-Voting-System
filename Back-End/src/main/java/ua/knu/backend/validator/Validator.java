package ua.knu.backend.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.knu.backend.entities.Block;
import ua.knu.backend.entities.Transaction;
import ua.knu.backend.hashalgorithms.HashAlgorithm;
import ua.knu.backend.hashalgorithms.SHA256;
import ua.knu.backend.services.BlockService;
import ua.knu.backend.services.TransactionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class Validator {
    private static final long MAX_TRANSACTION_COUNT = 50;
    private static final long MINUTE = 60_000;
    private static final long SECONDS = 10_000;
    private final TransactionService transactionService;
    private final BlockService blockService;
    private final HashAlgorithm hashAlgorithm;
    private List<Transaction> memPool;
    private Block currentBlock;
    private List<Transaction> currentBLockTransactions;
    private long firstTransactionTime;
    private Transaction currentTransaction;
    private final Object currentTransactionLock = new Object();

    public Validator(TransactionService transactionService,
                     TransactionProvider provider,
                     BlockService blockService,
                     SHA256 hashAlgorithm) {
        this.transactionService = transactionService;
        this.blockService = blockService;
        this.hashAlgorithm = hashAlgorithm;

        this.memPool = new ArrayList<>();
        this.currentBLockTransactions = new ArrayList<>();
        this.currentBlock = new Block();
        this.currentBlock.setPreviousBlockHash("-1");
        this.firstTransactionTime = -1;

        new Thread(new TransactionPuller(this, provider), "Transaction puller").start();
        new Thread(new MemPoolManager(this), "MemPool manager").start();
        new Thread(new BlockLoader(this), "Block loader").start();
        new Thread(new Miner(this), "Miner").start();
        log.info("Validator up");
    }

    public boolean setCurrentTransaction(Transaction transaction) {
        synchronized (currentTransactionLock) {
            if (currentTransaction == null) {
                currentTransaction = transaction;
                return true;
            }
            return false;
        }
    }

    public void mainRoutine() {
        if (!this.currentBLockTransactions.isEmpty()) {
            this.currentBlock.setNonce(0);
            this.currentBlock.setMajorVersion(0);
            this.currentBlock.setMinorVersion(0);
            this.currentBlock.setTimeStamp(new Date().getTime() / 1000);
            this.blockService.setMerkleRoot(this.currentBlock, this.currentBLockTransactions);
            mineBlock(1);

            log.info(this.currentBlock.getBlockHash());

            Block createdBlock = this.blockService.save(currentBlock);
            for (Transaction transaction : this.currentBLockTransactions) {
                transaction.setBlock(createdBlock);
                this.transactionService.save(transaction);
            }

            this.currentBLockTransactions = new ArrayList<>();
            setNewBlock();
        }
    }

    public void addTransaction() throws InterruptedException {
        synchronized (currentTransactionLock) {
            if (this.currentTransaction == null) {
                return;
            }

            if (this.transactionService.verifyTransaction(this.currentTransaction)) {
                this.memPool.add(this.currentTransaction);
                if (this.firstTransactionTime == -1) {
                    this.firstTransactionTime = new Date().getTime();
                }
            } else {
                log.error(String.format("The transaction ( %s ) signature isn't valid!", this.currentTransaction));
            }

            this.currentTransaction = null;
        }
    }

    public void loadBlock() {
        synchronized (currentTransactionLock) {
            if (this.firstTransactionTime == -1) {
                return;
            }

            if (memPool.size() >= MAX_TRANSACTION_COUNT || new Date().getTime() - this.firstTransactionTime >= SECONDS) {
                if (!currentBLockTransactions.isEmpty()) {
                    return;
                }
                this.currentBLockTransactions.addAll(this.memPool);
                this.memPool = new ArrayList<>();
                this.firstTransactionTime = -1;
            }
        }
    }

    private void setNewBlock() {
        Block newBlock = new Block();
        newBlock.setPreviousBlockHash(this.currentBlock.getBlockHash());
        this.currentBlock = newBlock;
    }

    private void setBlockHash() {
        String blockString = this.currentBlock.getFieldsString();
        String blockHash = this.hashAlgorithm.Hash(blockString);
        this.currentBlock.setBlockHash(blockHash);
    }

    private void mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        setBlockHash();
        while (!this.currentBlock.getBlockHash().substring(0, prefix).equals(prefixString)) {
            this.currentBlock.incrementNonce();
            setBlockHash();
        }
    }
}
