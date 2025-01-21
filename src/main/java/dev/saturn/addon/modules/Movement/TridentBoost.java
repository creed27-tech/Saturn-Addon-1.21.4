package dev.saturn.addon.modules.Movement;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

public class TridentBoost extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Double setting for the boost multiplier
    private final Setting<Double> multiplier = sgGeneral.add(new DoubleSetting.Builder()
            .name("boost")
            .description("How much your velocity is multiplied by when using riptide.")
            .defaultValue(2.0D)
            .min(0.1D)
            .sliderRange(1.0D, 5.0D)
            .build()
    );

    // Boolean setting for allowing out of water riptide use
    private final Setting<Boolean> allowOutOfWater = sgGeneral.add(new BoolSetting.Builder()
            .name("out-of-water")
            .description("Whether riptide should work out of water.")
            .defaultValue(true)
            .build()
    );

    // Constructor for TridentBoost module
    public TridentBoost() {
        super(Categories.Movement, "trident-boost", "Boosts you when using riptide with a trident.");
    }

    // Getter for the multiplier, returns the default value if not active
    public double getMultiplier() {
        return this.isActive() ? multiplier.get() : 1.0D;
    }

    // Getter for allowing riptide out of water, returns false if not active
    public boolean allowOutOfWater() {
        return this.isActive() ? allowOutOfWater.get() : false;
    }
}