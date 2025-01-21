package dev.saturn.addon.modules.Player;

import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;

public class FastEat extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Double setting for timer speed
    private final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder()
            .name("timer")
            .description("The timer speed to use while eating.")
            .defaultValue(2.0D)
            .min(1.0D)
            .sliderMax(10.0D)
            .build()
    );

    // Constructor for FastEat module
    public FastEat() {
        super(Categories.Player, "fast-eat", "Allows you to eat faster.");
    }

    @Override
    public void onDeactivate() {
        // Reset timer to default when deactivated
        ((Timer) Modules.get().get(Timer.class)).setOverride(1.0D);
        }
    }