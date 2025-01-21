package dev.saturn.addon.modules.Player;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;

import java.util.Objects;

public class PacketEat extends Module {
    public PacketEat() {
        super(Categories.Player, "packet-eat", "Allows you to eat fast wih packets. | Ported from Aurora");
    }

    private Item PackEatItem;

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player != null && mc.player.isUsingItem()) {
            PackEatItem = mc.player.getActiveItem().getItem();
        }
    }

    @EventHandler
    public void onPacket(PacketEvent.Send event) {
        try {
            if (event.packet instanceof PlayerActionC2SPacket packet) {
                if (packet.getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM) {
                    // Check if PackEatItem is an instance of FoodItem
                    event.cancel();
                }
            }
        } finally {

        }
    }
}