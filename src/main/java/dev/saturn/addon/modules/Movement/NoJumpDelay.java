package dev.saturn.addon.modules.Movement;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.LivingEntityAccessor;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.systems.modules.Module;

public class NoJumpDelay extends Module {

    public NoJumpDelay() {
        super(Categories.Movement, "no-jump-delay", "NoJumpDelay.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        ((LivingEntityAccessor) mc.player).setJumpCooldown(0);
    }
}