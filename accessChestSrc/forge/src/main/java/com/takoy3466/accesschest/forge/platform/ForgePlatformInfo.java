package com.takoy3466.accesschest.forge.platform;

import com.takoy3466.accesschest.core.platform.IPlatformInfo;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public final class ForgePlatformInfo implements IPlatformInfo {

    @Override
    public Path configDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
