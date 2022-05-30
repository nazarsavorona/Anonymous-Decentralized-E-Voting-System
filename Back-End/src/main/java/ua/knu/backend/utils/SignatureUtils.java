package ua.knu.backend.utils;

import org.bouncycastle.math.ec.ECPoint;
import ua.knu.backend.hashalgorithms.HashAlgorithm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SignatureUtils {
    private static final Random rand = new Random();

    public static BigInteger nextRandomBigInteger(BigInteger n) {
        BigInteger result = new BigInteger(n.bitLength(), rand);
        while (result.compareTo(n) >= 0 || result.compareTo(BigInteger.valueOf(1)) <= 0) {
            result = new BigInteger(n.bitLength(), rand);
        }
        return result;
    }

    public static List<BigInteger> generateRandomBigIntegerList(int size, BigInteger order) {
        List<BigInteger> numbers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            numbers.add(nextRandomBigInteger(order));
        }
        return numbers;
    }

    public static BigInteger getHash(HashAlgorithm hashAlgorithm, String message, List<ECPoint> ls, List<ECPoint> rs) {
        StringBuilder toHash = new StringBuilder(hashAlgorithm.Hash(message));

        for (ECPoint l : ls) {
            toHash.append(l);
        }

        for (ECPoint r : rs) {
            toHash.append(r);
        }

        String hash = hashAlgorithm.Hash(toHash.toString());

        return new BigInteger(hash.getBytes());
    }
}
