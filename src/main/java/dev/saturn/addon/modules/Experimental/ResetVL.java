package dev.saturn.addon.modules.Experimental;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import net.minecraft.client.MinecraftClient;

public class ResetVL extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Double> timer;

    public ResetVL() {
        super(Saturn.Experimental, "ResetVL", "Tries to reset your violation level");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.timer = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Timer")).description("What timer to use")).defaultValue(1.1D).min(0.0D).sliderMax(5.0D).build());
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player != null && mc.world != null) {
            ((Timer) Modules.get().get(Timer.class)).setOverride(this.timer.get());
            if (mc.player.isSprinting()) {
                mc.player.setSprinting(false); // Disable sprinting to reset violation level
            }
        }
    }

    @Override
    public void onDeactivate() {
        ((Timer) Modules.get().get(Timer.class)).setOverride(1.0D); // Reset timer to default
    }
}