package dev.saturn.addon.modules.Movement.noslow.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import dev.saturn.addon.modules.Movement.noslow.NoSlowMode;
import dev.saturn.addon.modules.Movement.noslow.NoSlowModes;
import dev.saturn.addon.modules.Movement.noslow.NoSlowPlus;
import net.minecraft.util.math.Vec3d;

public class Matrix extends NoSlowMode {
    public Matrix() {
        super(NoSlowModes.Matrix);
    }
    private int ticks = 0;

    @Override
    public void onActivate() {
        ticks = 0;
    }

    @Override
    public void onTickEventPre(TickEvent.Pre event) {
        if (mc.player.isUsingItem()) {
            if (mc.player.isOnGround()) {
                if (ticks % 2 == 0) {
                    float speed = 0.4f;
                    Vec3d vel = mc.player.getVelocity();
                    double x = vel.getX() * speed;
                    double z = vel.getZ() * speed;

                    mc.player.setVelocity(x, vel.getY(), z);
                }
            }
        }
        ticks++;
    }
}