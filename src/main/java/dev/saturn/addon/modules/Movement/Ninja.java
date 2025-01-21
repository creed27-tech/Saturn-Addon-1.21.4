package dev.saturn.addon.modules.Movement;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class Ninja extends Module {
    boolean turn = true;

    public Ninja() {
        super(Categories.Movement, "Ninja", "SpeedBridges For You");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ClientWorld world = MinecraftClient.getInstance().world;

        if (world.getBlockState(player.getBlockPos()).isFullCube(world, player.getBlockPos())) {
            if (!player.isOnGround()) {
                return;
            }

            this.turn = true;
            MinecraftClient.getInstance().options.attackKey.setPressed(true);
        } else if (this.turn) {
            this.turn = false;
            MinecraftClient.getInstance().options.attackKey.setPressed(false);
        }
    }
}