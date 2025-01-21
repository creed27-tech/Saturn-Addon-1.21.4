package dev.saturn.addon.modules.Movement.elytrafly.modes;

import dev.saturn.addon.modules.Movement.elytrafly.ElytraFlyMode;
import dev.saturn.addon.modules.Movement.elytrafly.ElytraFlyModes;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Simple extends ElytraFlyMode {

    public Simple() {
        super(ElytraFlyModes.Simple);
    }

    boolean shouldFreeze() {
        return !(mc.options.forwardKey.isPressed() || mc.options.sneakKey.isPressed() || mc.options.jumpKey.isPressed());
    }

    double frozenMotionX, frozenMotionY, frozenMotionZ;
    boolean isFrozen = false;

    public void onPreTick(TickEvent.Pre event) {
        if(mc.player.isGliding()) {
            if(!shouldFreeze()) {
                if(isFrozen) {
                    isFrozen = false;
                    //mc.player.setVelocity(frozenMotionX,frozenMotionY,frozenMotionY);
                }

                double yaw = Math.toRadians(mc.player.getYaw());;

                float movementSpeed = (float) Math.sqrt(Math.pow(mc.player.getVelocity().x, 2) + Math.pow(mc.player.getVelocity().z, 2));

                    if(mc.options.forwardKey.isPressed() || mc.options.jumpKey.isPressed() || mc.options.sneakKey.isPressed()) {
                    }
                }
            }else {
                if(!isFrozen) {
                    isFrozen = true;
                    frozenMotionX = mc.player.getVelocity().x;
                    frozenMotionY = mc.player.getVelocity().y;
                    frozenMotionZ = mc.player.getVelocity().z;
                }
                mc.player.setVelocity(0,0,0);
            }
        }
    }