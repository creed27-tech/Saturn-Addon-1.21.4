package dev.saturn.addon.utils.reaper.misc;

import dev.saturn.addon.modules.Chat.AutoEz;
import dev.saturn.addon.modules.Combat.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.speed.Speed;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuleHelper {


    public static void queueEZ(PlayerEntity target) {
        Modules.get().get(AutoEz.class);
    }

    public static List<ReaperModule> combatModules = new ArrayList<>(Arrays.asList(
    ));

    public static void disableCombat() {
        combatModules.forEach(reaperModule -> {
            if (reaperModule.isActive()) reaperModule.toggle();
        });
    }

    public static <TargetStrafe> void disableMovement() {
        Speed speed = Modules.get().get(Speed.class);
        TargetStrafe targetStrafe;
        if (speed.isActive()) speed.toggle();
    }


    public static void disableCombat(Module parent) {
        for (Module m : combatModules) {
            if (m.equals(parent)) continue;
            if (m.isActive()) m.toggle();
        }
    }

}