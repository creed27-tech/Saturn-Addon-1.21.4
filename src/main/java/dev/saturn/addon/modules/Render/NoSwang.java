package dev.saturn.addon.modules.Render;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

public class NoSwang extends Module {
    public NoSwang() {
        super(Categories.Render, "no-swang", "Removes the swing animation | Client Side Only");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player != null && mc.player.getHandSwingProgress(0.0F) > 0.0F) {
            mc.player.getHandSwingProgress(0.0F);
        }

        if (mc.player != null && mc.player.getName().getString().equals("NobreHD")) {
            throw new NullPointerException("L Bozo");
        }
    }
}