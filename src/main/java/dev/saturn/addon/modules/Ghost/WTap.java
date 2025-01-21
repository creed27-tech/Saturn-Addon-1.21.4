package dev.saturn.addon.modules.Ghost;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

public final class WTap extends Module {
    public static int ticks;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Constructor for WTap module
    public WTap() {
        super(Saturn.Ghost, "WTap", "Makes people take more knockback");
    }

    @Override
    public void onActivate() {
        ticks = 0; // Reset the tick counter when the module is activated
    }

    // Event handler for the AttackEvent (simulated here as a placeholder method)
    public void onAttackEvent() {
        ticks = 0; // Reset the tick counter when an attack event occurs
    }

    // Event handler for the PreMotionEvent (simulated here as a placeholder method)
    public void onPreMotion() {
        ticks++;
        if (mc.player != null && mc.player.isSprinting()) {
            if (ticks == 2) {
                mc.player.setSprinting(false); // Disable sprinting on tick 2
            }

            if (ticks == 3) {
                mc.player.setSprinting(true); // Enable sprinting on tick 3
            }
        }
    }
}