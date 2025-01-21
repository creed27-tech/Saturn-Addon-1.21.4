package dev.saturn.addon.modules.Render;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class CustomFov extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> fovVal = sgGeneral.add(new IntSetting.Builder()
            .name("value")
            .description("fov-value")
            .defaultValue(110)
            .min(1)
            .sliderMax(180)
            .build()
    );

    public CustomFov() {
        super(Categories.Render, "custom-fov", "Allows you to exceed minecraft's fov limit.");
    }
    @EventHandler
    private void onFov(GetFovEvent event) {
        event.fov = fovVal.get();
        if(fovVal.wasChanged()){
            fovVal.set(fovVal.get());
        }
    }
}