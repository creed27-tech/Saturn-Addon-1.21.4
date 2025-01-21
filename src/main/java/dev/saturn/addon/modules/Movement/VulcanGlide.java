package dev.saturn.addon.modules.Movement;

import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class VulcanGlide extends Module {
    private static boolean wait = false;

    public VulcanGlide() {
        super(Categories.Movement, "vulcan-glide", "Yippee!!!");
    }

    @EventHandler
    public void onTick(Post e) {
        ClientPlayerEntity player = mc.player;
        if (player != null && player.fallDistance > 0.0F && player.getVelocity().y <= 0.0D) {
            wait = !wait;
            if (wait) {
                Vec3d velocity = player.getVelocity();
                player.setVelocity(new Vec3d(velocity.x, -0.1D, velocity.z));
            }
        }
    }
}