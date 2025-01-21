package dev.saturn.addon.modules.Movement;

import dev.saturn.addon.events.bbc.PlayerMoveEvent;
import dev.saturn.addon.mixin.PlayerPositionLookS2CPacketAccessor;
import dev.saturn.addon.utils.bbc.math.TimerUtils;
import dev.saturn.addon.utils.bbc.player.PlayerHelper;
import dev.saturn.addon.utils.bbc.security.Initialization;
import dev.saturn.addon.utils.bbc.world.BlockHelper;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BBCSpeed extends Module {
    public BBCSpeed(){
        super(Categories.Movement, "Speedy", "Speed. | Ported from BBCWare");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgBoost = settings.createGroup("Boost");
    private final SettingGroup sgActions = settings.createGroup("Actions");
    private final SettingGroup sgMisc = settings.createGroup("Misc");

    private final Setting<Double> baseSpeedValue = sgGeneral.add(new DoubleSetting.Builder().name("base-speed").defaultValue(0.316).range(0, 3).sliderRange(0, 10).build());
    private final Setting<JumpSpeed> jumpMode = sgGeneral.add(new EnumSetting.Builder<JumpSpeed>().name("jump-mode").description("").defaultValue(JumpSpeed.Custom).build());
    public final Setting<Double> jumpValue = sgGeneral.add(new DoubleSetting.Builder().name("jump-height").defaultValue(0.3).min(0.01).sliderMin(0.01).sliderMax(3).visible(() -> jumpMode.get() == JumpSpeed.Custom).build());
    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>().name("mode").description("").defaultValue(Mode.Strafe).build());
    private final Setting<FrictionMode> frictionMode = sgGeneral.add(new EnumSetting.Builder<FrictionMode>().name("friction-mode").defaultValue(FrictionMode.Fast).build());
    public final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder().name("timer").description("Timer override.").defaultValue(1.101).min(0.01).sliderMin(0.01).sliderMax(10).build());
    public final Setting<Boolean> resetTimer = sgGeneral.add(new BoolSetting.Builder().name("reset-timer").defaultValue(true).build());
    public final Setting<Integer> setbackDelay = sgGeneral.add(new IntSetting.Builder().name("setback-delay").defaultValue(40).range(0, 60).build());
    private final Setting<Boolean> retain = sgGeneral.add(new BoolSetting.Builder().name("retain").defaultValue(false).build());
    private final Setting<Boolean> airStrafe = sgGeneral.add(new BoolSetting.Builder().name("air-strafe").defaultValue(true).build());
    private final Setting<Boolean> autoJump = sgGeneral.add(new BoolSetting.Builder().name("auto-jump").defaultValue(false).build());

    private final Setting<Boolean> boost = sgBoost.add(new BoolSetting.Builder().name("boost").defaultValue(true).build());
    public final Setting<Double> multiply = sgBoost.add(new DoubleSetting.Builder().name("Multiply").defaultValue(0.117D).min(0.1D).max(1D).build());
    public final Setting<Double> max = sgBoost.add(new DoubleSetting.Builder().name("Max").defaultValue(0.5D).min(0.166D).max(3D).build());


    private final Setting<Boolean> strictSprint = sgActions.add(new BoolSetting.Builder().name("strict-sprint").defaultValue(false).build());
    private final Setting<Boolean> strictJump = sgActions.add(new BoolSetting.Builder().name("strict-jump").defaultValue(false).build());
    private final Setting<Boolean> strictCollision = sgActions.add(new BoolSetting.Builder().name("strict-collision").defaultValue(false).build());

    private final Setting<Boolean> inLiquids = sgMisc.add(new BoolSetting.Builder().name("in-liquids").description("Uses speed when in lava or water.").defaultValue(false).build());
    private final Setting<Boolean> whenSneaking = sgMisc.add(new BoolSetting.Builder().name("when-sneaking").description("Uses speed when sneaking.").defaultValue(false).build());



    private double playerSpeed;
    private double latestMoveSpeed;
    private double boostSpeed;

    private int timersTick;
    private boolean accelerate;
    private boolean offsetPackets;

    private int strictTicks;

    private StrafeStage strafeStage = StrafeStage.Speed;
    private GroundStage groundStage = GroundStage.CheckSpace;

    private final TimerUtils setbackTimer = new TimerUtils();
    private final TimerUtils boostTimer = new TimerUtils();

    private int teleportId;

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {
        Modules.get().get(Timer.class).setOverride(Timer.OFF);
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        latestMoveSpeed = Math.sqrt(StrictMath.pow(mc.player.getX() - mc.player.prevX, 2) + StrictMath.pow(mc.player.getZ() - mc.player.prevZ, 2));
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event){
        event.setCancelled(true);
        if (!setbackTimer.passedTicks(setbackDelay.get())) return;
        if (!whenSneaking.get() && mc.player.isSneaking()) return;
        if (!inLiquids.get() && (mc.player.isTouchingWater() || mc.player.isInLava())) return;

        double baseSpeed = PlayerHelper.getBaseMoveSpeed(baseSpeedValue.get());

        if (strictSprint.get() && !mc.player.isSprinting()){
            if (mc.getNetworkHandler() != null){
                mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            }
        }


        switch (mode.get()) {
            case Strafe, StrafeStrict, StrafeLow, StrafeGround -> {
                // [Timer] //
                if (resetTimer.get()) {
                    timersTick++;
                    if (timersTick >= 5) {
                        Modules.get().get(Timer.class).setOverride(Timer.OFF);
                        timersTick = 0;
                    } else if (PlayerUtils.isMoving()) {
                        Modules.get().get(Timer.class).setOverride(timer.get());

                        // [slight boost] //
                        ((IVec3d) event.movement).meteor$setXZ(event.movement.x * 1.02, event.movement.z * 1.02);
                    }
                } else {
                    Modules.get().get(Timer.class).setOverride(PlayerUtils.isMoving() ? timer.get() : Timer.OFF);
                }


                if (PlayerUtils.isMoving()) {
                    if (mc.player.isOnGround()) strafeStage = StrafeStage.Start;

                    // [Check burrow] //
                    if (mode.get() == Mode.Strafe || mode.get() == Mode.StrafeLow) {
                        //if (BlockHelper.isSolid(mc.player.getBlockPos())) return;
                    }
                }

                if (strafeStage != StrafeStage.Collision || !PlayerUtils.isMoving()) {
                    // [start jumping] //

                    if (strafeStage == StrafeStage.Start) {
                        strafeStage = StrafeStage.Jump;
                        double jumpSpeed = 0.3999999463558197;

                        if (strictJump.get()) jumpSpeed = 0.42;

                        if (jumpMode.get() == JumpSpeed.Vanilla) {
                            if (mode.get() == Mode.StrafeLow) jumpSpeed = 0.31;
                            else jumpSpeed = 0.42;
                        } else if (mode.get() == Mode.StrafeLow) jumpSpeed = 0.27;

                        if (jumpMode.get() == JumpSpeed.Custom) jumpSpeed = jumpValue.get();

                        if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST))
                            jumpSpeed += (mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1;

                        // [jump] //
                        if (autoJump.get()) {
                            ((IVec3d) mc.player.getVelocity()).meteor$setY(jumpSpeed);
                            ((IVec3d) event.movement).meteor$setY(jumpSpeed);

                            double acceleration = 2.149;

                            if (mode.get() == Mode.Strafe) {
                                acceleration = 1.395;
                                if (accelerate) {
                                    acceleration = 1.6835;
                                }
                            }

                            playerSpeed *= acceleration;
                        }
                    } else if (strafeStage == StrafeStage.Jump) {
                        strafeStage = StrafeStage.Speed;
                        double scaledMoveSpeed = 3 * (latestMoveSpeed - baseSpeed);
                        playerSpeed = latestMoveSpeed - scaledMoveSpeed;
                        accelerate = !accelerate;
                    } else {
                        if (!mc.world.isSpaceEmpty(mc.player.getBoundingBox().offset(0.0, mc.player.getVelocity().y, 0.0)) || mc.player.verticalCollision) {
                            strafeStage = StrafeStage.Collision;

                            if (retain.get()) strafeStage = StrafeStage.Start;
                        }

                        double collisionSpeed = latestMoveSpeed - (latestMoveSpeed / 159);
                        if (strictCollision.get()) {
                            collisionSpeed = baseSpeed;
                            latestMoveSpeed = 0;
                        }
                        playerSpeed = collisionSpeed;
                    }
                } else {
                    if (mc.player.isOnGround()) strafeStage = StrafeStage.Start;
                    playerSpeed = baseSpeed * 1.38;
                }
            }
        }

    }

    // [Misc Util] //

    enum FrictionMode{
        Factor,
        Fast,
        Strict
    }

    enum JumpSpeed{
        NCP,
        Vanilla,
        Custom
    }

     enum Mode{
        Strafe, StrafeStrict, StrafeLow,
        StrafeGround, OnGround
    }

     enum StrafeStage{
        Collision,
        Start,
        Jump,
        Fall,
        Speed
    }

     enum GroundStage{
        Speed,
        FakeJump,
        CheckSpace}}