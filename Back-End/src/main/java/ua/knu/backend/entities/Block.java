package ua.knu.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@Table(name = "blocks")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Block {
    @Id
    @Column(name = "block_hash", nullable = false, unique = true)
    @JsonProperty
    private String blockHash;

    @Column(name = "major_version", nullable = false)
    @JsonProperty
    private int majorVersion;

    @Column(name = "minor_version", nullable = false)
    @JsonProperty
    private int minorVersion;

    @Column(name = "time_stamp", nullable = false)
    @JsonProperty
    private long timeStamp;

    @Column(name = "previous_block_hash", nullable = false, unique = true)
    @JsonProperty
    private String previousBlockHash;

    @Column(name = "merkle_root_hash", nullable = false)
    @JsonProperty
    private String merkleRootHash;

    @Column(name = "nonce", nullable = false)
    private int nonce;

    public void incrementNonce() {
        this.nonce++;
    }

    public String getFieldsString() {
        return majorVersion + String.valueOf(minorVersion) + timeStamp + previousBlockHash + merkleRootHash + nonce;
    }
}



