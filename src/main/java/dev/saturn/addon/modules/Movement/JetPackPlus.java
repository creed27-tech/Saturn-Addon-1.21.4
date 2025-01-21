package dev.saturn.addon.modules.Movement;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;

public class JetPackPlus extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public JetPackPlus() {
        super(Categories.Movement, "jetpack+", "Best Fly Hack 2023");
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        ClientPlayerEntity player = mc.player;

        if (mc.options.jumpKey.isPressed()) {
            player.jump();
        }

        if (mc.options.sprintKey.isPressed()) {
            player.setVelocity(player.getX(), -0.5D, player.getZ());
        }

        if (mc.options.attackKey.isPressed()) {
            player.setVelocity(player.getX(), 0.0D, player.getZ());
        }
    }
}