package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient; // Import the correct MinecraftClient class

public class NoCaveCulling extends Module {
    private final MinecraftClient mc;

    public NoCaveCulling() {
        super(Categories.Render, "no-cave-culling", "Disables Minecraft's cave culling algorithm.");
        this.mc = MinecraftClient.getInstance(); // Initialize the MinecraftClient instance
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        mc.worldRenderer.reload(); // Update the world renderer again
    }
}