package ua.knu.backend.services;

import org.bouncycastle.math.ec.ECPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.knu.backend.dto.ECPointDTO;
import ua.knu.backend.dto.SignatureDTO;
import ua.knu.backend.hashalgorithms.HashAlgorithm;
import ua.knu.backend.repositories.TransactionRepository;
import ua.knu.backend.sigalgorithms.KeyPair;
import ua.knu.backend.entities.Transaction;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {
    private final HashAlgorithm hashAlgorithm;
    private final RingSignatureService signatureService;
    private final TransactionRepository transactionRepository;


    public TransactionService(RingSignatureService signatureService,
                              TransactionRepository transactionRepository,
                              HashAlgorithm hashAlgorithm) {
        this.signatureService = signatureService;
        this.transactionRepository = transactionRepository;
        this.hashAlgorithm = hashAlgorithm;
    }

    public Transaction save(Transaction entity) {
        return transactionRepository.save(entity);
    }

    public void setTransactionHashValue(Transaction transaction) {
        transaction.setTransactionID(hashAlgorithm.Hash(transaction.getFieldsString()));
    }

    public void signTransaction(Transaction transaction, KeyPair keyPair, List<ECPoint> publicKeys, int s) {
        List<ECPointDTO> pKeys = new ArrayList<>();

        for(ECPoint pKey:publicKeys){
            pKeys.add(new ECPointDTO(pKey.getXCoord().toBigInteger(), pKey.getYCoord().toBigInteger()));
        }

        transaction.setPublicKeys(pKeys);
        String message = transaction.getFieldsString();

        transaction.setSignature(new SignatureDTO(this.signatureService.signMessage(message, keyPair, publicKeys, s)));
    }
}
