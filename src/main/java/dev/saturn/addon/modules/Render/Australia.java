package dev.saturn.addon.modules.Render;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Australia extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Integer setting for FOV with a slider
    private final Setting<Integer> fov = sgGeneral.add(new IntSetting.Builder()
            .name("FOV")
            .description("FOV to set back to on disable.")
            .defaultValue(110)
            .range(30, 110)
            .sliderRange(30, 110)
            .build()
    );

    // Constructor for Australia module
    public Australia() {
        super(Categories.Render, "australia", "Makes you look like you're in Australia.");
    }

    @Override
    public void onActivate() {
        // Set the FOV to 260 when the module is activated
        this.info("You are now in Australia.");
    }

    @Override
    public void onDeactivate() {
        // Set the FOV back to the original value on disable
        this.info("You have been deported from Australia.");
    }
}