package dev.saturn.addon.modules.Movement;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.ShapeType;

public class AntiJebus extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<AntiJebus.Mode> mode;
    private final Setting<Double> jebusSpeed;

    public AntiJebus() {
        super(Categories.Movement, "anti-jebus", "WIP | Stops you from leaving the water, now with NCP bypass.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("How to Jebus")).defaultValue(AntiJebus.Mode.Vanilla)).build());
        this.jebusSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("jebus speed")).description(" ")).defaultValue(1.0D).min(0.1D).sliderMax(10.0D).visible(() -> {
            return this.mode.get() == AntiJebus.Mode.Vanilla;
        })).build());
    }

    public enum Mode {
        Vanilla("Vanilla"),
        NCP("NCP");

        private final String title;

        private Mode(String title) {
            this.title = title;
        }

        public String toString() {
            return this.title;
        }

        // $FF: synthetic method
        private static AntiJebus.Mode[] $values() {
            return new AntiJebus.Mode[]{Vanilla, NCP};
        }
    }
}