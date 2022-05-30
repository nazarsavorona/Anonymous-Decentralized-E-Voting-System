package ua.knu.backend.dto;

import lombok.*;
import ua.knu.backend.sigalgorithms.Signature;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignatureDTO implements Serializable {
    private ECPointDTO keyImage;
    private List<BigInteger> cList;
    private List<BigInteger> rList;

    public SignatureDTO(Signature signature){
        this.keyImage = new ECPointDTO(signature.getKeyImage().getXCoord().toBigInteger(),
                signature.getKeyImage().getYCoord().toBigInteger());
        this.cList = signature.getCList();
        this.rList = signature.getRList();
    }
}
