package com.takoy3466.accesschest.chest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

/**
 * Variant of a chest item or placed chest.
 *
 * @param color shared storage channel for Access Chests, cosmetic for Compressed Chests
 * @param grade size class
 * @param copy  copies are crafted from an existing Access Chest and can not be upgraded
 */
public record ChestSpec(DyeColor color, ChestGrade grade, boolean copy) {

    public static final ChestSpec DEFAULT = new ChestSpec(DyeColor.WHITE, ChestGrade.LOWEST, false);

    public static final Codec<ChestSpec> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DyeColor.CODEC.fieldOf("color").forGetter(ChestSpec::color),
            ChestGrade.CODEC.fieldOf("grade").forGetter(ChestSpec::grade),
            Codec.BOOL.optionalFieldOf("copy", false).forGetter(ChestSpec::copy)
    ).apply(instance, ChestSpec::new));

    public static final StreamCodec<ByteBuf, ChestSpec> STREAM_CODEC = StreamCodec.composite(
            DyeColor.STREAM_CODEC, ChestSpec::color,
            ChestGrade.STREAM_CODEC, ChestSpec::grade,
            ByteBufCodecs.BOOL, ChestSpec::copy,
            ChestSpec::new
    );

    public ChestSpec withColor(DyeColor newColor) {
        return new ChestSpec(newColor, grade, copy);
    }

    public ChestSpec asCopy() {
        return new ChestSpec(color, grade, true);
    }
}
