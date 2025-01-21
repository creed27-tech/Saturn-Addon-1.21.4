package dev.saturn.addon.modules.Ghost;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class AutoSoupPlus extends Module {
    private final SettingGroup sgGeneral;
    public final Setting<Double> health;
    private int oldSlot;

    public AutoSoupPlus() {
        super(Saturn.Ghost, "auto-soup-plus", "Automatically eats soup when your health reaches a certain value.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.health = sgGeneral.add(new DoubleSetting.Builder()
                .name("health")
                .description("Eats a soup when your health reaches this value or falls below it.")
                .defaultValue(6.5D)
                .min(0.5D)
                .sliderMin(0.5D)
                .sliderMax(9.5D)
                .build()
        );
        this.oldSlot = -1;
        }
    }