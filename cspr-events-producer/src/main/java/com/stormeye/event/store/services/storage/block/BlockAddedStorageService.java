package com.stormeye.event.store.services.storage.block;

import com.casper.sdk.model.event.DataType;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.stormeye.event.store.services.storage.block.domain.Block;
import com.stormeye.event.store.services.storage.block.repository.BlockRepository;
import com.stormeye.event.store.services.storage.StorageFactory;
import com.stormeye.event.store.services.storage.StorageService;
import org.springframework.stereotype.Component;

/**
 * @author ian@meywood.com
 */
@Component
class BlockAddedStorageService implements StorageService<BlockAdded, Block> {

    private final BlockRepository blockRepository;

    BlockAddedStorageService(final BlockRepository blockRepository, final StorageFactory storageFactory) {
        this.blockRepository = blockRepository;
        storageFactory.register(DataType.BLOCK_ADDED, this);
    }

    @Override
    public Block store(final BlockAdded toStore) {

        return this.blockRepository.save(
                new Block(toStore.getBlockHash(),
                        toStore.getBlock().getHeader().getParentHash(),
                        toStore.getBlock().getHeader().getTimeStamp(),
                        toStore.getBlock().getHeader().getStateRootHash(),
                        toStore.getBlock().getBody().getDeployHashes().size(),
                        toStore.getBlock().getBody().getTransferHashes().size(),
                        toStore.getBlock().getHeader().getEraId(),
                        toStore.getBlock().getBody().getProposer(),
                        toStore.getBlock().getHeader().getHeight()
                )
        );
    }
}
