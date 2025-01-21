package dev.saturn.addon.modules.World.timer.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import dev.saturn.addon.modules.World.timer.TimerMode;
import dev.saturn.addon.modules.World.timer.TimerModes;
import dev.saturn.addon.utils.meteorplus.algoritms.RandomUtils;

import static dev.saturn.addon.modules.World.timer.TimerPlus.*;

public class Vulcan extends TimerMode  {
    public Vulcan() {
        super(TimerModes.Vulcan);
    }

    @Override
    public void onDeactivate() {
        timer.setOverride(Timer.OFF);
    }

    @Override
    public void onTickEventPre(TickEvent.Pre event) {
        if (mc.player == null) return;
        if (rechargeTimer == 0) {
            if (workingTimer > RandomUtils.nextInt(8, 10)) {
                int delay = RandomUtils.nextInt(510, 720);
                rechargeTimer = delay;
                rechargeDelay = delay;
                workingTimer = 0;
                timer.setOverride(Timer.OFF);
            }
            else {
                if (settings.isActive()) {
                    if (settings.onlyInMove.get() && PlayerUtils.isMoving()) {
                        workingTimer++;
                        timer.setOverride(1.35);
                    }
                    else if (!settings.onlyInMove.get()) {
                        workingTimer++;
                        timer.setOverride(1.35);
                    }
                    else {
                        timer.setOverride(Timer.OFF);
                    }
                }
            }
        }
        else {
            rechargeTimer--;
            if (settings.isActive()) {
                timer.setOverride(Timer.OFF);
            }
            else {
                timer.setOverride(Timer.OFF);
            }
        }
    }
}