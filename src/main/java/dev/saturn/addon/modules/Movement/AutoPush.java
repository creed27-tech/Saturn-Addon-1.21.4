package dev.saturn.addon.modules.Movement;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AutoPush extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Double> distance;

    public AutoPush() {
        super(Categories.Movement, "auto-push", "Automatically push entities");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.distance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("delta")).description("the length of each teleport")).defaultValue(0.1D).sliderRange(0.0D, 1.0D).build());
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        Vec3d ppos = mc.player.getPos();
    }
}