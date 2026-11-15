package com.takoy3466.accesschest.fabric.platform;

import com.takoy3466.accesschest.core.platform.IPlatformInfo;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public final class FabricPlatformInfo implements IPlatformInfo {

    @Override
    public Path configDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
