package dev.saturn.addon.modules.Movement;

import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

public class GrimVelocity extends Module {
    private int ticks = 0;

    public GrimVelocity() {
        super(Categories.Movement, "grim-velocity", "grim fail return v2 2024 real!1!!");
    }

    @EventHandler
    public void onTick(Pre e) {
        if (this.ticks > 0) {
            --this.ticks;
            if (this.ticks == 0) {
                for (int i = 0; i < 4; ++i) {
                }
            }
        }
    }

    @EventHandler
    public void onPacket(Receive e) {
        if (e.packet instanceof PlayerListS2CPacket ) {
            e.setCancelled(true);
            this.ticks = 1;
        }
    }
}