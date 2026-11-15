package com.takoy3466.accesschest.neoforge.platform;

import com.takoy3466.accesschest.core.platform.IPlatformInfo;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public final class NeoPlatformInfo implements IPlatformInfo {

    @Override
    public Path configDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
