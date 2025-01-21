package dev.saturn.addon.modules.Player;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class AntiSpawnpoint extends Module {
    private final SettingGroup sgDefault;
    private final Setting<Boolean> fakeUse;

    public AntiSpawnpoint() {
        super(Categories.Player, "anti-spawnpoint", "Protects the player from losing the respawn point.");
        this.sgDefault = this.settings.getDefaultGroup();
        this.fakeUse = this.sgDefault.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fake-use")).description("Fake using the bed or anchor.")).defaultValue(true)).build());
                }
            }