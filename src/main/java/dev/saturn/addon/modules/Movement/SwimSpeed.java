package dev.saturn.addon.modules.Movement;

import java.util.Objects;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class SwimSpeed extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Double> velocityMultiplier;

    public SwimSpeed() {
        super(Categories.Movement, "swim-speed", "Speedy");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.velocityMultiplier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("velocity-multiplier")).description("The velocity multiplier.")).defaultValue(1.0D).min(0.0D).sliderMax(10.0D).build());
    }

    @EventHandler
    public boolean onTick(TickEvent.Post event) {
        if (mc.world != null && mc.player != null && mc.player.isSwimming()) {
        }

        return false;
    }
}