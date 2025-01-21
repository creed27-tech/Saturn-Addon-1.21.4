package dev.saturn.addon.modules.Render;

import java.util.Iterator;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class NoobDetector extends Module {
    private boolean isTargetANoob = false;
    private PlayerEntity target;
    private int count;
    private final SettingGroup sgGeneral;
    private final SettingGroup sgRender;
    private final Setting<Double> damageThreshold;
    private final Setting<Boolean> chat;
    private final Setting<Boolean> render;
    private final Setting<ShapeMode> shapeMode;

    public NoobDetector() {
        super(Categories.Render, "noob-detector", "Checks if the Crystal Aura target is not burrowed and isn't surrounded.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.sgRender = this.settings.createGroup("Render");
        this.damageThreshold = this.sgGeneral.add(new DoubleSetting.Builder()
                .name("damage-threshold")
                .description("The threshold for Crystal Aura damage before Noob Detector begins rendering.")
                .defaultValue(6.0D)
                .min(0.0D)
                .sliderRange(0.0D, 40.0D)
                .build());
        this.chat = this.sgGeneral.add(new BoolSetting.Builder()
                .name("chat")
                .description("Notifies you in chat when the target is a noob.")
                .defaultValue(false)
                .build());
        this.render = this.sgGeneral.add(new BoolSetting.Builder()
                .name("render")
                .description("Renders a box around the target's feet if it is a noob.")
                .defaultValue(false)
                .build());
        this.shapeMode = this.sgRender.add(new EnumSetting.Builder<ShapeMode>()
                .name("shape-mode")
                .description("Determines how the shapes are rendered.")
                .defaultValue(ShapeMode.Lines)
                .build());
    }

    @Override
    public void onActivate() {
        this.isTargetANoob = false;
        this.target = null;
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (this.render.get() && this.isTargetANoob) {
        }
    }
}