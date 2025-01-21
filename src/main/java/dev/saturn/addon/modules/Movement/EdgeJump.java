package dev.saturn.addon.modules.Movement;

import dev.saturn.addon.events.JumpEvent;
import com.google.common.collect.Streams;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.util.shape.VoxelShape;

import java.util.stream.Stream;

public class EdgeJump extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("When to perform the jump.")
            .defaultValue(Mode.Post)
            .build()
    );

    private final Setting<Double> distance = sgGeneral.add(new DoubleSetting.Builder()
            .name("min-edge-distance")
            .description("How far you at least have to be from the edge to jump.")
            .defaultValue(0.001)
            .min(0)
            .max(0.999)
            .sliderMin(0.001)
            .sliderMax(0.1)
            .build()
    );

    private final Setting<Double> minHeight = sgGeneral.add(new DoubleSetting.Builder()
            .name("min-height")
            .description("How high the distance between the point you are standing and the floor have to be to jump.")
            .defaultValue(0.5)
            .min(0.001)
            .sliderMin(0.001)
            .sliderMax(0.75)
            .build()
    );

    // Variables

    private boolean jumped;

    // Constructor

    public EdgeJump() {
        super(Categories.Movement, "edge-jump", "Automatically jumps at the edges of blocks.");
    }

    public enum Mode {
        Pre,
        Post
    }
}