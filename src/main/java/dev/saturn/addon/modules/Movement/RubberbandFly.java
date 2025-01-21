package dev.saturn.addon.modules.Movement;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static dev.saturn.addon.utils.Vector.Utils.directionSpeed;
import static dev.saturn.addon.utils.Vector.Utils.isPlayerMoving;

public class RubberbandFly extends Module {
    private final SettingGroup sgBounds = settings.createGroup("Bounds");
    private final SettingGroup sgHorizontal = settings.createGroup("Horizontal");
    private final SettingGroup sgVertical = settings.createGroup("Vertical");
    private final SettingGroup sgOther = settings.createGroup("Other");
    private final SettingGroup sgAntiKick = settings.createGroup("Anti Kick");


    // Bounds


    private final Setting<BoundsType> boundsType = sgBounds.add(new EnumSetting.Builder<BoundsType>()
            .name("bounds-type")
            .description("How to rubberband you.")
            .defaultValue(BoundsType.Normal)
            .build()
    );

    private final Setting<BoundsMode> boundsMode = sgBounds.add(new EnumSetting.Builder<BoundsMode>()
            .name("bounds-mode")
            .description("What positions to spoof.")
            .defaultValue(BoundsMode.Both)
            .visible(() -> boundsType.get() == BoundsType.Small || boundsType.get() == BoundsType.Infinitive)
            .build()
    );

    private final Setting<CustomMode> customMode = sgBounds.add(new EnumSetting.Builder<CustomMode>()
            .name("bounds-mode")
            .description("How the positions are merged with your position.")
            .defaultValue(CustomMode.SmartRelative)
            .visible(() -> boundsType.get() == BoundsType.Custom || boundsType.get() == BoundsType.Random)
            .build()
    );

    private final Setting<Double> ceilRadius = sgBounds.add(new DoubleSetting.Builder()
            .name("ceil-radius")
            .description("The ceil's radius.")
            .defaultValue(25)
            .sliderMin(0.1)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Ceil)
            .build()
    );

    private final Setting<Double> bypassHeight = sgBounds.add(new DoubleSetting.Builder()
            .name("bypass-height")
            .description("How high to rubberband you.")
            .defaultValue(69420)
            .sliderMin(5)
            .sliderMax(70000)
            .visible(() -> boundsType.get() == BoundsType.Bypass || boundsType.get() == BoundsType.Alternative || boundsType.get() == BoundsType.Up || boundsType.get() == BoundsType.Down)
            .build()
    );

    private final Setting<Integer> digits = sgBounds.add(new IntSetting.Builder()
            .name("digits")
            .description("The number digits of the small bounds.")
            .defaultValue(7)
            .sliderMin(5)
            .sliderMax(9)
            .visible(() -> boundsType.get() == BoundsType.Small)
            .build()
    );

    private final Setting<Double> customX = sgBounds.add(new DoubleSetting.Builder()
            .name("custom-x")
            .description("The custom x factor.")
            .defaultValue(100)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Custom)
            .build()
    );

    private final Setting<Double> customY = sgBounds.add(new DoubleSetting.Builder()
            .name("custom-y")
            .description("The custom y factor.")
            .defaultValue(100)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Custom)
            .build()
    );

    private final Setting<Double> customZ = sgBounds.add(new DoubleSetting.Builder()
            .name("custom-z")
            .description("The custom z factor.")
            .defaultValue(100)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Custom)
            .build()
    );

    private final Setting<Double> randomMinX = sgBounds.add(new DoubleSetting.Builder()
            .name("random-min-x")
            .description("The minimum x.")
            .defaultValue(50)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );

    private final Setting<Double> randomMinY = sgBounds.add(new DoubleSetting.Builder()
            .name("random-min-y")
            .description("The minimum y.")
            .defaultValue(50)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );

    private final Setting<Double> randomMinZ = sgBounds.add(new DoubleSetting.Builder()
            .name("random-min-z")
            .description("The minimum z.")
            .defaultValue(50)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );

    private final Setting<Double> randomMaxX = sgBounds.add(new DoubleSetting.Builder()
            .name("random-max-x")
            .description("The maximum x.")
            .defaultValue(50)
            .min(0.001)
            .sliderMin(0.1)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );

    private final Setting<Double> randomMaxY = sgBounds.add(new DoubleSetting.Builder()
            .name("random-max-y")
            .description("The maximum y.")
            .defaultValue(50)
            .min(0.001)
            .sliderMin(0.1)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );

    private final Setting<Double> randomMaxZ = sgBounds.add(new DoubleSetting.Builder()
            .name("random-max-z")
            .description("The maximum z.")
            .defaultValue(50)
            .min(0.001)
            .sliderMin(0.1)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );


    // Horizontal


    private final Setting<HorizontalMode> horizontalMode = sgHorizontal.add(new EnumSetting.Builder<HorizontalMode>()
            .name("horizontal-mode")
            .description("How to fly horizontally on servers.")
            .defaultValue(HorizontalMode.Normal)
            .build()
    );

    private final Setting<Integer> packetDigits = sgHorizontal.add(new IntSetting.Builder()
            .name("packet-digits")
            .description("How far to.")
            .defaultValue(0)
            .sliderMin(5)
            .sliderMax(9)
            .noSlider()
            .visible(() -> horizontalMode.get() == HorizontalMode.Precise)
            .build()
    );

    private final Setting<Double> speed = sgHorizontal.add(new DoubleSetting.Builder()
            .name("speed")
            .description("At which speed to travel.")
            .defaultValue(1)
            .min(0.001)
            .sliderMin(0.001)
            .sliderMax(2)
            .visible(() -> horizontalMode.get() != HorizontalMode.Precise)
            .build()
    );

    private final Setting<Integer> horizontalClips = sgHorizontal.add(new IntSetting.Builder()
            .name("horizontal-clips")
            .description("How many times to clip when traveling.")
            .defaultValue(5)
            .min(1)
            .sliderMin(1)
            .sliderMax(5)
            .noSlider()
            .visible(() -> horizontalMode.get() == HorizontalMode.Precise)
            .build()
    );

    private final Setting<Double> horizontalStartDistance = sgHorizontal.add(new DoubleSetting.Builder()
            .name("horizontal-start-distance")
            .description("After what distance to start clipping.")
            .defaultValue(0.625)
            .min(0)
            .sliderMin(0)
            .sliderMax(1)
            .visible(() -> horizontalMode.get() != HorizontalMode.Clip && horizontalMode.get() != HorizontalMode.Precise)
            .build()
    );

    private final Setting<Integer> horizontalStartClip = sgHorizontal.add(new IntSetting.Builder()
            .name("horizontal-start-clip")
            .description("After what clip to start.")
            .defaultValue(1)
            .min(0)
            .sliderMin(0)
            .sliderMax(3)
            .noSlider()
            .visible(() -> horizontalMode.get() == HorizontalMode.Precise)
            .build()
    );

    private final Setting<Double> clipDistance = sgHorizontal.add(new DoubleSetting.Builder()
            .name("clip-distance")
            .description("How far to clip forwards per cycle.")
            .defaultValue(0.262)
            .min(0.001)
            .sliderMin(0.001)
            .sliderMax(0.312)
            .visible(() -> horizontalMode.get() != HorizontalMode.Clip)
            .build()
    );

    private final Setting<Double> slowClipDistance = sgHorizontal.add(new DoubleSetting.Builder()
            .name("slow-clip-distance")
            .description("How far to clip forwards when sneaking or sprinting per cycle.")
            .defaultValue(0.212)
            .min(0.001)
            .sliderMin(0.001)
            .sliderMax(0.3)
            .visible(() -> horizontalMode.get() != HorizontalMode.Clip)
            .build()
    );

    private final Setting<Boolean> acceptTeleport = sgHorizontal.add(new BoolSetting.Builder()
            .name("accept-teleport")
            .description("Sends a teleport confirm packet to the server after the bounds.")
            .defaultValue(true)
            .build()
    );


    // Vertical


    private final Setting<VerticalMode> verticalMode = sgVertical.add(new EnumSetting.Builder<VerticalMode>()
            .name("vertical-mode")
            .description("How to fly vertically on servers.")
            .defaultValue(VerticalMode.Simple)
            .build()
    );

    private final Setting<Double> verticalSpeed = sgVertical.add(new DoubleSetting.Builder()
            .name("vertical-speed")
            .description("Your vertical speed.")
            .defaultValue(0.060)
            .min(0.060)
            .sliderMin(0.060)
            .sliderMax(1.5)
            .visible(() -> verticalMode.get() == VerticalMode.Clip || verticalMode.get() == VerticalMode.Fast)
            .build()
    );

    private final Setting<Double> verticalStartDistance = sgVertical.add(new DoubleSetting.Builder()
            .name("vertical-start-distance")
            .description("After what distance to start clipping.")
            .defaultValue(0.015)
            .min(0)
            .sliderMin(0)
            .sliderMax(1)
            .visible(() -> verticalMode.get() == VerticalMode.Clip || verticalMode.get() == VerticalMode.Fast)
            .build()
    );

    private final Setting<Double> verticalClipDistance = sgVertical.add(new DoubleSetting.Builder()
            .name("vertical-clip-distance")
            .description("The clip distance when traveling vertically.")
            .defaultValue(0.062)
            .min(0.001)
            .sliderMin(0.001)
            .sliderMax(1.5)
            .visible(() -> verticalMode.get() != VerticalMode.None)
            .build()
    );


    // Other


    private final Setting<Boolean> spoofOnGround = sgOther.add(new BoolSetting.Builder()
            .name("spoof-on-ground")
            .description("Sets you server-side on ground.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> sprint = sgOther.add(new BoolSetting.Builder()
            .name("sprint")
            .description("Automatically sprints to allow higher speeds.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> noCollision = sgOther.add(new BoolSetting.Builder()
            .name("no-collision")
            .description("Removes block collisions client-side.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> smallBounds = sgOther.add(new BoolSetting.Builder()
            .name("small-bounds")
            .description("Uses smaller bounds when flying horizontally.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> updateRotation = sgOther.add(new BoolSetting.Builder()
            .name("update-rotation")
            .description("Updates your rotation while flying.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> updateVelocity = sgOther.add(new BoolSetting.Builder()
            .name("update-velocity")
            .description("Updates your client-side velocity.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Double> velocityMultiplier = sgOther.add(new DoubleSetting.Builder()
            .name("velocity-multiplier")
            .description("The value to multiply your client-side velocity with.")
            .defaultValue(1)
            .min(0)
            .sliderMin(0.1)
            .sliderMax(2.25)
            .visible(updateVelocity::get)
            .build()
    );

    // Anti Kick

    private final Setting<AntiFallMode> antiFallMode = sgAntiKick.add(new EnumSetting.Builder<AntiFallMode>()
            .name("anti-fall-mode")
            .description("How to vertically rubberband you once after enabling the module to reset your velocity.")
            .defaultValue(AntiFallMode.Up)
            .build()
    );

    private final Setting<Boolean> antiKickOnMove = sgAntiKick.add(new BoolSetting.Builder()
            .name("anti-kick-on-move")
            .description("Goes downwards after some time to prevent you from being kicked for flight.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> antiKickDelay = sgAntiKick.add(new IntSetting.Builder()
            .name("anti-kick-delay")
            .description("How long to wait before going down again.")
            .defaultValue(15)
            .min(2)
            .sliderMin(10)
            .sliderMax(25)
            .noSlider()
            .visible(antiKickOnMove::get)
            .build()
    );

    private final Setting<IdleMode> idleMode = sgAntiKick.add(new EnumSetting.Builder<IdleMode>()
            .name("idle-mode")
            .description("How to stop the server from kicking you.")
            .defaultValue(IdleMode.UpDown)
            .build()
    );

    private final Setting<Integer> idleDelay = sgAntiKick.add(new IntSetting.Builder()
            .name("idle-delay")
            .description("How long to wait before spoofing again.")
            .defaultValue(15)
            .min(0)
            .sliderMin(15)
            .sliderMax(25)
            .noSlider()
            .visible(() -> idleMode.get() == IdleMode.Down || idleMode.get() == IdleMode.UpDown)
            .build()
    );

    private final Setting<Integer> tpBackDelay = sgAntiKick.add(new IntSetting.Builder()
            .name("tp-back-delay")
            .description("How long to wait before going back up.")
            .defaultValue(5)
            .min(1)
            .sliderMin(15)
            .sliderMax(25)
            .noSlider()
            .visible(() -> idleMode.get() == IdleMode.UpDown)
            .build()
    );

    private final Setting<PosUpdateAction> posUpdateAction = sgAntiKick.add(new EnumSetting.Builder<PosUpdateAction>()
            .name("pos-update-action")
            .description("What to do when the server requires you to update your position.")
            .defaultValue(PosUpdateAction.AcceptRequired)
            .build()
    );

    private int antiKickTicks;
    private int idleTicks;
    private int tpTicks;

    private int ticksExisted;

    private int teleportID;
    private Vec3d teleportPos;

    private Vec3d velocity;

    private boolean up;
    private boolean updated;

    private float prevYaw;
    private float prevPitch;

    private final Random random = new Random();

    private List<PlayerMoveC2SPacket> packets;
    private List<TeleportConfirmC2SPacket> tpPackets;

    public RubberbandFly() {
        super(Categories.Movement, "rubberband-fly", "WIP | Fly with rubberbanding.");
    }

    public enum BoundsType {
        Up,
        Down,
        Zero,

        Ceil,

        Small,
        Alternative,
        Infinitive,

        Preserve,

        Obscure,
        Bypass,
        Random,
        Custom,
        Normal
    }

    public enum BoundsMode {
        Both,
        Horizontal,
        Vertical
    }

    public enum CustomMode {
        Polar,
        Relative,
        SmartPolar,
        SmartRelative,
        Multiply
    }

    public enum HorizontalMode {
        Clip,
        Normal,
        Precise,
        Fast
    }

    public enum VerticalMode {
        None,
        Simple,
        Clip,
        Fast
    }

    public enum AntiFallMode {
        None,
        Up,
        Down,
        UpPlus
    }

    public enum IdleMode {
        None,
        Down,
        UpDown,
        Semi
    }

    public enum PosUpdateAction {
        Ignore,
        Update,
        AcceptAll,
        IgnoreRotation,
        AcceptRequired
    }
}