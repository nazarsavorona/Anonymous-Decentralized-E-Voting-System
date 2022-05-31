package ua.knu.backend.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.backend.entities.Block;
import ua.knu.backend.services.BlockService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class BlockController {
    private final BlockService blockService;

    @GetMapping(value = "/blocks")
    public List<Block> findAll(){
        return this.blockService.indexBlocks();
    }

    @GetMapping(value = "/block")
    public void testSaveBlock() {
        Block block = new Block();

        block.setBlockHash("");
        block.setMajorVersion(0);
        block.setMinorVersion(0);
        block.setTimeStamp(0);
        block.setPreviousBlockHash("");
        block.setMerkleRootHash("");
        block.setNonce(0);

        Block savedBlock = blockService.save(block);
        log.info(savedBlock.toString());
    }
}
