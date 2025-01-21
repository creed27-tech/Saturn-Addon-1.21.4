package dev.saturn.addon.modules.Automation;

import dev.saturn.addon.utils.cLogUtils;
import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;

public class AutoVertical extends Module {
    public AutoVertical() {
        super(Saturn.Automation, "auto-vertical", "Fuck you advik (elytra fly's you to the moon)");
    }

    public void onActivate() {
        if (Modules.get().get(ElytraFly.class).isActive()) {
            mc.options.jumpKey.setPressed(true);
        } else {
            cLogUtils.sendMessage("ElytraFly needs to be enabled on mode: Vanilla", true);
            this.toggle();
        }

    }

    public void onDeactivate() {
        mc.options.jumpKey.setPressed(false);
    }
}