package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent.Post;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Sent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import dev.saturn.addon.enums.venomhack.SurroundBlocks;
import dev.saturn.addon.modules.VHModuleHelper;
import dev.saturn.addon.utils.venomhack.BlockUtils2;
import dev.saturn.addon.utils.venomhack.PlayerUtils2;
import dev.saturn.addon.utils.venomhack.RandUtils;
import dev.saturn.addon.utils.venomhack.UtilsPlus;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class VHBurrow extends VHModuleHelper {
    private static final int CHAT_ID = 972483264;
    private final SettingGroup sgExtra = this.group("Extra");
    public final Setting<Boolean> phoenixMode = this.setting("phoenix-mode", "Allows you to burrow with a block above your head. Works only on pa.", Boolean.valueOf(false), this.sgExtra);
    private final SettingGroup sgAutomation = this.group("Automation");
    private final Setting<SurroundBlocks> block = this.setting("block-to-use", "The block to use for Burrow.", SurroundBlocks.OBSIDIAN);
    private final Setting<Integer> minRubberbandHeight = this.setting("min-rubberband-height", "Maximum blocks to teleport up or down to cause a rubberband.", Integer.valueOf(4), -20.0, 20.0);
    private final Setting<Integer> maxRubberbandHeight = this.setting("max-rubberband-height", "Minimum blocks to teleport up or down to cause a rubberband.", Integer.valueOf(8), -20.0, 20.0);
    private final Setting<Integer> minBlacklist = this.setting("min-blacklist-height", "Start of the blacklisted area.", Integer.valueOf(2), -10.0, 10.0);
    private final Setting<Integer> maxBlacklist = this.setting("max-blacklist-height", "End of the blacklisted area.", Integer.valueOf(-2), -10.0, 10.0);
    private final Setting<Boolean> attackCrystals = this.setting("attack-crystals", "Whether to attack crystals that are in the way.", Boolean.valueOf(true), this.sgExtra);
    private final Setting<Boolean> center = this.setting("center", "Centers you to the middle of the block before burrowing.", Boolean.valueOf(true), this.sgExtra);
    private final Setting<Boolean> hardSnap = this.setting("Hard-Center", "Will align you at the exact center of your hole.", Boolean.valueOf(false), this.sgExtra, this.center::get);
    private final Setting<Boolean> strictDirections = this.setting("strict-directions", "Places only on visible sides.", Boolean.valueOf(false), this.sgExtra);
    private final Setting<Boolean> rotate = this.setting("rotate", "Faces the block you place server-side.", Boolean.valueOf(false), this.sgExtra);
    private final Setting<Integer> pitchStep = this.setting("max-yawstep", "How far to rotate with each step.", Integer.valueOf(180), this.sgExtra, this.rotate::get, 1.0, 180.0, 1, 180);
    private final Setting<Boolean> swing = this.setting("swing", "Whether to swing your hand client side or not.", Boolean.valueOf(false), this.sgExtra);
    private final Setting<Boolean> airPlace = this.setting("air-place", "Whether to place in midair or not.", Boolean.valueOf(true), this.sgExtra);
    private final List<Double> packetList = new ArrayList<>(6);    public final Setting<Boolean> autoTrap = this.setting("auto-burrow-trap", "Automatically activates burrow if someone is about to jump into your hole.", Boolean.valueOf(false), this.sgAutomation);
    private final Mutable mutable = new Mutable();
    private final StaticListener BURROW_LISTENER = new StaticListener();    public final Setting<Boolean> autoReburrow = this.setting("auto-burrow-replenish", "Automatically burrows you again when someone mines your burrow block.", Boolean.valueOf(false), this.sgAutomation);
    private Vec3d playerPos;
    private FindItemResult result;    public final Setting<Boolean> pauseEating = this.setting("pause-while-eating", "Will not automatically burrow you again when you are eating.", Boolean.valueOf(true), this.sgAutomation, () -> this.autoReburrow.get() || this.autoTrap.get());
    private BlockHitResult hitResult;
    private BlockPos playerBlock;
    private BlockState state;
    @Nullable
    private Entity entity;
    private float serverPitch;
    public VHBurrow() {
        super(Saturn.PVP, "VH-burrow", "Attempts to place a block inside of your feet.");
        this.handleListener(true);
    }

    @EventHandler
    private void onPacketSent(Sent event) {
        Packet<?> var3 = event.packet;
        if (var3 instanceof PlayerMoveC2SPacket packet) {
            this.serverPitch = packet.getPitch(this.serverPitch);
        }
    }

    @EventHandler(priority = -200)
    private void onLateTick(Pre event) {
        if (this.rotate.get() && this.playerBlock != null) {
            this.mc.player.networkHandler.sendPacket(new LookAndOnGround(this.mc.player.getYaw(), 90.0F, this.mc.player.isOnGround(), true));
            this.performBurrow(this.result, this.hitResult, this.playerBlock, this.state, this.entity);
            double targetPitch = Rotations.getPitch(this.hitResult.getPos());
            double pitchDist = Math.abs((double) this.serverPitch - targetPitch);
            if (pitchDist <= (double) this.pitchStep.get().intValue()) {
                Rotations.rotate(this.mc.player.getYaw(), targetPitch, 99, () -> this.performBurrow(this.result, this.hitResult, this.playerBlock, this.state, this.entity));
            } else {
                float pitch = this.serverPitch;
                if (pitchDist > (double) this.pitchStep.get().intValue()) {
                    pitchDist = this.pitchStep.get().intValue();
                }

                if (targetPitch > (double) this.serverPitch) {
                    pitch = (float) ((double) pitch + pitchDist);
                } else {
                    pitch = (float) ((double) pitch - pitchDist);
                }

                pitch = Math.min(Math.max(-90.0F, pitch), 90.0F);
                Rotations.rotate(this.mc.player.getYaw(), pitch);
            }
        }
    }

    private void performBurrow(FindItemResult result, BlockHitResult hitResult, BlockPos playerBlock, BlockState state, @Nullable Entity entity) {
        if (this.center.get()) {
            this.mc.player.setPosition(this.playerPos);
        }

        for (int i = 0; i < this.packetList.size(); ++i) {
            double height = this.packetList.get(i);
            if (i < this.packetList.size() - 1) {
                this.mc.player.networkHandler.sendPacket(new PositionAndOnGround(this.mc.player.getX(), this.mc.player.getY() + height, this.mc.player.getZ(), true, true));
            } else {
                if (entity != null) {
                    this.mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, this.mc.player.isSneaking()));
                    RandUtils.swing(this.swing.get(), RandUtils.hand(result));
                }

                if (state.getBlock() instanceof BedBlock && this.mc.world.getRegistryKey() != World.OVERWORLD) {
                    this.mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(playerBlock), Direction.UP, playerBlock, true), 0));
                } else if (state.getBlock().getHardness() == 0.0F && !state.isAir()) {
                    UtilsPlus.mine(playerBlock, false, false);
                }

                BlockUtils2.justPlace(result, hitResult, this.swing.get(), false, 0);
                this.mc.player.networkHandler.sendPacket(new PositionAndOnGround(this.mc.player.getX(), this.mc.player.getY() + height, this.mc.player.getZ(), true, true));
                this.toggle();
            }
        }
    }

    @Nullable
    private Entity getEntityInDaWay(VoxelShape placeShape, BlockPos playerBlock) {
        EndCrystalEntity crystal = null;

        try {
            for (Entity entity : this.mc.world.getOtherEntities(this.mc.player, placeShape.isEmpty() ? new Box(playerBlock) : placeShape.getBoundingBox().offset(playerBlock), Entity::isCollidable)) {
                if (!(entity instanceof EndCrystalEntity c)) {
                    return entity;
                }

                crystal = c;
            }
        } catch (ConcurrentModificationException var7) {
        }

        return crystal;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        event.renderer.box(this.mutable, new Color(255, 255, 255, 50), new Color(255, 255, 255, 255), ShapeMode.Both, 0);
    }

    private boolean collides(double yOffset) {
        return this.isCollides(this.mutable.set(this.playerPos.x + 0.3, this.playerPos.y + yOffset, this.playerPos.z + 0.3)) || this.isCollides(this.mutable.set(this.playerPos.x + 0.3, this.playerPos.y + yOffset, this.playerPos.z - 0.3)) || this.isCollides(this.mutable.set(this.playerPos.x - 0.3, this.playerPos.y + yOffset, this.playerPos.z + 0.3)) || this.isCollides(this.mutable.set(this.playerPos.x - 0.3, this.playerPos.y + yOffset, this.playerPos.z - 0.3));
    }

    private boolean isCollides(BlockPos pos) {
        return !this.mc.world.getBlockState(pos).getOutlineShape(this.mc.world, pos).isEmpty();
    }

    public void onDeactivate() {
        this.playerBlock = null;
        if (this.mc.player != null) {
            this.serverPitch = this.mc.player.getPitch();
        }
    }

    private void handleListener(boolean ignored) {
        if (!this.autoTrap.get() && !this.autoReburrow.get()) {
            MeteorClient.EVENT_BUS.unsubscribe(this.BURROW_LISTENER);
        } else {
            MeteorClient.EVENT_BUS.subscribe(this.BURROW_LISTENER);
        }
    }

    private class StaticListener {
        private BlockPos pos = null;
        private int delay;

        @EventHandler
        private void surroundListener(Post event) {
            --this.delay;
            if (UtilsPlus.isObbyBurrowed(VHBurrow.this.mc.player) && VHBurrow.this.mc.world.getBlockState(VHBurrow.this.mc.player.getBlockPos()).getBlock().getBlastResistance() > 600.0F) {
                this.pos = VHBurrow.this.mc.player.getBlockPos();
            } else if (!VHBurrow.this.isActive() && VHBurrow.this.mc.player.isOnGround() && !UtilsPlus.isBurrowed(VHBurrow.this.mc.player) && (!VHBurrow.this.pauseEating.get() || !VHBurrow.this.mc.player.isUsingItem( ))){
                if (VHBurrow.this.autoReburrow.get() && VHBurrow.this.mc.player.getBlockPos().equals(this.pos)) {
                    this.pos = null;
                    VHBurrow.this.toggle();
                } else {
                    if (this.delay <= 0 && VHBurrow.this.autoTrap.get() && UtilsPlus.isSurrounded(VHBurrow.this.mc.player, false, false)) {
                        for (PlayerEntity enemy : VHBurrow.this.mc.world.getPlayers()) {
                            if (!VHBurrow.this.mc.player.equals(enemy) && !(VHBurrow.this.mc.player.distanceTo(enemy) > 5.0F)) {
                                if (VHBurrow.this.mc.player.getBlockPos().equals(enemy.getBlockPos())) {
                                    break;
                                }

                                if (Friends.get().shouldAttack(enemy) && !UtilsPlus.isSurrounded(enemy, true, true) && VHBurrow.this.mc.player.getPos().add(0.0, 1.0, 0.0).distanceTo(enemy.getPos()) <= 2.0 && enemy.getY() > VHBurrow.this.mc.player.getY()) {
                                    VHBurrow.this.toggle();
                                    this.delay = 20;
                                    return;
                                }
                            }
                        }
                    }

                    this.pos = null;
                }
            }
        }
    }








}