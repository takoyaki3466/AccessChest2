package com.takoy3466.accesschest.block;

import com.mojang.serialization.MapCodec;
import com.takoy3466.accesschest.block.entity.AccessChestBlockEntity;
import com.takoy3466.accesschest.chest.ChestKind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Chest that shows the world wide shared storage of its color.
 */
public class AccessChestBlock extends StorageChestBlock {
    public static final MapCodec<AccessChestBlock> CODEC = simpleCodec(AccessChestBlock::new);

    private static final int PARTICLES_PER_TICK = 3;
    private static final double PARTICLE_OFFSET = 0.25;
    private static final double PARTICLE_VERTICAL_SPEED = 0.125;

    public AccessChestBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<AccessChestBlock> codec() {
        return CODEC;
    }

    @Override
    protected ChestKind kind() {
        return ChestKind.ACCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AccessChestBlockEntity(pos, state);
    }

    /** Portal particles flying out of the chest, as in the 1.7.10 version. */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < PARTICLES_PER_TICK; i++) {
            int directionX = random.nextInt(2) * 2 - 1;
            int directionZ = random.nextInt(2) * 2 - 1;
            double x = pos.getX() + 0.5 + PARTICLE_OFFSET * directionX;
            double y = pos.getY() + random.nextFloat();
            double z = pos.getZ() + 0.5 + PARTICLE_OFFSET * directionZ;
            double speedX = random.nextFloat() * directionX;
            double speedY = (random.nextFloat() - 0.5) * PARTICLE_VERTICAL_SPEED;
            double speedZ = random.nextFloat() * directionZ;
            level.addParticle(ParticleTypes.PORTAL, x, y, z, speedX, speedY, speedZ);
        }
    }
}
