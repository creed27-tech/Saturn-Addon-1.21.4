package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.utils.bananaplus.BPlusEntityUtils;
import dev.saturn.addon.utils.bananaplus.TimerUtils;
import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;

public class BReverseStepTimer extends Module {
    public enum Mode {
        Timer,
        Packet,
        Both
    }


    private final SettingGroup sgGeneral = settings.getDefaultGroup();


    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("Mode to use to bypass reverse step")
            .defaultValue(Mode.Packet)
            .build()
    );

    private final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder()
            .name("timer")
            .description("How fast to speed up timer for timer mode.")
            .min(0)
            .defaultValue(10)
            .visible(() -> mode.get() != Mode.Packet)
            .build()
    );

    private final Setting<Double> height = sgGeneral.add(new DoubleSetting.Builder()
            .name("height")
            .description("The maximum y height you are allowed to fall.")
            .min(0)
            .defaultValue(5)
            .build()
    );

    private final Setting<Boolean> webs = sgGeneral.add(new BoolSetting.Builder()
            .name("webs")
            .description("Will pull you even if there are webs below you.")
            .defaultValue(false)
            .build()
    );


    public BReverseStepTimer() {
        super(Saturn.PVP, "B+-reverse-step-timer", "Tries to bypass strict anticheats for reverse step.");
    }


    private int fallTicks;
    private final TimerUtils strictTimer = new TimerUtils();


    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (mc.player.isSubmergedInWater() || mc.player.isInLava() || mc.player.isGliding() || mc.player.isClimbing() || mc.player.getVehicle() != null) return;

        if (BPlusEntityUtils.isWebbed(mc.player) && !webs.get()) return;

        if (mc.options.jumpKey.isPressed() || mc.options.sneakKey.isPressed()) return;

        if (mc.player.isOnGround() && mc.world.isAir(BPlusEntityUtils.playerPos(mc.player).down())) fallTicks = 0;
        else fallTicks++;

        if (mc.player.fallDistance > 0 && (fallTicks > 0 && fallTicks < 10)) {
            double fallingBlock = mc.world.getBottomY();
            for (double y = mc.player.getY(); y > mc.world.getBottomY(); y -= 0.001) {
                if (mc.world.getBlockState(new BlockPos((int) mc.player.getX(), (int) y, (int) mc.player.getZ())).getBlock().getDefaultState().getCollisionShape(mc.world, new BlockPos(0, 0, 0)) == null) continue;

                fallingBlock = y;
                break;
            }

            if (fallingBlock >= mc.player.getY()) return;
            double fallHeight = mc.player.getY() - fallingBlock;
            if (fallHeight > height.get()) return;

            if (mode.get() != Mode.Timer) {
                if (mc.player.networkHandler != null && fallHeight > 0.5) {
                    double[] fallOffsets = {
                            0.07840000152, 0.23363200604, 0.46415937495, 0.76847620241,
                            1.14510670065, 1.59260459764, 2.10955254674, 2.69456154825,
                            3.34627038241
                    };

                    // Determine the number of packets to send based on fall height.
                    int maxPackets = fallHeight >= 3.5 ? 9 :
                            fallHeight >= 2.5 ? 7 :
                                    fallHeight >= 1.5 ? 5 : 4;

                    // Send packets up to the calculated number.
                    for (int i = 0; i < maxPackets; i++) {
                        double offset = fallOffsets[i];
                        mc.player.networkHandler.sendPacket(
                                new PlayerMoveC2SPacket.PositionAndOnGround(
                                        mc.player.getX(), mc.player.getY() - offset, mc.player.getZ(), false, false
                                )
                        );
                    }
                }
            }

                mc.player.setPosition(mc.player.getX(), fallingBlock + 0.1, mc.player.getZ());
                mc.player.setVelocity(0, 0, 0);
                strictTimer.reset();
                //  }
            }

            if (mode.get() != Mode.Packet) Modules.get().get(Timer.class).setOverride(timer.get());
        }
    }