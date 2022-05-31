package ua.knu.backend.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Service;
import ua.knu.backend.dto.ECPointDTO;
import ua.knu.backend.dto.SignatureDTO;
import ua.knu.backend.hashalgorithms.HashAlgorithm;
import ua.knu.backend.repositories.TransactionRepository;
import ua.knu.backend.sigalgorithms.KeyPair;
import ua.knu.backend.entities.Transaction;
import ua.knu.backend.sigalgorithms.Signature;
import ua.knu.backend.validator.TransactionProvider;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TransactionService {
    private final HashAlgorithm hashAlgorithm;
    @Getter
    private final RingSignatureService ringSignatureService;
    private final TransactionRepository transactionRepository;
    private final TransactionProvider transactionProvider;

    public TransactionService(RingSignatureService ringSignatureService,
                              TransactionRepository transactionRepository,
                              HashAlgorithm hashAlgorithm,
                              TransactionProvider transactionProvider) {
        this.ringSignatureService = ringSignatureService;
        this.transactionRepository = transactionRepository;
        this.hashAlgorithm = hashAlgorithm;
        this.transactionProvider = transactionProvider;
        log.info("Transaction service up");
    }

    public void save(Transaction entity) {
        this.transactionRepository.save(entity);
    }

    public void addTransaction(Transaction transaction){
        setTransactionHashValue(transaction);
        this.transactionProvider.addTransaction(transaction);
    }

    public List<Transaction> indexTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getVoterTransactions(ECPointDTO keyImage) {
        List<Transaction> allTransactions = indexTransactions();
        List<Transaction> voterTransactions = new ArrayList<>();

        for (Transaction transaction : allTransactions) {
            if(transaction.getSignature().getKeyImage().equals(keyImage)){
                voterTransactions.add(transaction);
            }
        }

        return voterTransactions;
    }

    public Transaction getTransaction(String txID){
        return transactionRepository.findByTransactionID(txID);
    }

    public void setTransactionHashValue(Transaction transaction) {
        transaction.setTransactionID(hashAlgorithm.Hash(transaction.getFieldsString()));
    }

    public void signTransaction(Transaction transaction, KeyPair keyPair, List<ECPoint> publicKeys, int s) {
        List<ECPointDTO> pKeys = new ArrayList<>();

        for (ECPoint pKey : publicKeys) {
            pKeys.add(new ECPointDTO(pKey.getXCoord().toBigInteger(), pKey.getYCoord().toBigInteger()));
        }

        transaction.setPublicKeys(pKeys);
        String message = transaction.getSigningString();

        transaction.setSignature(new SignatureDTO(this.ringSignatureService.signMessage(message, keyPair, publicKeys,
                s)));
    }

    public boolean verifyTransaction(Transaction transaction) {
        Signature signature = new Signature(transaction.getSignature());
        String message = transaction.getSigningString();
        List<ECPointDTO> publicKeys = transaction.getPublicKeys();
        List<ECPoint> pKeys = new ArrayList<>();

        for (ECPointDTO pKey : publicKeys) {
            pKeys.add(pKey.convertToECPoint());
        }

        return this.ringSignatureService.verifySignature(message, signature, pKeys);
    }
}
