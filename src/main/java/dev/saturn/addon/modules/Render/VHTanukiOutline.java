package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.util.math.Vec3d;
import dev.saturn.addon.modules.VHModuleHelper;

public class VHTanukiOutline extends VHModuleHelper {
    public final Setting<SettingColor> lineColor = this.setting("color", "The outline's color.", 255, 255, 255, 255);

    public VHTanukiOutline() {
        super(Categories.Render, "VH-tanuki-outline", "Block indicator from Tanuki. Credits to Walaryne.");
    }

    public Vec3d getColors() {
        return this.getDoubleVectorColor(this.lineColor);
    }

    public double getAlpha() {
        return (double) this.lineColor.get().a / 255.0;
    }

    private Vec3d getDoubleVectorColor(Setting<SettingColor> colorSetting) {
        return new Vec3d((double) colorSetting.get().r / 255.0, (double) colorSetting.get().g / 255.0, (double) colorSetting.get().b / 255.0);
    }
}