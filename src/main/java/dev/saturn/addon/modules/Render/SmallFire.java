package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import org.joml.Vector3f;

public class SmallFire extends Module {
    private final SettingGroup sgScale;
    private final SettingGroup sgPosition;
    private final Setting<Double> scaleX;
    private final Setting<Double> scaleY;
    private final Setting<Double> scaleZ;
    private final Setting<Double> positionX;
    private final Setting<Double> positionY;
    private final Setting<Double> positionZ;

    public SmallFire() {
        super(Categories.Render, "small-fire", "Smalls fire on screen.");
        this.sgScale = this.settings.createGroup("Scale");
        this.sgPosition = this.settings.createGroup("Position");
        this.scaleX = this.sgScale.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale-x")).description("Zoom for fire on screen.")).defaultValue(0.0D).sliderRange(0.05D, 1.0D).build());
        this.scaleY = this.sgScale.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale-y")).description("Zoom for fire on screen.")).defaultValue(0.2D).sliderRange(0.05D, 1.0D).build());
        this.scaleZ = this.sgScale.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Scale Z")).description("Zoom for fire on screen.")).defaultValue(0.0D).sliderRange(0.05D, 1.0D).build());
        this.positionX = this.sgPosition.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("position-x")).description("Offset for fire on screen.")).defaultValue(0.0D).sliderRange(-10.0D, 10.0D).build());
        this.positionY = this.sgPosition.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("position-y")).description("Offset for fire on screen.")).defaultValue(-1.0D).sliderRange(-10.0D, 10.0D).build());
        this.positionZ = this.sgPosition.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Position Z")).description("Offset for fire on screen.")).defaultValue(0.0D).sliderRange(-10.0D, 10.0D).build());
    }

    public Vector3f getFireScale() {
        return new Vector3f(((Double)this.scaleX.get()).floatValue(), ((Double)this.scaleY.get()).floatValue(), ((Double)this.scaleZ.get()).floatValue());
    }

    public Vector3f getFirePosition() {
        return new Vector3f(((Double)this.scaleX.get()).floatValue(), ((Double)this.scaleY.get()).floatValue(), ((Double)this.scaleZ.get()).floatValue());
    }
}