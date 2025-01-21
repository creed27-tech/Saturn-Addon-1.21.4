package dev.saturn.addon.modules.Player;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class FastBow extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Double setting for the timer speed with a slider
    private final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder()
            .name("timer")
            .description("The timer speed to use while using a Bow.")
            .defaultValue(2.0)
            .min(1.0)
            .sliderMax(10.0)
            .build()
    );

    // Constructor for FastBow module
    public FastBow() {
        super(Categories.Player, "fast-bow", "Allows you to shoot faster.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && mc.player.getMainHandStack().getItem() == Items.BOW) {
            ((Timer) Modules.get().get(Timer.class)).setOverride(timer.get());
        } else {
            ((Timer) Modules.get().get(Timer.class)).setOverride(1.0);
        }
    }

    @Override
    public void onDeactivate() {
        // Reset the timer override to default when deactivated
        ((Timer) Modules.get().get(Timer.class)).setOverride(1.0);
    }
}