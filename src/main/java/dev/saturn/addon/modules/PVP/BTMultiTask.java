package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.Saturn;
import dev.saturn.addon.events.bed.InteractEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class BTMultiTask extends Module {
    public BTMultiTask() {
        super(Saturn.PVP, "BT-multi-task", "Allows you to eat while mining a block.");
    }

    @EventHandler
    public void onInteractEvent(InteractEvent event) {
        event.usingItem = false;
    }
}
