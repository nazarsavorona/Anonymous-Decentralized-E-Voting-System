package ua.knu.backend.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.backend.entities.Block;
import ua.knu.backend.services.BlockService;
import ua.knu.backend.hashalgorithms.SHA256;
import ua.knu.backend.sigalgorithms.KeyPair;
import ua.knu.backend.services.RingSignatureService;
import ua.knu.backend.entities.Transaction;
import ua.knu.backend.services.TransactionService;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
    private final BlockService blockService;

    @GetMapping(value = "/transaction")
    public void saveTransaction() {
        RingSignatureService ringSignatureService = new RingSignatureService(new SHA256());
        Transaction transaction = new Transaction();

        transaction.setNonce(5);
        transaction.setCandidateID(0);
        transaction.setTimeStamp(0);

        List<KeyPair> keys = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            keys.add(ringSignatureService.generateKeys());
        }

        List<ECPoint> publicKeys = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            publicKeys.add(keys.get(i).getPublicKey());
        }

        transactionService.signTransaction(transaction, keys.get(0), publicKeys, 0);
        transactionService.setTransactionHashValue(transaction);

        Block block = new Block();
        block.setBlockHash("Glory to Ukraine!");
        block.setMajorVersion(0);
        block.setMinorVersion(0);
        block.setTimeStamp(0);
        block.setPreviousBlockHash("");
        blockService.setMerkleRoot(block, new ArrayList<>(List.of(transaction)));
        block.setNonce(0);

        Block blockNew = blockService.save(block);
        transaction.setBlock(blockNew);

        transaction.printTransaction();
        transactionService.save(transaction);
    }
}
