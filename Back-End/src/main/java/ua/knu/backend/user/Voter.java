package ua.knu.backend.user;

import lombok.Getter;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Component;
import ua.knu.backend.dto.ECPointDTO;
import ua.knu.backend.dto.SignatureDTO;
import ua.knu.backend.entities.Transaction;
import ua.knu.backend.identityprovider.IdentityProvider;
import ua.knu.backend.services.RingSignatureService;
import ua.knu.backend.sigalgorithms.KeyPair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class Voter {
    private final RingSignatureService ringSignatureService;
    private final IdentityProvider identityProvider;

    public Voter(RingSignatureService ringSignatureService,
                  IdentityProvider identityProvider) {
        this.ringSignatureService = ringSignatureService;
        this.identityProvider = identityProvider;
    }

    private BigInteger privateKey;

    @Getter
    private KeyPair myKeyPair;

    public Transaction createTransaction(int candidateID) {
        Transaction transaction = new Transaction();

        transaction.setCandidateID(candidateID);
        transaction.setTimeStamp(new Date().getTime() / 1000);
        transaction.setNonce(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));

        List<ECPoint> publicKeys = this.identityProvider.getPublicKeys();
        List<ECPointDTO> publicKeysDTO = new ArrayList<>();
        Collections.shuffle(publicKeys);

        int s = -1;
        for (int i = 0; i < publicKeys.size(); i++) {
            ECPoint currentKey = publicKeys.get(i);
            publicKeysDTO.add(new ECPointDTO(currentKey.getXCoord().toBigInteger(),
                    currentKey.getYCoord().toBigInteger()));

            if (currentKey.equals(this.myKeyPair.getPublicKey())) {
                s = i;
            }
        }

        if (s == -1) {
            return null;
        }

        transaction.setPublicKeys(publicKeysDTO);
        SignatureDTO signature = new SignatureDTO(ringSignatureService.signMessage(transaction.getSigningString(),
                this.myKeyPair, publicKeys, s));

        transaction.setSignature(signature);


        return transaction;
    }

    public boolean setPrivateKey(BigInteger privateKey) {
        this.privateKey = privateKey;
        this.myKeyPair = ringSignatureService.generateKeys(this.privateKey);
        List<ECPoint> publicKeys = this.identityProvider.getPublicKeys();

        for (ECPoint publicKey : publicKeys) {
            if(this.myKeyPair.getPublicKey().equals(publicKey)){
                return true;
            }
        }

        return false;
    }
}
