package com.takoy3466.accesschest.network;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.player.PlayerMode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Server tells the client which modes are enabled for the local player.
 */
public record PlayerModeSyncPayload(Set<PlayerMode> modes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerModeSyncPayload> TYPE = new CustomPacketPayload.Type<>(AccessChestCommon.id("player_mode_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerModeSyncPayload> STREAM_CODEC = StreamCodec.composite(
            PlayerMode.STREAM_CODEC.apply(ByteBufCodecs.list(PlayerMode.values().length)),
            payload -> List.copyOf(payload.modes()),
            modes -> new PlayerModeSyncPayload(modes.isEmpty() ? EnumSet.noneOf(PlayerMode.class) : EnumSet.copyOf(modes)));

    @Override
    public CustomPacketPayload.Type<PlayerModeSyncPayload> type() {
        return TYPE;
    }
}
