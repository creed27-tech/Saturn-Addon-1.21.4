package dev.saturn.addon.modules.Combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.entity.DamageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

public class KillAuraButBad extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> onJump;
    private final Setting<Double> fallRange;
    private final Setting<Double> range;
    private final Setting<Double> lookRange;
    private final ArrayList<Entity> targetList;

    public KillAuraButBad() {
        super(Categories.Combat, "kill-aura-but-bad", "A simple kill aura.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.onJump = this.sgGeneral.add(new BoolSetting.Builder().name("on-jump").description("Attack on jump.").defaultValue(true).build());
        this.fallRange = this.sgGeneral.add(new DoubleSetting.Builder().name("fall-range").description("Fall range.").defaultValue(0.4D).range(0.1D, 1.0D).sliderRange(0.1D, 1.0D)
                .visible(() -> this.onJump.get()).build());
        this.range = this.sgGeneral.add(new DoubleSetting.Builder().name("range").description("Range.").defaultValue(3.5D).range(1.0D, 10.0D).sliderRange(1.0D, 128.0D).build());
        this.lookRange = this.sgGeneral.add(new DoubleSetting.Builder().name("look-range").description("Look range.").defaultValue(5.0D).range(1.0D, 10.0D).sliderRange(1.0D, 128.0D).build());
        this.targetList = new ArrayList<>();
    }

    @Override
    public void onActivate() {
        this.targetList.clear();
    }

    @EventHandler
    private void onDamage(DamageEvent event) {
        }
    }