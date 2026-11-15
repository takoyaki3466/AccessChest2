package com.takoy3466.accesschest.core.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * A block together with the block item registered under the same id.
 */
public final class CompatBlockHolder<B extends Block, I extends BlockItem> {
    private final CompatHolder<B> block;
    private final CompatHolder<I> item;

    public CompatBlockHolder(String id) {
        this.block = CompatHolder.create(id);
        this.item = CompatHolder.create(id);
    }

    public String getId() {
        return block.getId();
    }

    public CompatHolder<B> blockHolder() {
        return block;
    }

    public CompatHolder<I> itemHolder() {
        return item;
    }

    public B block() {
        return block.get();
    }

    public I item() {
        return item.get();
    }
}
