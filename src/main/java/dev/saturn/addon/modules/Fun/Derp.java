package dev.saturn.addon.modules.Fun;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Derp extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public Derp() {
        super(Saturn.Fun, "derp", "WIP | Makes you look like you're derping around. | Ported from LiquidBounce");
    }
}