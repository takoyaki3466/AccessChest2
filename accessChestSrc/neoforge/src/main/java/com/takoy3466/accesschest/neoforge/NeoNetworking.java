package com.takoy3466.accesschest.neoforge;

import com.takoy3466.accesschest.network.ClientboundEntry;
import com.takoy3466.accesschest.network.ModNetwork;
import com.takoy3466.accesschest.network.ServerboundEntry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Registers every payload of {@link ModNetwork}. NeoForge runs the handlers on the main thread by default.
 */
final class NeoNetworking {

    private NeoNetworking() {
    }

    static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Integer.toString(ModNetwork.PROTOCOL_VERSION));
        for (ServerboundEntry<?> entry : ModNetwork.SERVERBOUND) {
            registerServerbound(registrar, entry);
        }
        for (ClientboundEntry<?> entry : ModNetwork.CLIENTBOUND) {
            registerClientbound(registrar, entry);
        }
    }

    private static <T extends CustomPacketPayload> void registerServerbound(PayloadRegistrar registrar, ServerboundEntry<T> entry) {
        registrar.playToServer(entry.type(), entry.codec(), (payload, context) -> {
            if (context.player() instanceof ServerPlayer player) {
                entry.handler().handle(payload, player);
            }
        });
    }

    private static <T extends CustomPacketPayload> void registerClientbound(PayloadRegistrar registrar, ClientboundEntry<T> entry) {
        registrar.playToClient(entry.type(), entry.codec(), (payload, context) -> entry.handler().accept(payload));
    }
}
