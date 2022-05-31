package ua.knu.backend.dto;

import lombok.*;
import org.bouncycastle.math.ec.ECPoint;
import ua.knu.backend.sigalgorithms.EllipticCurve;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ECPointDTO implements Serializable {
    private BigInteger x;
    private BigInteger y;

    public ECPointDTO getECPointDTO(ECPoint point) {
        return new ECPointDTO(point.getXCoord().toBigInteger(),
                point.getYCoord().toBigInteger());
    }

    public ECPoint convertToECPoint() {
        EllipticCurve curve = new EllipticCurve();
        return curve.createPoint(this);
    }
}
