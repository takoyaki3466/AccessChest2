package com.takoy3466.accesschest.client;

import com.takoy3466.accesschest.network.TogglePlayerModePayload;
import com.takoy3466.accesschest.player.PlayerMode;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Key bindings that toggle the {@link PlayerMode}s.
 * The 1.7.10 defaults (X and L) are taken by vanilla 1.21.1, so K and J are used instead.
 */
public final class ModeToggleKeys {
    public static final String CATEGORY = "key.categories.accesschest";

    private static final List<ToggleKey> KEYS = List.of(
            new ToggleKey(PlayerMode.COLLECTIVE_TRANSFER, GLFW.GLFW_KEY_K),
            new ToggleKey(PlayerMode.AUTO_COLLECT, GLFW.GLFW_KEY_J));

    private ModeToggleKeys() {
    }

    public static List<KeyMapping> keyMappings() {
        return KEYS.stream().map(ToggleKey::mapping).toList();
    }

    /** Called once per client tick by every loader. */
    public static void onClientTick() {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        for (ToggleKey key : KEYS) {
            while (key.mapping().consumeClick()) {
                ClientServices.NETWORK.sendToServer(new TogglePlayerModePayload(key.mode()));
            }
        }
    }

    private record ToggleKey(PlayerMode mode, KeyMapping mapping) {
        ToggleKey(PlayerMode mode, int defaultKey) {
            this(mode, new KeyMapping(mode.translationKey(), defaultKey, CATEGORY));
        }
    }
}
