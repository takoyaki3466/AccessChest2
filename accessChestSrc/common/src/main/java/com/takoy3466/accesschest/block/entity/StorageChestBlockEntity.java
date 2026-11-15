package com.takoy3466.accesschest.block.entity;

import com.takoy3466.accesschest.block.StorageChestBlock;
import com.takoy3466.accesschest.chest.ChestGrade;
import com.takoy3466.accesschest.chest.ChestSpec;
import com.takoy3466.accesschest.init.CompatData;
import com.takoy3466.accesschest.menu.ChestAccess;
import com.takoy3466.accesschest.menu.StorageChestMenu;
import com.takoy3466.accesschest.storage.BoundedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Common part of both chest block entities: grade and copy flag (the color is part of the block state), the open/close
 * state of the lid and the {@link Container} view used by hoppers and other automation.
 */
public abstract class StorageChestBlockEntity extends BlockEntity implements Container, ChestAccess {
    private static final String GRADE_KEY = "grade";
    private static final String COPY_KEY = "copy";
    private static final float SOUND_VOLUME = 0.5F;
    private static final float SOUND_BASE_PITCH = 0.9F;
    private static final float SOUND_PITCH_VARIATION = 0.1F;

    private ChestGrade grade = ChestGrade.LOWEST;
    private boolean copy;

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            playSound(SoundEvents.CHEST_OPEN);
            setLidOpen(state, true);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            playSound(SoundEvents.CHEST_CLOSE);
            setLidOpen(state, false);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int previousCount, int newCount) {
            // The lid only has an open and a closed model, so the exact number of viewers is not needed.
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof StorageChestMenu menu && menu.isAccessedThrough(StorageChestBlockEntity.this);
        }
    };

    protected StorageChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public DyeColor color() {
        return getBlockState().getValue(StorageChestBlock.COLOR);
    }

    public ChestSpec spec() {
        return new ChestSpec(color(), grade, copy);
    }

    protected ChestGrade grade() {
        return grade;
    }

    /** Called whenever the grade is assigned, before any items are accessed. */
    protected void onGradeAssigned(ChestGrade grade) {
    }

    private void assignSpecData(ChestGrade newGrade, boolean newCopy) {
        this.grade = newGrade;
        this.copy = newCopy;
        onGradeAssigned(newGrade);
    }

    /**
     * @return the storage seen by this chest, only available on the logical server
     */
    protected abstract Optional<BoundedStorage> serverStorage();

    @Override
    public BoundedStorage storage() {
        return serverStorage().orElseThrow(() -> new IllegalStateException("Chest storage is only available on the server"));
    }

    // ---- Lid state --------------------------------------------------------------------------------------------

    public void recheckOpen() {
        if (!remove && level != null) {
            openersCounter.recheckOpeners(level, getBlockPos(), getBlockState());
        }
    }

    private void setLidOpen(BlockState state, boolean open) {
        if (level != null) {
            level.setBlock(getBlockPos(), state.setValue(StorageChestBlock.OPEN, open), Block.UPDATE_ALL);
        }
    }

    private void playSound(SoundEvent sound) {
        if (level != null) {
            float pitch = level.random.nextFloat() * SOUND_PITCH_VARIATION + SOUND_BASE_PITCH;
            level.playSound(null, getBlockPos(), sound, SoundSource.BLOCKS, SOUND_VOLUME, pitch);
        }
    }

    @Override
    public void startOpen(Player player) {
        if (!remove && !player.isSpectator() && level != null) {
            openersCounter.incrementOpeners(player, level, getBlockPos(), getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!remove && !player.isSpectator() && level != null) {
            openersCounter.decrementOpeners(player, level, getBlockPos(), getBlockState());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    // ---- Container view for automation --------------------------------------------------------------------------

    @Override
    public int getContainerSize() {
        return serverStorage().map(BoundedStorage::size).orElse(0);
    }

    @Override
    public boolean isEmpty() {
        Optional<BoundedStorage> storage = serverStorage();
        if (storage.isEmpty()) {
            return true;
        }
        for (int i = 0; i < storage.get().size(); i++) {
            if (!storage.get().get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return serverStorage().map(storage -> storage.get(slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return serverStorage().map(storage -> storage.remove(slot, amount)).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return serverStorage().map(storage -> storage.take(slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        storage().set(slot, stack);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return serverStorage().map(storage -> storage.canStore(stack)).orElse(false);
    }

    // ---- Persistence and item components ------------------------------------------------------------------------

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        assignSpecData(ChestGrade.CODEC.byName(tag.getString(GRADE_KEY), ChestGrade.LOWEST), tag.getBoolean(COPY_KEY));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        writeSpecData(tag);
    }

    private void writeSpecData(CompoundTag tag) {
        tag.putString(GRADE_KEY, grade.getSerializedName());
        tag.putBoolean(COPY_KEY, copy);
    }

    /** Only grade and copy flag are sent to clients (needed for pick block); stored items never are. */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        writeSpecData(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        ChestSpec spec = componentInput.getOrDefault(CompatData.CHEST_SPEC.get(), ChestSpec.DEFAULT);
        assignSpecData(spec.grade(), spec.copy());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        ChestSpec spec = spec();
        if (!ChestSpec.DEFAULT.equals(spec)) {
            components.set(CompatData.CHEST_SPEC.get(), spec);
        }
    }

    @SuppressWarnings("deprecation") // Deprecated in vanilla but still the hook used when saving a block entity into an item.
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(GRADE_KEY);
        tag.remove(COPY_KEY);
    }
}
