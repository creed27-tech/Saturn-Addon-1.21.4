package dev.saturn.addon.modules.Funny;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

public class CreeperAlert extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public CreeperAlert() {
        super(Saturn.Funny, "creeper-alert", "Alerts when a creeper is in range.");
    }

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("range")
            .sliderRange(1, 20)
            .defaultValue(15)
            .build()
    );

    @EventHandler
    private void onTick(TickEvent.Post event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof CreeperEntity && entity.distanceTo(mc.player) <= range.get()) {
                mc.player.playSound(SoundEvents.ENTITY_CREEPER_PRIMED);
                break;
            }
        }
    }
}