package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class MountHUD extends Module {
    public MountHUD() {
        super(Categories.Render, "mount-hud", "Display xp bar and hunger when riding.");
    }
}