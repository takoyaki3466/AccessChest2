package com.takoy3466.accesschest.client;

import com.takoy3466.accesschest.core.platform.Services;

/**
 * Platform services that only exist on the physical client. Only client code may touch this class.
 */
public final class ClientServices {

    public static final IClientNetworkPlatform NETWORK = Services.load(IClientNetworkPlatform.class);

    private ClientServices() {
    }
}
