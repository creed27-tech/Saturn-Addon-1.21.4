package dev.saturn.addon.modules.Client;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Windows extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Integer> macroPos = sgGeneral.add(new IntSetting.Builder()
            .name("Macro Pos")
            .description("Macro Pos.")
            .min(1)
            .max(10)
            .build()
    );

    private final Setting<Integer> configPos = sgGeneral.add(new IntSetting.Builder()
            .name("Config Pos")
            .description("Config Pos.")
            .min(1)
            .max(10)
            .build()
    );

    private final Setting<Integer> friendPos = sgGeneral.add(new IntSetting.Builder()
            .name("Friend Pos")
            .description("Friend Pos.")
            .min(1)
            .max(10)
            .build()
    );

    private final Setting<Integer> waypointPos = sgGeneral.add(new IntSetting.Builder()
            .name("Waypoint Pos")
            .description("Waypoint Pos.")
            .min(1)
            .max(10)
            .build()
    );

    private final Setting<Integer> proxyPos = sgGeneral.add(new IntSetting.Builder()
            .name("Proxy Pos")
            .description("Proxy Pos.")
            .min(1)
            .max(10)
            .build()
    );

    public Windows() {
        super(Saturn.Client, "Windows", "WIP | Windows Settings for Meteor Client | Ported from ThunderHack");
    }
}