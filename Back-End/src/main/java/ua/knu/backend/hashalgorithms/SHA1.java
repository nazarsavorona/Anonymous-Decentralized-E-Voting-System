package ua.knu.backend.hashalgorithms;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.pow;

@Component(value = "SHA1")
public class SHA1 implements HashAlgorithm{
    private final int BLOCK_SIZE = 512;
    private final int WORD_SIZE = 32;
    private final long MODULO = (long) pow(2, 32);

    @Override
    public String Hash(String message) {
        long h0 = 0x67452301L;
        long h1 = 0xEFCDAB89L;
        long h2 = 0x98BADCFEL;
        long h3 = 0x10325476L;
        long h4 = 0xC3D2E1F0L;
        int messageLengthInBits = message.length() * 8;

        String padded = padMessage(message, messageLengthInBits);
        List<List<Long>> words = divideIntoWords(padded);

        for (int i = 0; i < words.size(); i++) {
            List<Long> schedule = new ArrayList<>(80);
            for (int j = 0; j < 16; j++) {
                schedule.add(words.get(i).get(j));
            }
            for (int j = 16; j < 80; j++) {
                schedule.add(ROTL(schedule.get(j - 3) ^ schedule.get(j - 8) ^ schedule.get(j - 14) ^ schedule.get(j - 16), 1));
            }

            long a = h0;
            long b = h1;
            long c = h2;
            long d = h3;
            long e = h4;


            for (int t = 0; t < 80; t++) {
                long T = (ROTL(a, 5) + f_t(b, c, d, t) + e + K_t(t) + schedule.get(t)) % MODULO;
                e = d;
                d = c;
                c = ROTL(b, 30) % MODULO;
                b = a;
                a = T;
            }

            h0 = (h0 + a) % MODULO;
            h1 = (h1 + b) % MODULO;
            h2 = (h2 + c) % MODULO;
            h3 = (h3 + d) % MODULO;
            h4 = (h4 + e) % MODULO;
        }

        return Long.toHexString(h0) + Long.toHexString(h1) + Long.toHexString(h2) + Long.toHexString(h3) + Long.toHexString(h4);
    }

    private List<List<Long>> divideIntoWords(String padded) {
        List<List<Long>> words = new ArrayList<>(padded.length() / BLOCK_SIZE);

        for (int i = 0; i < padded.length() / BLOCK_SIZE; i++) {
            words.add(new ArrayList<>(16));
            for (int j = 0; j < 16; j++) {
                int shift = i * BLOCK_SIZE + j * WORD_SIZE;
                words.get(i).add(Long.parseLong(Long.toHexString(Long.parseLong(padded.substring(shift, shift + WORD_SIZE), 2)), 16));
            }
        }

        return words;
    }

    private String padMessage(String message, int messageLengthInBits) {
        StringBuilder padded = new StringBuilder();

        for (char c : message.toCharArray()) {
            padded.append(String.format("%08d", Long.parseLong(Long.toBinaryString(c))));
        }

        padded.append('1');

        int k = 0;

        for (k = 0; k < BLOCK_SIZE; k++) {
            if ((messageLengthInBits + 1 + k) % BLOCK_SIZE == 448) {
                break;
            }
            padded.append('0');
        }

        padded.append(String.format("%064d", Long.parseLong(Long.toBinaryString(messageLengthInBits))));

        return padded.toString();
    }

    private Long ROTL(long x, int n) {
        return ((x << n) | (x >> (WORD_SIZE - n))) % MODULO;
    }

    private Long f_t(long x, long y, long z, int t) {
        if (0 <= t && t < 20) {
            return (x & y) ^ (~x & z);
        }
        if (20 <= t && t < 40) {
            return x ^ y ^ z;
        }
        if (40 <= t && t < 60) {
            return (x & y) ^ (x & z) ^ (y & z);
        } else {
            return x ^ y ^ z;
        }
    }

    private Long K_t(int t) {
        if (0 <= t && t < 20) {
            return 0x5a827999L;
        }
        if (20 <= t && t < 40) {
            return 0x6ed9eba1L;
        }
        if (40 <= t && t < 60) {
            return 0x8f1bbcdcL;
        } else {
            return 0xca62c1d6L;
        }
    }

    public static void main(String[] args) {
        HashAlgorithm hashFunction = new SHA1();

        List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(6);
        list.add(12);

        String listString = list.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        System.out.println(listString);

        System.out.println(hashFunction.Hash("abc"));


    }
}

