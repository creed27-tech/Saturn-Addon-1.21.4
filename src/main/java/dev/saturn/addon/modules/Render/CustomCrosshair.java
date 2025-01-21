package dev.saturn.addon.modules.Render;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class CustomCrosshair extends Module {
    private final SettingGroup sgGeneral;

    public CustomCrosshair() {
        super(Categories.Render, "custom-crosshair", "Renders a customizable crosshair instead of the Minecraft one.");
        this.sgGeneral = this.settings.getDefaultGroup();
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
    }
}
