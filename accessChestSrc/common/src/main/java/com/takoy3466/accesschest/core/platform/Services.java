package com.takoy3466.accesschest.core.platform;

import java.util.ServiceLoader;

/**
 * Loads the loader specific implementations declared in {@code META-INF/services} of each loader module.
 * Client only services are loaded separately by {@code ClientServices} so that a dedicated server never touches them.
 */
public final class Services {

    public static final IPlatformInfo PLATFORM = load(IPlatformInfo.class);
    public static final IRegistryPlatform REGISTRY = load(IRegistryPlatform.class);
    public static final IMenuPlatform MENU = load(IMenuPlatform.class);
    public static final INetworkPlatform NETWORK = load(INetworkPlatform.class);

    private Services() {
    }

    public static <T> T load(Class<T> type) {
        return ServiceLoader.load(type)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No platform implementation found for " + type.getName()));
    }
}
