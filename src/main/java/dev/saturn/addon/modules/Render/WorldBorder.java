package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class WorldBorder extends Module {
    private final SettingGroup sgGeneral;

    public WorldBorder() {
        super(Categories.Render, "world-border", "Let you disable the worldborder client-side");
        this.sgGeneral = this.settings.getDefaultGroup();
        }
    }