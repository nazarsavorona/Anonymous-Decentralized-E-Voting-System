package ua.knu.backend.hashalgorithms;

import lombok.AllArgsConstructor;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Component;

import static ua.knu.backend.hashalgorithms.HexUtils.leftRotate64;
import static ua.knu.backend.hashalgorithms.HexUtils.convertToUint;
import static ua.knu.backend.hashalgorithms.HexUtils.convertFromLittleEndianTo64;
import static ua.knu.backend.hashalgorithms.HexUtils.convertFrom64ToLittleEndian;
import static java.lang.Math.min;
import static java.lang.System.arraycopy;
import static java.util.Arrays.fill;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Hex;

@Component(value = "Keccak")
@AllArgsConstructor
public class Keccak implements HashAlgorithm{
    private final int rate = 1088;
    public final int d = 0x01;
    public final int outputLen = 256;

    private static final BigInteger BIT_64 = new BigInteger("18446744073709551615");

    @Override
    public String Hash(String message){
        return Hex.encodeHexString(getHash(message.getBytes()));
    }

    public String Hash(ECPoint point){
        byte[] toHash = point.getEncoded(true);
        return Hex.encodeHexString(toHash);
    }

    public byte[] getHash(final byte[] message) {
        int[] uState = new int[200];
        int[] uMessage = convertToUint(message);


        int rateInBytes = rate / 8;
        int blockSize = 0;
        int inputOffset = 0;

        while (inputOffset < uMessage.length) {
            blockSize = min(uMessage.length - inputOffset, rateInBytes);
            for (int i = 0; i < blockSize; i++) {
                uState[i] = uState[i] ^ uMessage[i + inputOffset];
            }

            inputOffset = inputOffset + blockSize;
            if (blockSize == rateInBytes) {
                doKeccakFill(uState);
                blockSize = 0;
            }
        }

        uState[blockSize] = uState[blockSize] ^ d;
        if ((d & 0x80) != 0 && blockSize == (rateInBytes - 1)) {
            doKeccakFill(uState);
        }

        uState[rateInBytes - 1] = uState[rateInBytes - 1] ^ 0x80;
        doKeccakFill(uState);

        ByteArrayOutputStream byteResults = new ByteArrayOutputStream();
        int tOutputLen = outputLen / 8;
        while (tOutputLen > 0) {
            blockSize = min(tOutputLen, rateInBytes);
            for (int i = 0; i < blockSize; i++) {
                byteResults.write((byte) uState[i]);
            }

            tOutputLen -= blockSize;
            if (tOutputLen > 0) {
                doKeccakFill(uState);
            }
        }

        return byteResults.toByteArray();
    }

    private void doKeccakFill(final int[] uState) {
        BigInteger[][] lState = new BigInteger[5][5];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int[] data = new int[8];
                arraycopy(uState, 8 * (i + 5 * j), data, 0, data.length);
                lState[i][j] = convertFromLittleEndianTo64(data);
            }
        }
        roundB(lState);

        fill(uState, 0);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int[] data = convertFrom64ToLittleEndian(lState[i][j]);
                arraycopy(data, 0, uState, 8 * (i + 5 * j), data.length);
            }
        }

    }

    private void roundB(final BigInteger[][] state) {
        int LFSRstate = 1;
        for (int round = 0; round < 24; round++) {
            BigInteger[] C = new BigInteger[5];
            BigInteger[] D = new BigInteger[5];

            for (int i = 0; i < 5; i++) {
                C[i] = state[i][0].xor(state[i][1]).xor(state[i][2]).xor(state[i][3]).xor(state[i][4]);
            }

            for (int i = 0; i < 5; i++) {
                D[i] = C[(i + 4) % 5].xor(leftRotate64(C[(i + 1) % 5], 1));
            }

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    state[i][j] = state[i][j].xor(D[i]);
                }
            }

            int x = 1, y = 0;
            BigInteger current = state[x][y];
            for (int i = 0; i < 24; i++) {
                int tX = x;
                x = y;
                y = (2 * tX + 3 * y) % 5;

                BigInteger shiftValue = current;
                current = state[x][y];

                state[x][y] = leftRotate64(shiftValue, (i + 1) * (i + 2) / 2);
            }

            for (int j = 0; j < 5; j++) {
                BigInteger[] t = new BigInteger[5];
                for (int i = 0; i < 5; i++) {
                    t[i] = state[i][j];
                }

                for (int i = 0; i < 5; i++) {
                    // ~t[(i + 1) % 5]
                    BigInteger invertVal = t[(i + 1) % 5].xor(BIT_64);
                    // t[i] ^ ((~t[(i + 1) % 5]) & t[(i + 2) % 5])
                    state[i][j] = t[i].xor(invertVal.and(t[(i + 2) % 5]));
                }
            }

            for (int i = 0; i < 7; i++) {
                LFSRstate = ((LFSRstate << 1) ^ ((LFSRstate >> 7) * 0x71)) % 256;
                // pow(2, i) - 1
                int bitPosition = (1 << i) - 1;
                if ((LFSRstate & 2) != 0) {
                    state[0][0] = state[0][0].xor(BigInteger.valueOf(1).shiftLeft(bitPosition));
                }
            }
        }
    }

    public static void main(String[] args) {
        HashAlgorithm hashFunction = new Keccak();

        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(9);
        list.add(10);

        String listString = list.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        System.out.println(listString);

        System.out.println(hashFunction.Hash(listString));


    }

}