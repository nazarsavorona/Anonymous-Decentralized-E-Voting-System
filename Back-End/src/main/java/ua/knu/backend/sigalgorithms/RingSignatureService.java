package ua.knu.backend.sigalgorithms;

import org.bouncycastle.math.ec.ECPoint;
import ua.knu.backend.hashalgorithms.HashAlgorithm;
import ua.knu.backend.hashalgorithms.SHA1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RingSignatureService {
    private final HashAlgorithm hashAlgorithm;

    private final EllipticCurve curve;

    public RingSignatureService(HashAlgorithm hashAlgorithm, String ellipticCurveName) {
        this.hashAlgorithm = hashAlgorithm;
        this.curve = new EllipticCurve(ellipticCurveName);
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

        List<BigInteger> rList = Utils.generateRandomBigIntegerList(publicKeysCount, curve.getOrder());
        List<BigInteger> cList = Utils.generateRandomBigIntegerList(publicKeysCount, curve.getOrder());

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

        BigInteger c = Utils.getHash(hashAlgorithm, message, lArray, rArray);
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
        BigInteger hash = Utils.getHash(hashAlgorithm, message, newLList, newRList).mod(curve.getOrder());

        return c.equals(hash);
    }

    public static void main(String[] args) {
        RingSignatureService ringSignatureService = new RingSignatureService(new SHA1(), "curve25519");
        KeyPair keys1 = ringSignatureService.generateKeys();
        KeyPair keys2 = ringSignatureService.generateKeys();
        KeyPair keys3 = ringSignatureService.generateKeys();

        String message = "Yes, it's working...";

        List<ECPoint> publicKeys = new ArrayList<>(Arrays.asList(keys1.getPublicKey(), keys2.getPublicKey()
                , keys3.getPublicKey()));

        Signature signature = ringSignatureService.signMessage(message,
                keys2, publicKeys, 1);

        System.out.print(ringSignatureService.verifySignature(message, signature, publicKeys));
    }
}