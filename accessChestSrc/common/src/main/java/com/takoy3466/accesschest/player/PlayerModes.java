package com.takoy3466.accesschest.player;

import com.takoy3466.accesschest.core.platform.Services;
import com.takoy3466.accesschest.network.PlayerModeSyncPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Reads, toggles and synchronises {@link PlayerMode}s. The server is authoritative; clients get a copy.
 */
public final class PlayerModes {
    private static final String MESSAGE_PREFIX_KEY = "message.accesschest.prefix";
    private static final String MESSAGE_TOGGLED_KEY = "message.accesschest.mode_toggled";
    private static final String ENABLED_KEY = "message.accesschest.enabled";
    private static final String DISABLED_KEY = "message.accesschest.disabled";

    private PlayerModes() {
    }

    /**
     * Works on both sides: servers read the saved data, clients the synchronised copy of the local player.
     */
    public static boolean isEnabled(Player player, PlayerMode mode) {
        if (player instanceof ServerPlayer serverPlayer) {
            return PlayerModeSavedData.get(serverPlayer.server).isEnabled(serverPlayer.getUUID(), mode);
        }
        return player.level().isClientSide && ClientPlayerModes.isEnabled(mode);
    }

    public static void toggle(ServerPlayer player, PlayerMode mode) {
        boolean enabled = PlayerModeSavedData.get(player.server).toggle(player.getUUID(), mode);
        sync(player);
        player.displayClientMessage(toggleMessage(mode, enabled), false);
    }

    public static void sync(ServerPlayer player) {
        Services.NETWORK.sendToPlayer(player, new PlayerModeSyncPayload(PlayerModeSavedData.get(player.server).enabledModes(player.getUUID())));
    }

    private static Component toggleMessage(PlayerMode mode, boolean enabled) {
        Component state = Component.translatable(enabled ? ENABLED_KEY : DISABLED_KEY);
        return Component.translatable(MESSAGE_PREFIX_KEY)
                .append(Component.translatable(MESSAGE_TOGGLED_KEY, Component.translatable(mode.translationKey()), state)
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
