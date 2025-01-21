package dev.saturn.addon.modules.Render;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.render.HeldItemRendererEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;

public class Animations extends Module {
    private final SettingGroup general;
    public final Setting<Mode> mode;

    public Animations() {
        super(Categories.Render, "animations", "Sword Animations like 1.8");
        this.general = this.settings.createGroup("General");
        this.mode = this.general.add(new EnumSetting.Builder<Mode>()
                .name("mode")
                .description("Animation mode")
                .defaultValue(Mode.None)
                .build());
    }

    public static enum Mode {
        Slide("Slide"),
        Dev("Dev"),
        None("None");

        private final String title;

        private Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return this.title;
        }
    }
}