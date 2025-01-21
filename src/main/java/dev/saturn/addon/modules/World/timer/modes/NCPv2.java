package dev.saturn.addon.modules.World.timer.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import dev.saturn.addon.modules.World.timer.TimerMode;
import dev.saturn.addon.modules.World.timer.TimerModes;

import static dev.saturn.addon.modules.World.timer.TimerPlus.*;
import static dev.saturn.addon.modules.World.timer.TimerPlus.timerMultiplierOnRecharge;

public class NCPv2 extends TimerMode {
    public NCPv2() {
        super(TimerModes.Custom_v2);
    }

    @Override
    public void onDeactivate() {
        timer.setOverride(Timer.OFF);
    }

    @Override
    public void onTickEventPre(TickEvent.Pre event) {
        if (mc.player == null) return;
        if (rechargeTimer == 0) {
            if (workingTimer > workingDelay) {
                rechargeTimer = rechargeDelay;
                workingTimer = 0;
                timer.setOverride(Timer.OFF);
            }
            else {
                if (settings.isActive()) {
                    if (settings.onlyInMove.get() && PlayerUtils.isMoving()) {
                        workingTimer++;
                        if (mc.player.isOnGround()) {
                            timer.setOverride(timerMultiplier);
                        }
                        else {
                            timer.setOverride(timerMultiplierInAir);
                        }
                    }
                    else if (!settings.onlyInMove.get()) {
                        workingTimer++;
                        if (mc.player.isOnGround()) {
                            timer.setOverride(timerMultiplier);
                        }
                        else {
                            timer.setOverride(timerMultiplierInAir);
                        }
                    }
                    else {
                        timer.setOverride(timerMultiplierOnRecharge);
                    }
                }
            }
        }
        else {
            rechargeTimer--;
            if (settings.isActive()) {
                timer.setOverride(timerMultiplierOnRecharge);
            }
            else {
                timer.setOverride(Timer.OFF);
            }
        }
    }
}