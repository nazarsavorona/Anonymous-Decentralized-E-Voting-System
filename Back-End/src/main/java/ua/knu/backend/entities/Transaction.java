package ua.knu.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ua.knu.backend.converters.PublicKeysConverterJson;
import ua.knu.backend.converters.SignatureConverterJson;
import ua.knu.backend.dto.ECPointDTO;
import ua.knu.backend.dto.SignatureDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
@Entity
@Getter
@Setter
@Slf4j
public class Transaction implements Serializable {
    @Id
    @Column(name = "tx_id", nullable = false, unique = true)
    @JsonProperty
    private String transactionID;

    @Column(name = "nonce", nullable = false)
    private int nonce;

    @Column(name = "candidate_id", nullable = false)
    @JsonProperty
    private int candidateID;

    @Column(name = "time_stamp", nullable = false)
    private long timeStamp;

    @Column(name = "public_keys", nullable = false)
    @Convert(converter = PublicKeysConverterJson.class)
    @JsonProperty
    private List<ECPointDTO> publicKeys;

    @Column(name = "signature", nullable = false, unique = true)
    @Convert(converter = SignatureConverterJson.class)
    @JsonProperty
    private SignatureDTO signature;

    @ManyToOne
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    public void printTxID() {
        log.info(this.transactionID);
    }

    public String getFieldsString(){
        return nonce +
                String.valueOf(candidateID) +
                timeStamp +
                publicKeys.toString() +
                signature;
    }

    public void printTransaction() {
        log.info(this.toString());
    }

    @Override
    public String toString() {
        return String.format("TransactionID: %s, Nonce: %d, CandidateID: %d, Timestamp: %d, Public Keys: %s, " +
                        "Signature: %s, Block hash: %s ",
                this.transactionID,
                this.nonce,
                this.candidateID,
                this.timeStamp,
                this.publicKeys,
                this.signature,
                this.block.getBlockHash());
    }
}
