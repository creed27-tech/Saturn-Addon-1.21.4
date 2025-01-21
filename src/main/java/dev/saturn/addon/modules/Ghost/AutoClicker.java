package dev.saturn.addon.modules.Ghost;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.client.MinecraftClient;

import java.util.Random;

public class AutoClicker extends Module {
    private int timer;
    private final SettingGroup sgGeneral;
    private final Setting<Double> jitterAttack;
    private final Setting<Double> jitterUse;
    private final Setting<Integer> minDelay;
    private final Setting<Integer> maxDelay;

    public AutoClicker() {
        super(Saturn.Ghost, "auto-clicker", "Automatically clicks.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.jitterAttack = this.sgGeneral.add(new DoubleSetting.Builder()
                .name("Jitter Attack")
                .description("Gives you grandma hands when attacking.")
                .defaultValue(1.0D)
                .min(0.0D)
                .sliderRange(0.0D, 10.0D)
                .build());
        this.jitterUse = this.sgGeneral.add(new DoubleSetting.Builder()
                .name("Jitter Use")
                .description("Gives you grandma hands when using an item.")
                .defaultValue(1.0D)
                .min(0.0D)
                .sliderRange(0.0D, 10.0D)
                .build());
        this.minDelay = this.sgGeneral.add(new IntSetting.Builder()
                .name("Minimum Click Delay")
                .description("The shortest delay between clicks.")
                .defaultValue(2)
                .min(0)
                .sliderRange(0, 60)
                .build());
        this.maxDelay = this.sgGeneral.add(new IntSetting.Builder()
                .name("Maximum Click Delay")
                .description("The longest delay between clicks.")
                .defaultValue(4)
                .min(0)
                .sliderRange(0, 60)
                .build());
        }
    }