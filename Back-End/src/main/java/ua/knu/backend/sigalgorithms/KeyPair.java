package ua.knu.backend.sigalgorithms;

import javafx.util.Pair;
import lombok.Getter;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

@Getter
public class KeyPair {
    private final BigInteger privateKey;
    private final ECPoint publicKey;
    private final ECPoint keyImage;

    public KeyPair(EllipticCurve curve) {
        Pair<BigInteger, ECPoint> keys = curve.generateKeyPair();
        ECPoint generatedKeyImage = curve.getKeyImage(keys.getKey(), keys.getValue()).normalize();

        this.privateKey = keys.getKey();
        this.publicKey = keys.getValue();
        this.keyImage = generatedKeyImage;
    }

    public KeyPair(EllipticCurve curve, BigInteger privateKey) {
        Pair<BigInteger, ECPoint> keys = curve.generateKeyPair(privateKey);
        ECPoint generatedKeyImage = curve.getKeyImage(keys.getKey(), keys.getValue()).normalize();

        this.privateKey = keys.getKey();
        this.publicKey = keys.getValue();
        this.keyImage = generatedKeyImage;
    }
}
