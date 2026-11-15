package com.takoy3466.accesschest;

import com.mojang.logging.LogUtils;
import com.takoy3466.accesschest.config.AccessChestConfig;
import com.takoy3466.accesschest.core.platform.Services;
import com.takoy3466.accesschest.init.CompatBlockEntities;
import com.takoy3466.accesschest.init.CompatBlocks;
import com.takoy3466.accesschest.init.CompatData;
import com.takoy3466.accesschest.init.CompatMenus;
import com.takoy3466.accesschest.init.CompatSerializers;
import com.takoy3466.accesschest.init.CompatTabs;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

/**
 * Loader independent entry point. Every loader calls {@link #init()} once from its own entry point.
 */
public final class AccessChestCommon {

    public static final String MOD_ID = "accesschest";
    public static final Logger LOGGER = LogUtils.getLogger();

    private AccessChestCommon() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * Loads the configuration and declares every registry object.
     * The order matters on loaders that register immediately (Fabric):
     * data components and blocks must exist before the objects that reference them.
     */
    public static void init() {
        AccessChestConfig.load(Services.PLATFORM.configDirectory());

        CompatData.init();
        CompatBlocks.init();
        CompatBlockEntities.init();
        CompatMenus.init();
        CompatSerializers.init();
        CompatTabs.init();
    }
}
