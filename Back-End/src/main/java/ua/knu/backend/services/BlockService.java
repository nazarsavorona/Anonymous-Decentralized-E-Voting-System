package ua.knu.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.knu.backend.hashalgorithms.HashAlgorithm;
import ua.knu.backend.hashalgorithms.Keccak;
import ua.knu.backend.merkletree.MerkleTree;
import ua.knu.backend.merkletree.Node;
import ua.knu.backend.entities.Block;
import ua.knu.backend.entities.Transaction;
import ua.knu.backend.repositories.BlockRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BlockService {
    private final BlockRepository repository;

    public Block save(Block block) {
        return repository.save(block);
    }

    public Block getBlock(String blockHash){
        return  repository.findByBlockHash(blockHash);
    }

    public List<Block> indexBlocks(){
        return repository.findAll();
    }

    public void setMerkleRoot(Block block, List<Transaction> transactions) {
        List<String> transactionIDs = new ArrayList<>();

        for (Transaction transaction : transactions) {
            transactionIDs.add(transaction.getTransactionID());
        }

        Node root = MerkleTree.generateTree(transactionIDs);
        block.setMerkleRootHash(root.getHash());
    }
}
