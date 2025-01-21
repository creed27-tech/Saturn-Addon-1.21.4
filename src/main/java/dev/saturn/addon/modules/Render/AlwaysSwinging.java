package dev.saturn.addon.modules.Render;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class AlwaysSwinging extends Module {

    public AlwaysSwinging() {
        super(Categories.Render, "cronic-swinger", "Swings your hand 24/7");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player.getMainHandStack().getItem().equals(0)) { // Ensure item in hand check
        }
    }
}