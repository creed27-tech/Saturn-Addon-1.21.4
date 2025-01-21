package dev.saturn.addon.modules.Movement.elytrafly.modes;

import dev.saturn.addon.modules.Movement.elytrafly.ElytraFlyMode;
import dev.saturn.addon.modules.Movement.elytrafly.ElytraFlyModes;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.util.math.Vec3d;

public class Boost extends ElytraFlyMode {

    public Boost() {
        super(ElytraFlyModes.Boost);
    }

    public void onPreTick(TickEvent.Pre event) {
        if(mc.player.isGliding()) {
            float movementSpeed = (float) Math.sqrt(Math.pow(mc.player.getVelocity().x, 2) + Math.pow(mc.player.getVelocity().z, 2));
            if(movementSpeed < 2.4) {
                double speed = mc.player.getPitch() > -10 ? 0.035f : 0.01 * Math.max(0, 25f - mc.player.getPitch()) / 25f;
                if(mc.player.getPitch() > 0) speed *= 1+Math.min(1.5,movementSpeed);
                if(mc.options.forwardKey.isPressed() || mc.options.jumpKey.isPressed()) {
                    Vec3d vec3d = mc.player.getRotationVector();
                    mc.player.setVelocity(mc.player.getVelocity().x + (vec3d.x*speed), mc.player.getVelocity().y, mc.player.getVelocity().z + (vec3d.z*speed));
                }
            }
        }
    }

}