package dev.saturn.addon.modules.Movement;

import com.google.common.collect.Streams;
import java.util.stream.Stream;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

public class Parkour extends Module {
    public Parkour() {
        super(Categories.Movement, "parkour", "Automatically jumps at the edges of blocks.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player.isOnGround() && !mc.options.attackKey.isPressed()) {
            if (!mc.player.isSneaking() && !mc.options.sprintKey.isPressed()) {
                Box playerBox = mc.player.getBoundingBox();
                Box adjustedBox = playerBox.offset(0.0D, -0.5D, 0.0D).stretch(-0.001D, 0.0D, -0.001D);
                Stream<VoxelShape> blockCollisions = Streams.stream(mc.world.getCollisions(mc.player, adjustedBox));
                if (!blockCollisions.findAny().isPresent()) {
                    mc.player.jump();
                }
            }
        }
    }
}