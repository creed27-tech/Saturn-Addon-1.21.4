package dev.saturn.addon.modules.Movement;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import meteordevelopment.meteorclient.events.world.TickEvent;

public class TensorFly extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Boolean settings
    private final Setting<Boolean> stableY = sgGeneral.add(new BoolSetting.Builder()
            .name("stable-y")
            .description("Stabilizes your y position.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> rotationY = sgGeneral.add(new BoolSetting.Builder()
            .name("rotation-y")
            .description("Stabilizes your rotation.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> ignoreFluids = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-fluids")
            .description("Allows you to fly through fluids.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> alwaysMoving = sgGeneral.add(new BoolSetting.Builder()
            .name("always-moving")
            .description("Makes you always move forward.")
            .defaultValue(true)
            .build()
    );

    // Double setting for speed with a slider
    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
            .name("speed")
            .description("The speed to fly at.")
            .defaultValue(1.0)
            .min(0.0)
            .sliderMax(10.0)
            .build()
    );

    public TensorFly() {
        super(Categories.Movement, "tensor-fly", "Tensor Client's Elytra Fly.");
    }

    // Get the speed as a float, divided by 10 for scaling
    private float getSpeed() {
        return (float) (speed.get() / 10.0);
    }

    // Get the flying velocity vector
    private Vec3d getFlyingVelocity() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return Vec3d.ZERO;

        double x = player.getVelocity().x * getSpeed();
        double y = player.getVelocity().y;
        double z = player.getVelocity().z * getSpeed();

        // If rotation stabilization is enabled, scale the y velocity
        if (rotationY.get()) {
            y *= getSpeed();
        }

        // If stable Y is enabled, set y velocity to 0
        return new Vec3d(x, stableY.get() ? 0.0 : y, z);
    }

    // Check if any of the control buttons are being pressed
    private boolean areButtonsDown() {
        return MinecraftClient.getInstance().options.forwardKey.isPressed()
                || MinecraftClient.getInstance().options.backKey.isPressed()
                || MinecraftClient.getInstance().options.leftKey.isPressed()
                || MinecraftClient.getInstance().options.rightKey.isPressed()
                || (rotationY.get() && MinecraftClient.getInstance().options.jumpKey.isPressed());
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.isGliding()) {
            // Disable player gravity when flying
            player.setNoGravity(true);

            if (!areButtonsDown() && !alwaysMoving.get()) {
                // Stop movement if no buttons are pressed and alwaysMoving is disabled
                player.setVelocity(Vec3d.ZERO);
            } else {
                // Apply flying velocity
                player.setVelocity(getFlyingVelocity());
            }

            // If stable Y and rotation Y are both disabled, apply movement controls manually
            if (!stableY.get() && !rotationY.get()) {
                if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                    player.setVelocity(player.getVelocity().add(0.0, -getSpeed(), 0.0));
                } else if (MinecraftClient.getInstance().options.sneakKey.isPressed()) {
                    player.setVelocity(player.getVelocity().add(0.0, getSpeed(), 0.0));
                }
            }
        }
    }
}