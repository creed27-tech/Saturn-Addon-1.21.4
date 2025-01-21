package dev.saturn.addon.modules.Fun;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class DankBobbing extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Float setting for controlling the bobbing motion
    private final Setting<Integer> motion = sgGeneral.add(new IntSetting.Builder()
            .name("Motion")
            .description("Controls the bobbing motion")
            .defaultValue(5)
            .min(1)
            .max(50)
            .build()
    );

    // Constructor for DankBobbing
    public DankBobbing() {
        super(Saturn.Fun, "dank-bobbing", "Adds more bobbing effect. | Ported from LiquidBounce");
    }

    // Getter method for motion value
    public float getMotion() {
        return motion.get();
    }
}