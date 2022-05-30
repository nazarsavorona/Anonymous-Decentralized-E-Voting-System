package ua.knu.backend.services;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Component;
import ua.knu.backend.hashalgorithms.HashAlgorithm;
import ua.knu.backend.hashalgorithms.SHA256;
import ua.knu.backend.sigalgorithms.EllipticCurve;
import ua.knu.backend.sigalgorithms.KeyPair;
import ua.knu.backend.sigalgorithms.Signature;
import ua.knu.backend.utils.SignatureUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RingSignatureService {
    private final HashAlgorithm hashAlgorithm;

    private final EllipticCurve curve;

    public RingSignatureService(SHA256 hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
        this.curve = new EllipticCurve();
    }

    public KeyPair generateKeys() {
        return new KeyPair(curve);
    }

    public Signature signMessage(String message, KeyPair personalKeys,
                                 List<ECPoint> publicKeys, int s) {
        int publicKeysCount = publicKeys.size();

        if (s < 0 || s >= publicKeysCount) {
            throw new AssertionError();
        }

        List<BigInteger> rList = SignatureUtils.generateRandomBigIntegerList(publicKeysCount, curve.getOrder());
        List<BigInteger> cList = SignatureUtils.generateRandomBigIntegerList(publicKeysCount, curve.getOrder());

        List<ECPoint> lArray = new ArrayList<>();
        List<ECPoint> rArray = new ArrayList<>();

        ECPoint basePoint = curve.getBasePoint();
        for (int i = 0; i < publicKeysCount; i++) {
            ECPoint rG = basePoint.multiply(rList.get(i)).normalize();
            ECPoint rH = curve.deterministicHashOnCurve(publicKeys.get(i))
                    .multiply(rList.get(i)).normalize();

            if (i == s) {
                lArray.add(rG);
                rArray.add(rH);
                continue;
            }

            ECPoint cP = publicKeys.get(i).multiply(cList.get(i)).normalize();
            ECPoint rGcP = rG.add(cP).normalize();
            lArray.add(rGcP);

            ECPoint cI = personalKeys.getKeyImage().multiply(cList.get(i)).normalize();
            ECPoint rHcI = rH.add(cI).normalize();
            rArray.add(rHcI);
        }

        BigInteger c = SignatureUtils.getHash(hashAlgorithm, message, lArray, rArray);
        BigInteger res = BigInteger.valueOf(0);

        for (int i = 0; i < publicKeysCount; i++) {
            if (i != s) {
                res = res.add(cList.get(i));
            }
        }

        res = (c.subtract(res)).mod(curve.getOrder());
        cList.set(s, res);

        BigInteger subtrahend = cList.get(s).multiply(personalKeys.getPrivateKey());
        rList.set(s, (rList.get(s).subtract(subtrahend)).mod(curve.getOrder()));

        return new Signature(personalKeys.getKeyImage(), cList, rList);
    }

    public boolean verifySignature(String message, Signature signature,
                                   List<ECPoint> publicKeys) {
        List<ECPoint> newLList = new ArrayList<>();
        List<ECPoint> newRList = new ArrayList<>();

        List<BigInteger> cList = signature.getCList();
        List<BigInteger> rList = signature.getRList();

        int publicKeysCount = publicKeys.size();
        BigInteger c = BigInteger.valueOf(0);

        for (int i = 0; i < publicKeysCount; i++) {
            ECPoint rG = curve.getBasePoint().multiply(rList.get(i)).normalize();
            ECPoint cP = publicKeys.get(i).multiply(cList.get(i)).normalize();
            ECPoint rGcP = rG.add(cP).normalize();
            newLList.add(rGcP);

            ECPoint rH = curve.deterministicHashOnCurve(publicKeys.get(i))
                    .multiply(rList.get(i)).normalize();
            ECPoint cI = signature.getKeyImage().multiply(cList.get(i)).normalize();
            ECPoint rHcI = rH.add(cI).normalize();
            newRList.add(rHcI);

            c = c.add(cList.get(i));
        }

        c = c.mod(curve.getOrder());
        BigInteger hash = SignatureUtils.getHash(hashAlgorithm, message, newLList, newRList).mod(curve.getOrder());

        return c.equals(hash);
    }

    public static void main(String[] args) {
        RingSignatureService ringSignatureService = new RingSignatureService(new SHA256());

        int numberOfKeys = 8;

        List<KeyPair> keys = new ArrayList<>();
        for (int i = 0; i < numberOfKeys; i++) {
            keys.add(ringSignatureService.generateKeys());
        }

        String message = "Yes, it's working...";

        List<ECPoint> publicKeys = new ArrayList<>();
        for (int i = 0; i < numberOfKeys; i++) {
            publicKeys.add(keys.get(i).getPublicKey());
        }

        Signature signature = ringSignatureService.signMessage(message,
                keys.get(5), publicKeys, 5);

        log.info(String.valueOf(ringSignatureService.verifySignature(message, signature, publicKeys)));
    }
}