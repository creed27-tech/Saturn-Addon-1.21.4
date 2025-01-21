package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class GrieferTracer extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<SettingColor> playersColor;

    public GrieferTracer() {
        super(Categories.Render, "griefer-tracer", "Tracers to fellow Griefers. Disabled on 2b2t.org.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.playersColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("players-colors")).description("The griefers color.")).defaultValue(new SettingColor(205, 205, 205, 127)).build());
            }
        }