package dev.saturn.addon.modules.Combat;

import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class AutoLog extends Module {
    enum Mode {
        TotemPops,
        HP,
        Durability
    }

    public AutoLog() {
        super(Categories.Combat, "auto-log", "Automatically log in different events");
    }

    @EventHandler
    private void onEntity(EntityAddedEvent event) {

    }
}