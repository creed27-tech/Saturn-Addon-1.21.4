package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.Saturn;
import dev.saturn.addon.utils.bed.port.other.TimerUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class BTQQuiver extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> lowMode = sgGeneral.add(new BoolSetting.Builder().name("low-mode").description("Works only w/ 1-2 types of arrows.").defaultValue(true).build());
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder().name("release-interval").defaultValue(4).min(0).max(7).sliderRange(0, 7).build());
    private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder().name("only-on-ground").defaultValue(true).build());
    private final Setting<Boolean> checkEffects = sgGeneral.add(new BoolSetting.Builder().name("check-existing-effects").description("Won't shoot you with effects you already have.").defaultValue(true).build());
    private final Setting<Boolean> silentBow = sgGeneral.add(new BoolSetting.Builder().name("silent-bow").defaultValue(true).build());

    private final List<Integer> arrowSlots = new ArrayList<>();
    TimerUtils afterTimer = new TimerUtils();
    int interval;
    int prevBowSlot;

    public BTQQuiver() {
        super(Saturn.PVP, "BT-QQuiver", "Pretty much prob wont work due to how big of a dumbass Mojang is to remove PotionUtil.");
    }

    @Override
    public void onActivate() {
        afterTimer.reset();
        interval = 0;
        prevBowSlot = -1;

        FindItemResult bow = InvUtils.find(Items.BOW);

        if (!bow.found()) {
            toggle();
            return;
        }

        if (silentBow.get() && !bow.isHotbar()) {
            prevBowSlot = bow.slot();
            InvUtils.move().from(bow.slot()).to(mc.player.getInventory().selectedSlot);
        } else if (!bow.isHotbar()) {
            ChatUtils.error("No bow in inventory found.");
            toggle();
            return;
        }

        mc.options.useKey.setPressed(false);
        mc.interactionManager.stopUsingItem(mc.player);

        if (!silentBow.get()) InvUtils.swap(bow.slot(), true);

        arrowSlots.clear();

        List<StatusEffect> usedEffects = new ArrayList<>();

        for (int i = mc.player.getInventory().size(); i > 0; i--) {
            if (i == mc.player.getInventory().selectedSlot) continue;

            ItemStack item = mc.player.getInventory().getStack(i);

            if (item.getItem() != Items.TIPPED_ARROW) continue;
        }
    }
}
