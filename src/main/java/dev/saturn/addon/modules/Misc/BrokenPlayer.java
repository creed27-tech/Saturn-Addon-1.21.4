package dev.saturn.addon.modules.Misc;

import java.util.Random;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

public class BrokenPlayer extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance(); // Getting Minecraft instance

    @EventHandler
    public void onTick(TickEvent.Post event) {
        Random random = new Random();
        int randomOffset = random.nextInt(5) - 1; // Random offset between -1 and 3
        mc.player.setPos(mc.player.getX(), mc.player.getY() + 0.7F + randomOffset, mc.player.getZ()); // Adjusting Y position
        mc.player.setPos(mc.player.getX(), mc.player.getY(), mc.player.getZ() + 0.5F + randomOffset); // Adjusting Z position
    }

    public BrokenPlayer() {
        super(Categories.Misc, "broken-player", "Makes the Player Act Oddly");
    }
}