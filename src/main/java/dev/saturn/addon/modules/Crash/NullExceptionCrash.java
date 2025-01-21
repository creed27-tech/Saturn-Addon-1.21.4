package dev.saturn.addon.modules.Crash;

import dev.saturn.addon.Saturn;
import it.unimi.dsi.fastutil.ints.IntList;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;

public class NullExceptionCrash extends Module {
    private final Setting<Modes> crashMode;
    private final Setting<Boolean> autoDisable;
    private final Setting<Boolean> onTick;

    public NullExceptionCrash() {
        super(Saturn.Crash, "null-exception-crash", "WIP | Attempts to crash the server by sending invalid packets.");
        SettingGroup sgGeneral = this.settings.getDefaultGroup();
        this.crashMode = sgGeneral.add(new EnumSetting.Builder<Modes>()
                .name("mode")
                .description("Which crash mode to use.")
                .defaultValue(Modes.EFFICIENT)
                .build());
        this.onTick = sgGeneral.add(new BoolSetting.Builder()
                .name("on-tick")
                .description("Sends the packets every tick.")
                .defaultValue(true)
                .build());
        this.autoDisable = sgGeneral.add(new BoolSetting.Builder()
                .name("auto-disable")
                .description("Disables the module on kick.")
                .defaultValue(true)
                .build());
    }

    public enum Modes {
        NEW,
        OLD,
        EFFICIENT
    }
}
