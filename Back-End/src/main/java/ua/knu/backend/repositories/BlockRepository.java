package ua.knu.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.backend.entities.Block;

public interface BlockRepository extends JpaRepository<Block, Integer> {
    Block findByBlockHash(String blockHash);
}
