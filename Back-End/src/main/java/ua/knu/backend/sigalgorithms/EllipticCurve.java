package ua.knu.backend.sigalgorithms;

import javafx.util.Pair;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import ua.knu.backend.sigalgorithms.Utils;

import java.math.BigInteger;

public class EllipticCurve {
    private final ECNamedCurveParameterSpec spec;
    private final ECCurve curve;

    public EllipticCurve(String curveName) {
        //"curve25519"
        spec = ECNamedCurveTable.getParameterSpec(curveName);
        curve = spec.getCurve();

    }

    public ECPoint deterministicHashOnCurve(ECPoint publicKey) {
        BigInteger pKInteger = new BigInteger(publicKey.getEncoded(true));
        return spec.getG().multiply(pKInteger).normalize();
    }

    public ECPoint getKeyImage(BigInteger factor, ECPoint publicKey) {
        return deterministicHashOnCurve(publicKey).multiply(factor).normalize();
    }

    public ECPoint getPublicKey(BigInteger factor) {
        return spec.getG().multiply(factor).normalize();
    }

    public Pair<BigInteger, ECPoint> generateKeyPair() {
        BigInteger privateKey = Utils.nextRandomBigInteger(getOrder());
//        byte[] keys = privateKey.toByteArray();
//        maskPrivateKey(keys);
//        privateKey = new BigInteger(keys);

        return new Pair<>(privateKey, getPublicKey(privateKey));
    }

    public static void maskPrivateKey(byte[] key) {
        key[0] &= 248;
        key[31] &= 127;
        key[31] |= 64;
    }

    public BigInteger getOrder() {
        return curve.getOrder();
    }

    public ECPoint getBasePoint() {
        return spec.getG();
    }
}
