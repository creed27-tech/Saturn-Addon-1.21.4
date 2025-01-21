package dev.saturn.addon.modules.Combat;

import java.util.Iterator;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Vec3d;

public class KillAuraButNOt extends Module {
    public KillAuraButNOt() {
        super(Categories.Combat, "serverside-killaura", "KillAura that only swings ServerSide and doesn't bypass anything");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player != null) {
            Iterator<Entity> var2 = mc.world.getEntities().iterator();

            while(var2.hasNext()) {
                Entity target = var2.next();
                if (target instanceof PlayerEntity && !target.equals(mc.player)) {
                    if (mc.player.squaredDistanceTo(target) > 0.1D && mc.player.squaredDistanceTo(target) < 25.0D) {
                        double dX = mc.player.getX() - target.getX();
                        double dY = mc.player.getY() - target.getY();
                        double dZ = mc.player.getZ() - target.getZ();
                        double DistanceXZ = Math.sqrt(dX * dX + dZ * dZ);
                        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + dY * dY);
                        double newYaw = Math.acos(dX / DistanceXZ) * 180.0D / Math.PI;
                        double newPitch = Math.acos(dY / -DistanceY) * 180.0D / Math.PI - 90.0D;
                        if (dZ < 0.0D) {
                            newYaw += Math.abs(180.0D - newYaw) * 2.0D;
                        }

                        newYaw += 90.0D;
                    }

                        mc.interactionManager.attackEntity(mc.player, target);
                    }
                }
            }
        }
    }