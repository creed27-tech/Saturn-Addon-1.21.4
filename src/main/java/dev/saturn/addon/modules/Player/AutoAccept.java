package dev.saturn.addon.modules.Player;

import java.util.ArrayList;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class AutoAccept extends Module {
    private final SettingGroup AASettings;
    private final Setting<Boolean> FriendsOnly;
    private final Setting<Integer> Delay;
    private final Setting<Boolean> Debug;

    public AutoAccept() {
        super(Categories.Player, "auto-accept", "Automatically accepts incoming teleport requests.");
        this.AASettings = this.settings.createGroup("Auto Accept Settings");
        this.FriendsOnly = this.AASettings.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Friends only")).description("Accepts only friends requests.")).defaultValue(true)).build());
        this.Delay = this.AASettings.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Delay")).defaultValue(0)).build());
        this.Debug = this.AASettings.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Debug")).description("Prints all incoming messages in console (raw format).")).defaultValue(false)).build());
        }
    }