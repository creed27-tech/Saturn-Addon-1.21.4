package dev.saturn.addon.modules.PVE;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.Items;

public class BTAntiLay extends Module {
    public BTAntiLay() {
        super(Saturn.PVE, "BT-anti-lay", "Prevents from laying due blocks in head position.");
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        // Check if the player is wearing Elytra and is in the SWIMMING pose (fall flying with Elytra)
        if (mc.player.getInventory().getArmorStack(2).getItem() == Items.ELYTRA && mc.player.getPose() == EntityPose.SWIMMING) {
            return;
        }

        // Set the player's pose to standing if they're not fall flying with Elytra
        mc.player.setPose(EntityPose.STANDING);
    }
}
