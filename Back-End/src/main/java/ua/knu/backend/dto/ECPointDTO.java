package ua.knu.backend.dto;

import lombok.*;
import org.bouncycastle.math.ec.ECPoint;
import ua.knu.backend.sigalgorithms.EllipticCurve;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ECPointDTO implements Serializable {
    private BigInteger x;
    private BigInteger y;

    public ECPointDTO(ECPoint point){
        this.x = point.getXCoord().toBigInteger();
        this.y = point.getYCoord().toBigInteger();
    }

    public ECPoint getECPoint(){
        EllipticCurve curve = new EllipticCurve();
        return curve.createPoint(this);
    }
}
