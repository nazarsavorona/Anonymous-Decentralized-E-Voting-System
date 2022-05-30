package ua.knu.backend.sigalgorithms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bouncycastle.math.ec.ECPoint;
import ua.knu.backend.dto.SignatureDTO;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Signature implements Serializable {
    private ECPoint keyImage;
    private List<BigInteger> cList;
    private List<BigInteger> rList;

    public Signature(SignatureDTO dto) {
        EllipticCurve curve = new EllipticCurve();
        this.keyImage = curve.createPoint(dto.getKeyImage());
        this.cList = dto.getCList();
        this.rList = dto.getRList();
    }
}
