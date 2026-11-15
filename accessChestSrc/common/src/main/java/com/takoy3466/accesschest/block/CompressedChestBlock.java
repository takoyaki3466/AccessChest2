package com.takoy3466.accesschest.block;

import com.mojang.serialization.MapCodec;
import com.takoy3466.accesschest.block.entity.CompressedChestBlockEntity;
import com.takoy3466.accesschest.chest.ChestKind;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Large chest that keeps its items in the block. Breaking it drops the items.
 */
public class CompressedChestBlock extends StorageChestBlock {
    public static final MapCodec<CompressedChestBlock> CODEC = simpleCodec(CompressedChestBlock::new);

    public CompressedChestBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<CompressedChestBlock> codec() {
        return CODEC;
    }

    @Override
    protected ChestKind kind() {
        return ChestKind.COMPRESSED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CompressedChestBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
