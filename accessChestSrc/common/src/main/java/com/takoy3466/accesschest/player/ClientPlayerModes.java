package com.takoy3466.accesschest.player;

import java.util.EnumSet;
import java.util.Set;

/**
 * The local player's modes as last sent by the server. Used for client side prediction of item use.
 * Contains no client only classes, so it is safe to reference from common code.
 */
public final class ClientPlayerModes {
    private static Set<PlayerMode> enabled = EnumSet.noneOf(PlayerMode.class);

    private ClientPlayerModes() {
    }

    public static boolean isEnabled(PlayerMode mode) {
        return enabled.contains(mode);
    }

    /** Called on the client thread when the server sends the current modes. */
    public static void update(Set<PlayerMode> modes) {
        enabled = modes.isEmpty() ? EnumSet.noneOf(PlayerMode.class) : EnumSet.copyOf(modes);
    }
}
