package dev.saturn.addon.modules.Experimental;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.EntityVelocityUpdateS2CPacketAccessor;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;

public class NewVelocity extends Module {

    public NewVelocity() {
        super(Saturn.Experimental, "new-velocity", "Velocity that can bypass some anti-cheats.");
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof EntityVelocityUpdateS2CPacket packet) {
            if (packet.getEntityId() == mc.player.getId()) {
                double velX = ((double) packet.getVelocityX() / 8000.0D - mc.player.getVelocity().x) * 0.05D;
                double velZ = ((double) packet.getVelocityZ() / 8000.0D - mc.player.getVelocity().z) * 0.05D;

                // Adjust the velocity to bypass anti-cheats
                EntityVelocityUpdateS2CPacketAccessor packetAccessor = (EntityVelocityUpdateS2CPacketAccessor) packet;
                packetAccessor.setX((int) (velX * 8000.0D + mc.player.getVelocity().x * 8000.0D));
                packetAccessor.setZ((int) (velZ * 8000.0D + mc.player.getVelocity().z * 8000.0D));
            }
        }
    }
}