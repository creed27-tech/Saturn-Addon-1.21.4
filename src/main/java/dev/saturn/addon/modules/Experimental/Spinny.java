package dev.saturn.addon.modules.Experimental;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

public class Spinny extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    @EventHandler
    public void onTick(TickEvent.Post event) {
        assert mc.player != null;

        mc.player.setYaw(mc.player.getYaw() + 10.0F);
        mc.player.setPitch(mc.player.getPitch() - 10.0F);
    }

    public Spinny() {
        super(Saturn.Experimental, "Spinny", "Vary Spinny.");
    }
}