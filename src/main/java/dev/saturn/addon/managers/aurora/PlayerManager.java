package dev.saturn.addon.managers.aurora;

import dev.saturn.addon.utils.aurora.RenderUtils;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

public class PlayerManager {
    public void init() {
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        RenderUtils.updateJello();
    }
}