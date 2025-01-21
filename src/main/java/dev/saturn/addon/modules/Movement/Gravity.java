package dev.saturn.addon.modules.Movement;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Gravity extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Double> gravityStrength;

    public Gravity() {
        super(Categories.Movement, "gravity", "Changes gravity to moon gravity.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.gravityStrength = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Strength")).description("Gravity strength.")).defaultValue(0.0568000030517578D).min(1.0E-7D).sliderRange(0.0D, 1.0D).build());
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!MinecraftClient.getInstance().isPaused()) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                Vec3d velocity = player.getVelocity();
                IVec3d velocityInterface = (IVec3d) velocity;
                velocityInterface.meteor$set(velocity.x, velocity.y + gravityStrength.get(), velocity.z);
            }
        }
    }
}