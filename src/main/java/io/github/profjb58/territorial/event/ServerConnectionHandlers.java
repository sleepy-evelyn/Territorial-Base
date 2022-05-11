package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.Territorial;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class ServerConnectionHandlers {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register(ServerConnectionHandlers::onPlayerConnect);
    }

    private static void onPlayerConnect(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        Territorial.TEAMS_HANDLER.updateLastLogin(handler.getPlayer());
        Territorial.TEAMS_HANDLER.checkInactive();
    }
}