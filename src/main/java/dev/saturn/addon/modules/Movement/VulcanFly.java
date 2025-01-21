package dev.saturn.addon.modules.Movement;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.player.PlayerEntity;

public class VulcanFly extends Module {
    private final SettingGroup generalSettings;
    private double startHeight;
    private final Setting<Double> clipHeight;
    private final Setting<Boolean> shouldClip;

    public VulcanFly() {
        super(Categories.Movement, "vulcan-fly", "Vulcan Fly Bypass");
        this.generalSettings = this.settings.getDefaultGroup();
        this.clipHeight = this.generalSettings.add(new DoubleSetting.Builder()
                .name("clip")
                .description("The clip amount.")
                .defaultValue(10.0D)
                .min(1.0D)
                .sliderRange(1.0D, 100.0D)
                .build());
        this.shouldClip = this.generalSettings.add(new BoolSetting.Builder()
                .name("Clip")
                .description("Should clip.")
                .defaultValue(false)
                .build());
    }

    @Override
    public void onActivate() {
        this.startHeight = mc.player.getY();
        if (shouldClip.get()) {
            mc.player.setPosition(mc.player.getX(), mc.player.getY() + clipHeight.get(), mc.player.getZ());
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player != null) {
            double targetHeight = this.startHeight - clipHeight.get();
            if (mc.player.getVelocity().y > 2.0F) {
                mc.player.setNoGravity(true);
                mc.player.setVelocity(mc.player.getVelocity().x, 0.0F, mc.player.getVelocity().z);
            }
        }
    }
}