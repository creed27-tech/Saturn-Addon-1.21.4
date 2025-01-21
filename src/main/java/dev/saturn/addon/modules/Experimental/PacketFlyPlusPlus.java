package dev.saturn.addon.modules.Experimental;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;

public class PacketFlyPlusPlus extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgMovement = settings.createGroup("Movement");
    private final SettingGroup sgClient = settings.createGroup("Client");
    private final SettingGroup sgBypass = settings.createGroup("Bypass");
    private final SettingGroup sgBounds = settings.createGroup("Bounds");
    private final SettingGroup sgFlight = settings.createGroup("Flight");
    private final SettingGroup sgAntiKick = settings.createGroup("Anti Kick");
    private final SettingGroup sgSpeed = settings.createGroup("Speeds");
    private final SettingGroup sgAntiCheat = settings.createGroup("AntiCheat");
    private final SettingGroup sgPhase = settings.createGroup("Phase");
    private final SettingGroup sgKeybind = settings.createGroup("Keybinds");


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

    public enum BoundsUpdateMode {
        Active,
        Passive
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

    public enum MultiAxisMode {
        None,
        OnPhase,
        Always
    }

    public enum Type {
        FACTOR,
        SETBACK,
        FAST,
        SLOW,
        DESYNC,
        VECTOR
    }

    public enum Limit {
        None,
        Strict,
        Strong
    }

    public enum PacketFlyMode {
        PRESERVE,
        UP,
        DOWN,
        LIMITJITTER,
        BYPASS,
        OBSCURE
    }

    public enum PacketFlyBypass {
        NONE,
        DEFAULT,
        NCP
    }

    public enum PacketFlyType {
        FACTOR,
        SETBACK,
        FAST,
        SLOW,
        ELYTRA,
        DESYNC,
        GHOST
    }

    public enum PacketFlyPhase {
        NONE,
        VANILLA,
        NCP
    }


    // Movement

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

    private final Setting<Double> packetFlySpeed = sgGeneral.add(new DoubleSetting.Builder()
            .name("packet-fly-speed")
            .description("How fast to fly with Packet Fly")
            .defaultValue(0.1)
            .min(0)
            .sliderMax(50)
            .build()
    );

    private final Setting<Double> verticalPacketSpeed = sgGeneral.add(new DoubleSetting.Builder()
            .name("vertical-packet-speed")
            .description("How fast you should ascend with Packet Fly.")
            .defaultValue(1)
            .min(0)
            .sliderMax(20)
            .build()
    );

    private final Setting<Integer> tpConfirmCounter = sgGeneral.add(new IntSetting.Builder()
            .name("tp-confirm-counter")
            .description("How many instances of TeleportConfirm before a reset.")
            .defaultValue(20)
            .min(0)
            .sliderMax(1024)
            .build()
    );

    private final Setting<Integer> delay = sgAntiKick.add(new IntSetting.Builder()
            .name("delay")
            .description("The amount of delay, in ticks, between toggles in normal mode.")
            .defaultValue(80)
            .min(1)
            .max(5000)
            .sliderMax(200)
            .build()
    );

    private final Setting<Integer> offTime = sgAntiKick.add(new IntSetting.Builder()
            .name("off-time")
            .description("The amount of delay, in ticks, that Flight is toggled off for in normal mode.")
            .defaultValue(5)
            .min(1)
            .max(20)
            .sliderMax(10)
            .build()
    );

    private final Setting<PacketFlyMode> packetMode = sgGeneral.add(new EnumSetting.Builder<PacketFlyMode>().name("packet-mode").description("Which mode to use for sending packets.").defaultValue(PacketFlyMode.DOWN).build());
    private final Setting<PacketFlyBypass> bypass = sgGeneral.add(new EnumSetting.Builder<PacketFlyBypass>().name("bypass-mode").description("Which bypass mode to use.").defaultValue(PacketFlyBypass.NONE).build());
    private final Setting<Boolean> onlyOnMove = sgGeneral.add(new BoolSetting.Builder().name("only-on-move").description("Stops sending packets if you stand still").defaultValue(true).build());
    private final Setting<Boolean> stopOnGround = sgGeneral.add(new BoolSetting.Builder().name("stop-on-ground").description("Stops sending packets if you're on the ground").defaultValue(true).build());
    private final Setting<Boolean> strict = sgGeneral.add(new BoolSetting.Builder().name("strict").description("Can improve vertical movement").defaultValue(false).build());
    private final Setting<Boolean> bounds = sgGeneral.add(new BoolSetting.Builder().name("bounds").description("Set bounds for the packets sent").defaultValue(true).build());
    private final Setting<Boolean> multiAxis = sgGeneral.add(new BoolSetting.Builder().name("multi-axis").description("Allow sending packets in any direction").defaultValue(true).build());
    private final Setting<Boolean> autoToggle = sgGeneral.add(new BoolSetting.Builder().name("toggle").description("Automatically disable").defaultValue(true).build());


    // Client

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

    // Flight

    private final Setting<Type> type1 = sgFlight.add(new EnumSetting.Builder<Type>()
            .name("bypass-type")
            .description("How to bypass the servers anti-cheat.")
            .defaultValue(Type.FACTOR)
            .build()
    );

    private final Setting<MultiAxisMode> multiAxisMode = sgFlight.add(new EnumSetting.Builder<MultiAxisMode>()
            .name("multi-axis-mode")
            .description("How to bypass the servers anti-cheat.")
            .defaultValue(MultiAxisMode.None)
            .build()
    );

    private final Setting<Double> factor = sgFlight.add(new DoubleSetting.Builder()
            .name("factor")
            .description("Your flight factor.")
            .defaultValue(5)
            .min(0)
            .visible(() -> type1.get() == Type.FACTOR || type1.get() == Type.DESYNC)
            .build()
    );

    private final Setting<Integer> speed = sgFlight.add(new IntSetting.Builder()
            .name("speed")
            .description("How often to repeat the bypass.")
            .defaultValue(1)
            .min(1)
            .visible(() -> type1.get() != Type.FACTOR && type1.get() != Type.DESYNC)
            .build()
    );

    private final Setting<Boolean> phase = sgFlight.add(new BoolSetting.Builder()
            .name("phase")
            .description("Tries to phase when on older versions.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> phaseNoSlow = sgFlight.add(new BoolSetting.Builder()
            .name("phase-no-slow")
            .description("Allows for higher speeds when phasing.")
            .defaultValue(true)
            .visible(phase::get)
            .build()
    );

    // Bypass

    private final Setting<Boolean> spoofOnGround = sgBypass.add(new BoolSetting.Builder()
            .name("spoof-on-ground")
            .description("Sets you server-side on ground.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> sprint = sgBypass.add(new BoolSetting.Builder()
            .name("sprint")
            .description("Automatically sprints to allow higher speeds.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> noCollision = sgBypass.add(new BoolSetting.Builder()
            .name("no-collision")
            .description("Removes block collisions client-side.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> smallBounds = sgBypass.add(new BoolSetting.Builder()
            .name("small-bounds")
            .description("Uses smaller bounds when flying horizontally.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> bind = sgBypass.add(new BoolSetting.Builder()
            .name("bind")
            .description("Bounds for the player.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> ncp = sgBypass.add(new BoolSetting.Builder()
            .name("ncp")
            .description("Limits your movement to bypass NCP.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> onlyOnMove1 = sgBypass.add(new BoolSetting.Builder()
            .name("only-on-move")
            .description("Only sends packets if your moving.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> updateRotation = sgBypass.add(new BoolSetting.Builder()
            .name("update-rotation")
            .description("Updates your rotation while flying.")
            .defaultValue(true)
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

    // General

    private final Setting<Boolean> onGroundSpoof = sgGeneral.add(new BoolSetting.Builder()
            .name("On Ground Spoof")
            .description("Spoofs on ground.")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> onGround = sgGeneral.add(new BoolSetting.Builder()
            .name("On Ground")
            .description("Should we tell the server that you are on ground.")
            .defaultValue(false)
            .visible(onGroundSpoof::get)
            .build()
    );
    private final Setting<Integer> xzBound = sgGeneral.add(new IntSetting.Builder()
            .name("XZ Bound")
            .description("Bounds offset horizontally.")
            .defaultValue(1337)
            .sliderRange(-1337, 1337)
            .build()
    );
    private final Setting<Integer> yBound = sgGeneral.add(new IntSetting.Builder()
            .name("Y Bound")
            .description("Bounds offset vertically.")
            .defaultValue(0)
            .sliderRange(-1337, 1337)
            .build()
    );
    private final Setting<Boolean> strictVertical = sgGeneral.add(new BoolSetting.Builder()
            .name("Strict Vertical")
            .description("Doesn't move horizontally and vertically in the same packet.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Double> antiKickAmount = sgGeneral.add(new DoubleSetting.Builder()
            .name("Anti-Kick Multiplier")
            .description("Fall speed multiplier for antikick (0.04 blocks * multiplier).")
            .defaultValue(1)
            .sliderRange(0, 10)
            .build()
    );
    private final Setting<Integer> antiKickDelay = sgGeneral.add(new IntSetting.Builder()
            .name("Anti-Kick Delay")
            .description("Tick delay between moving anti kick packets.")
            .defaultValue(10)
            .min(1)
            .sliderRange(0, 100)
            .build()
    );
    private final Setting<Boolean> predictID = sgGeneral.add(new BoolSetting.Builder()
            .name("Predict ID")
            .description("Predicts the id of next rubberband.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> debugID = sgGeneral.add(new BoolSetting.Builder()
            .name("Debug ID")
            .description("Sends rubberband packet id in chat.")
            .defaultValue(false)
            .build()
    );

    // Bounds

    private final Setting<Double> bypassHeight = sgBounds.add(new DoubleSetting.Builder()
            .name("bypass-height")
            .description("How high to rubberband you.")
            .defaultValue(69420)
            .sliderMin(5)
            .sliderMax(50)
            .build()
    );

    private final Setting<Integer> digits = sgBounds.add(new IntSetting.Builder()
            .name("digits")
            .description("The number digits of the small bounds.")
            .defaultValue(7)
            .sliderMin(5)
            .sliderMax(9)
            .build()
    );

    private final Setting<Double> customX = sgBounds.add(new DoubleSetting.Builder()
            .name("custom-x")
            .description("The custom x factor.")
            .defaultValue(100)
            .sliderMin(-250)
            .sliderMax(250)
            .build()
    );

    private final Setting<Double> customY = sgBounds.add(new DoubleSetting.Builder()
            .name("custom-y")
            .description("The custom y factor.")
            .defaultValue(100)
            .sliderMin(-250)
            .sliderMax(250)
            .build()
    );

    private final Setting<Double> customZ = sgBounds.add(new DoubleSetting.Builder()
            .name("custom-z")
            .description("The custom z factor.")
            .defaultValue(100)
            .sliderMin(-250)
            .sliderMax(250)
            .build()
    );

    private final Setting<Double> randomMinX = sgBounds.add(new DoubleSetting.Builder()
            .name("random-min-x")
            .description("The minimum x.")
            .defaultValue(50)
            .sliderMin(-250)
            .sliderMax(250)
            .build()
    );

    private final Setting<Double> randomMinY = sgBounds.add(new DoubleSetting.Builder()
            .name("random-min-y")
            .description("The minimum y.")
            .defaultValue(50)
            .sliderMin(-250)
            .sliderMax(250)
            .build()
    );

    private final Setting<Double> randomMinZ = sgBounds.add(new DoubleSetting.Builder()
            .name("random-min-z")
            .description("The minimum z.")
            .defaultValue(50)
            .sliderMin(-250)
            .sliderMax(250)
            .build()
    );

    private final Setting<Double> randomMaxX = sgBounds.add(new DoubleSetting.Builder()
            .name("random-max-x")
            .description("The maximum x.")
            .defaultValue(50)
            .min(0.001)
            .sliderMin(0.1)
            .sliderMax(250)
            .build()
    );

    private final Setting<Double> randomMaxY = sgBounds.add(new DoubleSetting.Builder()
            .name("random-max-y")
            .description("The maximum y.")
            .defaultValue(50)
            .min(0.001)
            .sliderMin(0.1)
            .sliderMax(250)
            .build()
    );

    private final Setting<Double> randomMaxZ = sgBounds.add(new DoubleSetting.Builder()
            .name("random-max-z")
            .description("The maximum z.")
            .defaultValue(50)
            .min(0.001)
            .sliderMin(0.1)
            .sliderMax(250)
            .build()
    );

    private final Setting<Boolean> boundsOnGround = sgBounds.add(new BoolSetting.Builder()
            .name("bounds-on-ground")
            .description("Whether or not to send onground or offground bounds.")
            .defaultValue(true)
            .build()
    );

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

    private final Setting<BoundsUpdateMode> boundsUpdateMode = sgBounds.add(new EnumSetting.Builder<BoundsUpdateMode>()
            .name("bounds-mode")
            .description("How to update the bounds normal position.")
            .defaultValue(BoundsUpdateMode.Passive)
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

    private final Setting<Double> bypassHeight1 = sgBounds.add(new DoubleSetting.Builder()
            .name("bypass-height")
            .description("How high to rubberband you.")
            .defaultValue(69420)
            .sliderMin(5)
            .sliderMax(50)
            .visible(() -> boundsType.get() == BoundsType.Bypass || boundsType.get() == BoundsType.Alternative || boundsType.get() == BoundsType.Up || boundsType.get() == BoundsType.Down)
            .build()
    );

    private final Setting<Integer> digits1 = sgBounds.add(new IntSetting.Builder()
            .name("digits")
            .description("The number digits of the small bounds.")
            .defaultValue(7)
            .sliderMin(5)
            .sliderMax(9)
            .visible(() -> boundsType.get() == BoundsType.Small)
            .build()
    );

    private final Setting<Double> customX1 = sgBounds.add(new DoubleSetting.Builder()
            .name("custom-x")
            .description("The custom x factor.")
            .defaultValue(100)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Custom)
            .build()
    );

    private final Setting<Double> customY1 = sgBounds.add(new DoubleSetting.Builder()
            .name("custom-y")
            .description("The custom y factor.")
            .defaultValue(100)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Custom)
            .build()
    );

    private final Setting<Double> customZ1 = sgBounds.add(new DoubleSetting.Builder()
            .name("custom-z")
            .description("The custom z factor.")
            .defaultValue(100)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Custom)
            .build()
    );

    private final Setting<Double> randomMinX1 = sgBounds.add(new DoubleSetting.Builder()
            .name("random-min-x")
            .description("The minimum x.")
            .defaultValue(50)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );

    private final Setting<Double> randomMinY1 = sgBounds.add(new DoubleSetting.Builder()
            .name("random-min-y")
            .description("The minimum y.")
            .defaultValue(50)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );

    private final Setting<Double> randomMinZ1 = sgBounds.add(new DoubleSetting.Builder()
            .name("random-min-z")
            .description("The minimum z.")
            .defaultValue(50)
            .sliderMin(-250)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );

    private final Setting<Double> randomMaxX1 = sgBounds.add(new DoubleSetting.Builder()
            .name("random-max-x")
            .description("The maximum x.")
            .defaultValue(50)
            .min(0.001)
            .sliderMin(0.1)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );

    private final Setting<Double> randomMaxY1 = sgBounds.add(new DoubleSetting.Builder()
            .name("random-max-y")
            .description("The maximum y.")
            .defaultValue(50)
            .min(0.001)
            .sliderMin(0.1)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );

    private final Setting<Double> randomMaxZ1 = sgBounds.add(new DoubleSetting.Builder()
            .name("random-max-z")
            .description("The maximum z.")
            .defaultValue(50)
            .min(0.001)
            .sliderMin(0.1)
            .sliderMax(250)
            .visible(() -> boundsType.get() == BoundsType.Random)
            .build()
    );

    // Anti Kick
    private final Setting<Limit> limit = sgAntiKick.add(new EnumSetting.Builder<Limit>()
            .name("limit")
            .description("How to limit the flight to prevent you from getting kicked.")
            .defaultValue(Limit.Strict)
            .build()
    );

    private final Setting<Boolean> constrict = sgAntiKick.add(new BoolSetting.Builder()
            .name("constrict")
            .description("Whether or not to stricken your movement.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> jitter = sgAntiKick.add(new BoolSetting.Builder()
            .name("jitter")
            .description("Randomizes the movement ever so slightly.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> stopOnGround1 = sgAntiKick.add(new BoolSetting.Builder()
            .name("stop-on-ground")
            .description("Disables anti-kick when you are on ground.")
            .defaultValue(true)
            .build()
    );

    // Speeds
    private final Setting<Keybind> factorize = sgSpeed.add(new KeybindSetting.Builder().name("factorize").description("Quickly moves you on keybind").defaultValue(Keybind.fromKey(-1)).build());
    private final Setting<Boolean> boost = sgSpeed.add(new BoolSetting.Builder().name("boost").description("Boosts player motion").defaultValue(false).build());
    private final Setting<Double> speed1 = sgSpeed.add(new DoubleSetting.Builder().name("speed").description("How fast each packet moves you").defaultValue(1).min(0).build());
    private final Setting<Double> boostTimer = sgSpeed.add(new DoubleSetting.Builder().name("boost-timer").description("The timer for boost.").defaultValue(1.1).min(0).visible(boost::get).build());

    // Keybind
    private final Setting<Boolean> message = sgKeybind.add(new BoolSetting.Builder().name("keybind-message").description("Whether or not to send you a message when toggled a mode.").defaultValue(true).build());
    private final Setting<Keybind> toggleLimit = sgKeybind.add(new KeybindSetting.Builder().name("toggle-limit").description("Key to toggle PacketFlyLimit on or off.").defaultValue(Keybind.fromKey(-1)).build());
    private final Setting<Keybind> toggleAntiKick = sgKeybind.add(new KeybindSetting.Builder().name("toggle-anti-kick").description("Key to toggle anti kick on or off.").defaultValue(Keybind.fromKey(-1)).build());

    // Phase
    private final Setting<PacketFlyPhase> phase1 = sgPhase.add(new EnumSetting.Builder<PacketFlyPhase>().name("phase").description("Allow phasing through blocks").defaultValue(PacketFlyPhase.NONE).build());
    private final Setting<Boolean> noPhaseSlow = sgPhase.add(new BoolSetting.Builder().name("boost").description("Increase phase speed").defaultValue(true).build());
    private final Setting<Boolean> noCollision1 = sgPhase.add(new BoolSetting.Builder().name("no-collision").description("Disable block collision while phasing").defaultValue(false).build());

    private final Setting<Integer> phasePackets = sgPhase.add(new IntSetting.Builder()
            .name("Phase Packets")
            .description("How many packets to send every movement tick.")
            .defaultValue(1)
            .min(1)
            .sliderRange(0, 10)
            .build()
    );
    private final Setting<Double> phaseSpeed = sgPhase.add(new DoubleSetting.Builder()
            .name("Phase Speed")
            .description("Distance to travel each packet.")
            .defaultValue(0.062)
            .min(0)
            .sliderRange(0, 10)
            .build()
    );
    private final Setting<Boolean> phaseFastVertical = sgPhase.add(new BoolSetting.Builder()
            .name("Fast Vertical Phase")
            .description("Sends multiple packets every movement tick while going up.")
            .defaultValue(false)
            .build()
    );
    private final Setting<Double> phaseDownSpeed = sgPhase.add(new DoubleSetting.Builder()
            .name("Phase Down Speed")
            .description("How fast to phase down.")
            .defaultValue(0.062)
            .min(0)
            .sliderRange(0, 10)
            .build()
    );
    private final Setting<Double> phaseUpSpeed = sgPhase.add(new DoubleSetting.Builder()
            .name("Phase Up Speed")
            .description("How fast to phase up.")
            .defaultValue(0.062)
            .min(0)
            .sliderRange(0, 10)
            .build()
    );

    public PacketFlyPlusPlus() {
        super(Saturn.Experimental, "packet-fly-++", "Most OP Packet Fly!");
    }
}
