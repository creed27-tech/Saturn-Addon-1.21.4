package dev.saturn.addon.modules.Combat;

import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.util.Hand;

public class HornAura extends Module {
    public HornAura() {
        super(Categories.Combat, "horn-aura", "Blows a horn automatically when someone gets into render distance. | Ported from BananaHack");
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (!(event.entity instanceof PlayerEntity)) return;
        int oldslot = mc.player.getInventory().selectedSlot;
        FindItemResult horn = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof GoatHornItem);
        if (!horn.found()) return;
        mc.player.getInventory().selectedSlot = horn.slot();
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.getInventory().selectedSlot = oldslot;
    }
}