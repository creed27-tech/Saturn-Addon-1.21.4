package dev.saturn.addon.modules.Client;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class UnHook extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    public UnHook() {
        super(Saturn.Client, "UnHook", "WIP | Uninjects Meteor Client from your Minecraft Instance | Ported from ThunderHack");
    }
}