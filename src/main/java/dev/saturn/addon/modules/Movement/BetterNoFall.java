package dev.saturn.addon.modules.Movement;

import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class BetterNoFall extends Module {
    public BetterNoFall() {
        super(Categories.Movement, "better-no-fall", "Universal No-Fall (works on almost any anticheat).");
    }

    @EventHandler
    private void onPreTick(SendMovementPacketsEvent.Post event) {
        if (!mc.player.isOnGround() && mc.player.fallDistance > 3f) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY() + Math.random()*0.000001, mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), false, false));
            mc.player.onLanding();
        }
    }
}