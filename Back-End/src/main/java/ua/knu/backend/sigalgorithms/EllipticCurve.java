package ua.knu.backend.sigalgorithms;

import javafx.util.Pair;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import ua.knu.backend.dto.ECPointDTO;
import ua.knu.backend.utils.SignatureUtils;

import java.math.BigInteger;

public class EllipticCurve {
    private final ECNamedCurveParameterSpec spec;
    private final ECCurve curve;

    public EllipticCurve() {
        spec = ECNamedCurveTable.getParameterSpec("curve25519");
        curve = spec.getCurve();
    }

    public ECPoint deterministicHashOnCurve(ECPoint publicKey) {
        BigInteger pKInteger = new BigInteger(publicKey.getEncoded(true));
        return spec.getG().multiply(pKInteger).normalize();
    }

    public ECPoint getKeyImage(BigInteger privateKey, ECPoint publicKey) {
        return deterministicHashOnCurve(publicKey).multiply(privateKey).normalize();
    }

    public ECPoint getPublicKey(BigInteger factor) {
        return spec.getG().multiply(factor).normalize();
    }

    public Pair<BigInteger, ECPoint> generateKeyPair() {
        BigInteger privateKey = SignatureUtils.nextRandomBigInteger(getOrder());

        return new Pair<>(privateKey, getPublicKey(privateKey));
    }

    public Pair<BigInteger, ECPoint> generateKeyPair(BigInteger privateKey) {
        return new Pair<>(privateKey, getPublicKey(privateKey));
    }

    public ECPoint createPoint(ECPointDTO point) {
        return curve.createPoint(point.getX(), point.getY());
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
