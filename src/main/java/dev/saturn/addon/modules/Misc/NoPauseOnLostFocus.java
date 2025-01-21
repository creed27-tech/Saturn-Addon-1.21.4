package dev.saturn.addon.modules.Misc;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

public class NoPauseOnLostFocus extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public NoPauseOnLostFocus() {
        super(Categories.Misc, "no-pause-on-lost-focus", "Allow alt+tab without pause");
        mc.options.pauseOnLostFocus = !this.isActive();
    }

    @Override
    public void onActivate() {
        mc.options.pauseOnLostFocus = false;
    }

    @Override
    public void onDeactivate() {
        mc.options.pauseOnLostFocus = true;
    }
}