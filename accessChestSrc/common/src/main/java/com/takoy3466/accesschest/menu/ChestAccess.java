package com.takoy3466.accesschest.menu;

import com.takoy3466.accesschest.storage.BoundedStorage;
import net.minecraft.world.entity.player.Player;

/**
 * Where an open chest menu gets its storage from (a placed block or a held item) and how long it stays usable.
 * Only used on the logical server.
 */
public interface ChestAccess {

    BoundedStorage storage();

    boolean stillValid(Player player);

    /** Called when a player opens a menu through this access. */
    default void startOpen(Player player) {
    }

    /** Called when a player closes a menu that was opened through this access. */
    default void stopOpen(Player player) {
    }
}
