package com.takoy3466.accesschest.network;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.player.PlayerMode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client pressed a mode toggle key.
 */
public record TogglePlayerModePayload(PlayerMode mode) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TogglePlayerModePayload> TYPE = new CustomPacketPayload.Type<>(AccessChestCommon.id("toggle_player_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TogglePlayerModePayload> STREAM_CODEC =
            StreamCodec.composite(PlayerMode.STREAM_CODEC, TogglePlayerModePayload::mode, TogglePlayerModePayload::new);

    @Override
    public CustomPacketPayload.Type<TogglePlayerModePayload> type() {
        return TYPE;
    }
}
