package dev.saturn.addon.modules.Misc;

import java.util.Random;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

public class CrazyCape extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance(); // Adjusted to access the Minecraft instance

    @EventHandler
    public void onTick(TickEvent.Post event) {
        Random random = new Random();
        int randomOffset = random.nextInt(5) - 1;

        // Adjust the player's cape position based on random offsets
        mc.player.prevCapeX = mc.player.prevCapeX + 0.7D + randomOffset; // Adjusted the field names for player position
        mc.player.prevCapeY = mc.player.prevCapeY + 0.5D + randomOffset;
        mc.player.prevCapeZ = mc.player.prevCapeY + 0.7D + randomOffset;
    }

    public CrazyCape() {
        super(Categories.Misc, "crazy-cape", "Breaks the Cape Physics");
    }
}