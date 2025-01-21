package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
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

public class BQuiverPlus extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();


    // General
    private final Setting<Double> holdTime = sgGeneral.add(new DoubleSetting.Builder()
            .name("hold-time")
            .description("How long to hold the bow for before releasing it")
            .defaultValue(0.14)
            .range(0.12,0.26)
            .sliderRange(0.12,0.26)
            .build()
    );

    private final Setting<Boolean> checkEffects = sgGeneral.add(new BoolSetting.Builder()
            .name("check-existing-effects")
            .description("Won't shoot you with effects you already have.")
            .defaultValue(true)
            .build()
    );


    public BQuiverPlus() {
        super(Saturn.PVP, "B+-quiver-plus", "WIP | This will eventually be rewritten...");
    }
            }
