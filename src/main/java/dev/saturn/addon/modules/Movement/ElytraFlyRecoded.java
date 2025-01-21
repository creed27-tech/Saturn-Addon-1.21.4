package dev.saturn.addon.modules.Movement;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class ElytraFlyRecoded extends Module {
    private final SettingGroup sgGeneral;
    private double lockY;
    private boolean isFrozen;

    // Settings for module controls
    private final Setting<Integer> multiplier;
    private final Setting<Boolean> ylock;
    private final Setting<Integer> leftspeed;
    private final Setting<Integer> rightspeed;
    private final Setting<Integer> descendspeed;
    private final Setting<Boolean> spin;
    private final Setting<Mode> mode;

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public ElytraFlyRecoded() {
        super(Categories.Movement, "ElytraFlyRecoded", "Elytra Fly module for Meteor Client");

        this.sgGeneral = this.settings.getDefaultGroup();
        this.lockY = 0.0D;
        this.isFrozen = false;

        this.multiplier = sgGeneral.add(new IntSetting.Builder()
                .name("Speed")
                .description("Speed of the elytra")
                .defaultValue(1)
                .range(1, 10)
                .sliderRange(1, 10)
                .build());

        this.ylock = sgGeneral.add(new BoolSetting.Builder()
                .name("Lock Y")
                .description("Locks Y position to the Y you started at")
                .defaultValue(false)
                .build());

        this.leftspeed = sgGeneral.add(new IntSetting.Builder()
                .name("Left Speed")
                .description("Left movement speed")
                .defaultValue(1)
                .range(1, 10)
                .sliderRange(1, 10)
                .build());

        this.rightspeed = sgGeneral.add(new IntSetting.Builder()
                .name("Right Speed")
                .description("Right movement speed")
                .defaultValue(1)
                .range(1, 10)
                .sliderRange(1, 10)
                .build());

        this.descendspeed = sgGeneral.add(new IntSetting.Builder()
                .name("Descend Speed")
                .description("Speed of downward movement")
                .defaultValue(1)
                .range(1, 10)
                .sliderRange(1, 10)
                .build());

        this.spin = sgGeneral.add(new BoolSetting.Builder()
                .name("Spin")
                .description("Enable spinning while flying")
                .defaultValue(false)
                .build());

        this.mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
                .name("Mode")
                .description("Mode of flight behavior")
                .defaultValue(Mode.CursorLock)
                .build());
    }

    @EventHandler
    public void onActivate() {
        this.lockY = mc.player.getY();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player.isInLava()) {
            // Handle Elytra flight speed and movement
            if (mc.player.isGliding()) {
                handleElytraMovement();
            }

            // Handle Y-locking
            if (ylock.get()) {
                mc.player.setPosition(mc.player.getX(), lockY, mc.player.getZ());
            }

            // Handle spinning if enabled
            if (spin.get()) {
                mc.player.setYaw(mc.player.getYaw() + 2.0F);
            }
        }
    }

    private void handleElytraMovement() {
        boolean moveForward = mc.options.forwardKey.isPressed();
        boolean moveBackward = mc.options.backKey.isPressed();
        boolean moveLeft = mc.options.leftKey.isPressed();
        boolean moveRight = mc.options.rightKey.isPressed();

        double speed = multiplier.get();

        if (moveForward && (moveLeft || moveRight)) {
            float yaw = mc.player.getYaw();
            double radian = Math.toRadians(yaw + (moveLeft ? -45 : 45));
            double velX = -Math.sin(radian) * speed;
            double velZ = Math.cos(radian) * speed;
            mc.player.addVelocity(velX, 0.0D, velZ);
        } else if (moveForward) {
            Vec3d velocity = mc.player.getRotationVector().multiply(speed);
            mc.player.addVelocity(velocity.x, 0.0D, velocity.z);
        } else if (moveBackward && (moveLeft || moveRight)) {
            float yaw = mc.player.getYaw();
            double radian = Math.toRadians(yaw + (moveLeft ? 45 : -45));
            double velX = Math.sin(radian) * speed;
            double velZ = -Math.cos(radian) * speed;
            mc.player.addVelocity(velX, 0.0D, velZ);
        }

        if (mc.options.jumpKey.isPressed()) {
            mc.player.addVelocity(0.0D, -descendspeed.get(), 0.0D);
        }
    }

    public enum Mode {
        CursorLock("CursorLock"),
        YLock("Y-Lock");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return this.title;
        }
    }
}