package dev.saturn.addon.modules.Render;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.client.render.FogShape;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Fog extends Module {
    public Fog() {
        super(Categories.Render, "Fog", "Customizable fog.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<FogShape> shape = sgGeneral.add(new EnumSetting.Builder<FogShape>()
            .name("Shape")
            .description("Fog shape.")
            .defaultValue(FogShape.SPHERE)
            .build()
    );
    public final Setting<Double> distance = sgGeneral.add(new DoubleSetting.Builder()
            .name("Distance")
            .description("How far away should the fog start rendering.")
            .defaultValue(25)
            .min(0)
            .sliderRange(0, 100)
            .build()
    );
    public final Setting<Integer> fading = sgGeneral.add(new IntSetting.Builder()
            .name("Fading")
            .description("How smoothly should the fog fade.")
            .defaultValue(25)
            .min(0)
            .sliderRange(0, 1000)
            .build()
    );
    public final Setting<Double> thickness = sgGeneral.add(new DoubleSetting.Builder()
            .name("Thickness")
            .description(".")
            .defaultValue(10)
            .range(1, 100)
            .sliderRange(1, 100)
            .build()
    );
    public final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
            .name("Color")
            .description("Color of the fog.")
            .defaultValue(new SettingColor(255, 0, 0, 255))
            .build()
    );
    }