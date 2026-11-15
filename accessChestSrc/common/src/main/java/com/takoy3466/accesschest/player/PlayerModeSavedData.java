package com.takoy3466.accesschest.player;

import com.takoy3466.accesschest.AccessChestCommon;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Enabled {@link PlayerMode}s per player, stored with the world ({@code accesschest_player_modes.dat}).
 * <pre>{ format: int, players: [{uuid: int[4], modes: ["auto_collect", ...]}] }</pre>
 */
public final class PlayerModeSavedData extends SavedData {
    private static final String DATA_NAME = AccessChestCommon.MOD_ID + "_player_modes";
    private static final int FORMAT_VERSION = 1;
    private static final String FORMAT_KEY = "format";
    private static final String PLAYERS_KEY = "players";
    private static final String UUID_KEY = "uuid";
    private static final String MODES_KEY = "modes";

    private final Map<UUID, EnumSet<PlayerMode>> modes = new HashMap<>();

    private PlayerModeSavedData() {
    }

    public static PlayerModeSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(PlayerModeSavedData::new, PlayerModeSavedData::load, null), DATA_NAME);
    }

    public boolean isEnabled(UUID player, PlayerMode mode) {
        EnumSet<PlayerMode> enabled = modes.get(player);
        return enabled != null && enabled.contains(mode);
    }

    public Set<PlayerMode> enabledModes(UUID player) {
        EnumSet<PlayerMode> enabled = modes.get(player);
        return enabled == null ? EnumSet.noneOf(PlayerMode.class) : EnumSet.copyOf(enabled);
    }

    /** @return whether the mode is enabled after toggling */
    public boolean toggle(UUID player, PlayerMode mode) {
        EnumSet<PlayerMode> enabled = modes.computeIfAbsent(player, uuid -> EnumSet.noneOf(PlayerMode.class));
        boolean nowEnabled = !enabled.contains(mode);
        if (nowEnabled) {
            enabled.add(mode);
        } else {
            enabled.remove(mode);
        }
        if (enabled.isEmpty()) {
            modes.remove(player);
        }
        setDirty();
        return nowEnabled;
    }

    private static PlayerModeSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        int format = tag.getInt(FORMAT_KEY);
        if (format > FORMAT_VERSION) {
            throw new IllegalStateException("Access Chest player mode format " + format + " is newer than supported format " + FORMAT_VERSION);
        }
        PlayerModeSavedData data = new PlayerModeSavedData();
        ListTag players = tag.getList(PLAYERS_KEY, Tag.TAG_COMPOUND);
        for (int i = 0; i < players.size(); i++) {
            CompoundTag entry = players.getCompound(i);
            if (!entry.hasUUID(UUID_KEY)) {
                continue;
            }
            EnumSet<PlayerMode> enabled = EnumSet.noneOf(PlayerMode.class);
            ListTag names = entry.getList(MODES_KEY, Tag.TAG_STRING);
            for (int j = 0; j < names.size(); j++) {
                PlayerMode mode = PlayerMode.CODEC.byName(names.getString(j));
                if (mode != null) {
                    enabled.add(mode);
                }
            }
            if (!enabled.isEmpty()) {
                data.modes.put(entry.getUUID(UUID_KEY), enabled);
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt(FORMAT_KEY, FORMAT_VERSION);
        ListTag players = new ListTag();
        modes.forEach((uuid, enabled) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID(UUID_KEY, uuid);
            ListTag names = new ListTag();
            enabled.forEach(mode -> names.add(StringTag.valueOf(mode.getSerializedName())));
            entry.put(MODES_KEY, names);
            players.add(entry);
        });
        tag.put(PLAYERS_KEY, players);
        return tag;
    }
}
