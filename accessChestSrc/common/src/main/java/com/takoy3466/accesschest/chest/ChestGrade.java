package com.takoy3466.accesschest.chest;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Optional;
import java.util.function.IntFunction;

/**
 * Size class of a chest. Each grade multiplies the number of slots of the previous one.
 */
public enum ChestGrade implements StringRepresentable {
    CLASS_0(0),
    CLASS_1(1),
    CLASS_2(2),
    CLASS_3(3);

    /** Slot count of the lowest grade (same as a vanilla single chest). */
    private static final int BASE_SLOT_COUNT = 27;
    /** Every grade multiplies the slot count by this factor (27, 216, 1728, 13824). */
    private static final int SLOT_MULTIPLIER_PER_GRADE = 8;

    private static final IntFunction<ChestGrade> BY_LEVEL = ByIdMap.continuous(ChestGrade::level, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final StringRepresentable.EnumCodec<ChestGrade> CODEC = StringRepresentable.fromEnum(ChestGrade::values);
    public static final StreamCodec<ByteBuf, ChestGrade> STREAM_CODEC = ByteBufCodecs.idMapper(BY_LEVEL, ChestGrade::level);

    public static final ChestGrade LOWEST = CLASS_0;
    public static final ChestGrade HIGHEST = CLASS_3;

    private final int level;
    private final int slotCount;

    ChestGrade(int level) {
        this.level = level;
        int slots = BASE_SLOT_COUNT;
        for (int i = 0; i < level; i++) {
            slots *= SLOT_MULTIPLIER_PER_GRADE;
        }
        this.slotCount = slots;
    }

    public int level() {
        return level;
    }

    public int slotCount() {
        return slotCount;
    }

    public Optional<ChestGrade> next() {
        return this == HIGHEST ? Optional.empty() : Optional.of(BY_LEVEL.apply(level + 1));
    }

    @Override
    public String getSerializedName() {
        return "class_" + level;
    }
}
