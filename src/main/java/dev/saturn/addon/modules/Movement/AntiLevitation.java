package dev.saturn.addon.modules.Movement;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.network.ClientPlayerEntity;

public class AntiLevitation extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> applyGravity;

    public AntiLevitation() {
        super(Categories.Movement, "anti-levitation", "Prevents the levitation effect from working.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.applyGravity = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("gravity")).description("Applies gravity.")).defaultValue(false)).build());
    }

    public boolean isApplyGravity() {
        return this.applyGravity.get();
    }
}