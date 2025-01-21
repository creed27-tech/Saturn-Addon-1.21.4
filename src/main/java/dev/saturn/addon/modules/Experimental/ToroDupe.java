package dev.saturn.addon.modules.Experimental;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class ToroDupe extends Module {
    private int timer = 0;
    private boolean drop = false;
    private boolean hasDismount = false;
    private BoatEntity boat = null;

    public ToroDupe() {
        super(Saturn.Experimental, "ToroDupe", "Toro Dupe by Colonizadores.");
    }

    private void dropAll() {
        if (mc.player instanceof PlayerEntity) {
            for (int i = 0; i < 27; ++i) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (!stack.isEmpty() && !stack.isEmpty()) {
                }
            }
        }
    }

    @EventHandler
    private void onTickChest(TickEvent.Post event) {
        if (!mc.player.isRiding()) {
            mc.options.sprintKey.setPressed(true); // Force sprint to prevent dismounts
        }

        Iterable<Entity> entities = mc.world.getEntities();
        if (entities != null) {
            if (!(mc.player instanceof PlayerEntity)) {
                entities.forEach(entity -> {
                    if (entity instanceof BoatEntity && mc.player.distanceTo(entity) < 5.0F && !entity.isSpectator() && mc.player.isOnGround() && !(mc.player instanceof PlayerEntity)) {
                    }
                });
            }
        }
    }

    @EventHandler
    private void onTickBoat(TickEvent.Post event) {
        Iterable<Entity> entities = mc.world.getEntities();
        entities.forEach(entity -> {
            if (!(mc.player.distanceTo(entity) > 5.0F)) {
                if (entity instanceof BoatEntity) {
                    this.boat = (BoatEntity) entity;
                }
            }
        });
    }

    @EventHandler
    private void onTickDrop(TickEvent.Post event) {
        if (this.boat != null && !this.boat.hasPassengers()) {
            this.hasDismount = true;
        }

        if (this.hasDismount) {
            ++this.timer;
            if (this.timer >= 10 && this.boat.hasPassengers()) {
                this.dropAll();
                this.timer = 0;
                this.hasDismount = false;
            }
        }
    }
}