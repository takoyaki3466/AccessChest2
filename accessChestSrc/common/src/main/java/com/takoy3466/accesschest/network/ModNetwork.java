package com.takoy3466.accesschest.network;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.config.AccessChestConfig;
import com.takoy3466.accesschest.menu.ServerChestSession;
import com.takoy3466.accesschest.menu.StorageChestMenu;
import com.takoy3466.accesschest.player.ClientPlayerModes;
import com.takoy3466.accesschest.player.PlayerModes;
import com.takoy3466.accesschest.storage.PriorityTable;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.Consumer;

/**
 * Declares every payload of the mod. Loader modules iterate {@link #SERVERBOUND} and {@link #CLIENTBOUND} and register
 * them with their own networking API, so the payload list and the handlers exist only once.
 * <p>
 * Server handlers never trust the client: they check that the player has one of this mod's menus open, that the menu
 * is still valid and that values are in range.
 */
public final class ModNetwork {
    /** Bump when payloads change incompatibly. */
    public static final int PROTOCOL_VERSION = 1;
    public static final String CHANNEL_NAME = "main";

    public static final List<ServerboundEntry<?>> SERVERBOUND = List.of(
            new ServerboundEntry<>(SetScrollPayload.TYPE, SetScrollPayload.STREAM_CODEC,
                    (payload, player) -> withOpenSession(player, session -> session.scrollTo(payload.row()))),
            new ServerboundEntry<>(SetFilterPayload.TYPE, SetFilterPayload.STREAM_CODEC,
                    (payload, player) -> withOpenSession(player, session -> session.applyFilter(payload.text()))),
            new ServerboundEntry<>(SortPayload.TYPE, SortPayload.STREAM_CODEC,
                    (payload, player) -> withOpenSession(player, ServerChestSession::sort)),
            new ServerboundEntry<>(AssignPriorityPayload.TYPE, AssignPriorityPayload.STREAM_CODEC, ModNetwork::handleAssignPriority),
            new ServerboundEntry<>(EjectPayload.TYPE, EjectPayload.STREAM_CODEC,
                    (payload, player) -> withOpenSession(player, session -> session.eject(player, AccessChestConfig.EJECT_LIMIT.get()))),
            new ServerboundEntry<>(StorePayload.TYPE, StorePayload.STREAM_CODEC,
                    (payload, player) -> withOpenSession(player, session -> session.store(player.getInventory(), payload.target()))),
            new ServerboundEntry<>(TogglePlayerModePayload.TYPE, TogglePlayerModePayload.STREAM_CODEC,
                    (payload, player) -> PlayerModes.toggle(player, payload.mode()))
    );

    public static final List<ClientboundEntry<?>> CLIENTBOUND = List.of(
            new ClientboundEntry<>(PlayerModeSyncPayload.TYPE, PlayerModeSyncPayload.STREAM_CODEC,
                    payload -> ClientPlayerModes.update(payload.modes()))
    );

    private ModNetwork() {
    }

    private static void handleAssignPriority(AssignPriorityPayload payload, ServerPlayer player) {
        if (!PriorityTable.isValid(payload.priority())) {
            AccessChestCommon.LOGGER.warn("Ignoring invalid sort priority {} from {}", payload.priority(), player.getGameProfile().getName());
            return;
        }
        withOpenSession(player, session -> session.assignPriority(payload.priority()));
    }

    /**
     * Runs {@code action} on the chest menu the player has open, if any. Afterwards the menu state id is advanced
     * because the window may now show different storage slots; clicks based on the old view are then fully resynced.
     */
    private static void withOpenSession(ServerPlayer player, Consumer<ServerChestSession> action) {
        if (player.containerMenu instanceof StorageChestMenu menu && menu.stillValid(player)) {
            menu.session().ifPresent(session -> {
                action.accept(session);
                menu.incrementStateId();
            });
        }
    }
}
