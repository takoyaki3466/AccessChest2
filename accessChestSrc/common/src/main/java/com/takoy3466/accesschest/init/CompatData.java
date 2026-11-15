package com.takoy3466.accesschest.init;

import com.takoy3466.accesschest.chest.ChestSpec;
import com.takoy3466.accesschest.core.registry.CompatHolder;
import com.takoy3466.accesschest.core.registry.CompatRegistry;
import net.minecraft.core.component.DataComponentType;

/**
 * Data components.
 */
public final class CompatData {

    /** Variant of a chest item. Absent means {@link ChestSpec#DEFAULT}. */
    public static final CompatHolder<DataComponentType<ChestSpec>> CHEST_SPEC =
            CompatRegistry.registerDataComponentType("chest_spec", ChestSpec.CODEC, ChestSpec.STREAM_CODEC);

    private CompatData() {
    }

    /** Forces class loading so that the static fields above are registered. */
    public static void init() {
    }
}
