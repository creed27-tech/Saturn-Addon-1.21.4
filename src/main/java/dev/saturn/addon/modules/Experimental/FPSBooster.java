package dev.saturn.addon.modules.Experimental;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;

public class FPSBooster extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Integer setting for render distance
    private final Setting<Integer> renderDistance = sgGeneral.add(new IntSetting.Builder()
            .name("Render Distance")
            .description("Adjusts the render distance to boost FPS.")
            .defaultValue(8)
            .range(2, 32)
            .sliderRange(2, 32)
            .build()
    );

    // Enum setting for graphics quality
    private final Setting<GraphicsMode> graphicsMode = sgGeneral.add(new EnumSetting.Builder<GraphicsMode>()
            .name("Graphics Mode")
            .description("Select the graphics mode to improve performance.")
            .defaultValue(GraphicsMode.Fast)
            .build()
    );

    // Boolean setting to enable or disable particles
    private final Setting<Boolean> disableParticles = sgGeneral.add(new BoolSetting.Builder()
            .name("Disable Particles")
            .description("Turns off particles to improve FPS.")
            .defaultValue(true)
            .build()
    );

    // Constructor for FPSBooster
    public FPSBooster() {
        super(Saturn.Experimental, "FPS-booster", "WIP | Allows you to apply some tweaks to make your fps better");
    }

    // Enum definition for graphics modes
    public enum GraphicsMode {
        Fast,
        Fancy
    }

    @Override
    public void onActivate() {
    }

    @Override
    public void onDeactivate() {
    }
}