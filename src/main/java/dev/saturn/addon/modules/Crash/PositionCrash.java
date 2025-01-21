package dev.saturn.addon.modules.Crash;

import java.util.Objects;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class PositionCrash extends Module {
    private final Setting<Modes> packetMode;
    private final Setting<Integer> packetAmount;
    private final Setting<Boolean> autoDisable;
    private final Setting<Boolean> onGround;
    private boolean toggleSwitch = false;

    public PositionCrash() {
        super(Saturn.Crash, "position-crash", "Attempts to crash the server by sending broken position packets.");
        SettingGroup sgGeneral = this.settings.getDefaultGroup();

        this.packetMode = sgGeneral.add(new EnumSetting.Builder<Modes>()
                .name("mode")
                .description("Select the position crash mode.")
                .defaultValue(Modes.TWENTY_MILLION)
                .build());

        this.packetAmount = sgGeneral.add(new IntSetting.Builder()
                .name("amount")
                .description("Number of packets to send per tick.")
                .defaultValue(500)
                .min(1)
                .sliderMin(1)
                .sliderMax(10000)
                .build());

        this.autoDisable = sgGeneral.add(new BoolSetting.Builder()
                .name("auto-disable")
                .description("Disables the module on server kick.")
                .defaultValue(true)
                .build());

        this.onGround = sgGeneral.add(new BoolSetting.Builder()
                .name("on-ground")
                .description("Sets whether packets should be marked as onGround.")
                .defaultValue(true)
                .build());
    }

    public enum Modes {
        TWENTY_MILLION,
        INFINITY,
        TP,
        VELT,
        SWITCH
    }
}