package ua.knu.backend.sigalgorithms;

import lombok.Getter;
import lombok.ToString;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.List;

@Getter
@ToString
public class Signature {
    private ECPoint keyImage;
    private List<BigInteger> cList;
    private List<BigInteger> rList;

    public Signature(ECPoint keyImage, List<BigInteger> cList, List<BigInteger> rList){
        this.keyImage = keyImage;
        this.cList = cList;
        this.rList = rList;
    }
}
