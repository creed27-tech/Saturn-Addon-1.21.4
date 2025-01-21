package dev.saturn.addon.modules.Movement;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;

public class PacketFly extends Module {
    private final HashSet<PlayerMoveC2SPacket> packets = new HashSet<>();
    private final SettingGroup sgMovement = settings.createGroup("movement");
    private final SettingGroup sgClient = settings.createGroup("client");
    private final SettingGroup sgBypass = settings.createGroup("bypass");

    private final Setting<Double> horizontalSpeed = sgMovement.add(new DoubleSetting.Builder()
            .name("horizontal-speed")
            .description("Horizontal speed in blocks per second.")
            .defaultValue(5.2)
            .min(0.0)
            .max(20.0)
            .sliderMin(0.0)
            .sliderMax(20.0)
            .build()
    );

    private final Setting<Double> verticalSpeed = sgMovement.add(new DoubleSetting.Builder()
            .name("vertical-speed")
            .description("Vertical speed in blocks per second.")
            .defaultValue(1.24)
            .min(0.0)
            .max(20.0)
            .sliderMin(0.0)
            .sliderMax(20.0)
            .build()
    );

    private final Setting<Boolean> sendTeleport = sgMovement.add(new BoolSetting.Builder()
            .name("teleport")
            .description("Sends teleport packets.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> setYaw = sgClient.add(new BoolSetting.Builder()
            .name("set-yaw")
            .description("Sets yaw client side.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> setMove = sgClient.add(new BoolSetting.Builder()
            .name("set-move")
            .description("Sets movement client side.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> setPos = sgClient.add(new BoolSetting.Builder()
            .name("set-pos")
            .description("Sets position client side.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> setID = sgClient.add(new BoolSetting.Builder()
            .name("set-id")
            .description("Updates teleport id when a position packet is received.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> antiKick = sgBypass.add(new BoolSetting.Builder()
            .name("anti-kick")
            .description("Moves down occasionally to prevent kicks.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> downDelay = sgBypass.add(new IntSetting.Builder()
            .name("down-delay")
            .description("How often you move down when not flying upwards. (ticks)")
            .defaultValue(4)
            .sliderMin(1)
            .sliderMax(30)
            .min(1)
            .max(30)
            .build()
    );

    private final Setting<Integer> downDelayFlying = sgBypass.add(new IntSetting.Builder()
            .name("flying-down-delay")
            .description("How often you move down when flying upwards. (ticks)")
            .defaultValue(10)
            .sliderMin(1)
            .sliderMax(30)
            .min(1)
            .max(30)
            .build()
    );

    private final Setting<Boolean> invalidPacket = sgBypass.add(new BoolSetting.Builder()
            .name("invalid-packet")
            .description("Sends invalid movement packets.")
            .defaultValue(false)
            .build()
    );

    private int flightCounter = 0;
    private int teleportID = 0;

    public PacketFly() {

        super(Categories.Movement, "packet-fly", "Fly using packets.");
    }
}