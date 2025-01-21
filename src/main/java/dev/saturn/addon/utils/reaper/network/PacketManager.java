package dev.saturn.addon.utils.reaper.network;

import dev.saturn.addon.modules.Misc.RConfigTweaker;
import dev.saturn.addon.utils.reaper.player.Interactions;
import dev.saturn.addon.utils.reaper.services.TL;
import dev.saturn.addon.utils.reaper.world.BlockHelper;
import dev.saturn.addon.utils.reaper.world.CombatHelper;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Objects;

import static dev.saturn.addon.utils.reaper.world.RotationHelper.getDirection;
import static dev.saturn.addon.utils.venomhack.RandUtils.rotate;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PacketManager {

    // Packet Mining
    public static void sendStartDestroy(BlockPos pos) {sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));}
    public static void sendStopDestroy(BlockPos pos) {sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));}
    public static void sendAbortDestroy(BlockPos pos) {sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, pos, Direction.UP));}

    public static void startPacketMine(BlockPos pos, boolean sendSwing, boolean offhand) {
        if (pos == null) return;
        sendStartDestroy(pos);
        if (sendSwing) swingHand(offhand);
        sendStopDestroy(pos);
    }

    public static void finishPacketMine(BlockPos pos, boolean sendSwing, boolean offhand) {
        if (pos == null) return;
        sendAbortDestroy(pos);
        if (sendSwing) swingHand(offhand);
    }

    public static void abortPacketMine(BlockPos pos) {
        if (pos == null) return;
        sendAbortDestroy(pos);
    }

    public static void swingHand(boolean offhand) {
        if (offhand) {
            sendPacket(new HandSwingC2SPacket(Hand.OFF_HAND));
        } else {
            sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        }
    }


    // Inventory

    public static void updateSlot(int slot) {
        if (slot == -1) return;
        sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    // Sending Packets
    public static void sendPacket(Packet packet) {
        if (packet == null) return;
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(packet);
    }
    public static void sendMovementPacket(double x, double y, double z, boolean onGround) {
        if (x == -1 || y == -1 || z == -1) return;
        sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround, true)); // or false depending on your logic
    }
    public static void sendAttackPacket(Entity entity, boolean isSneaking) {
        if (entity == null) return;
        sendPacket(PlayerInteractEntityC2SPacket.attack(entity, isSneaking));
    }

    // Block Interactions


    public static ArrayList<BlockHitResult> pendingPlaces = new ArrayList<>();

    public static void sendInteract(Hand hand, FindItemResult item, BlockHitResult hitResult, boolean rotate, boolean packet) {
        if (hand == null || item == null || hitResult == null || !item.found() || pendingPlaces.contains(hitResult)) return;
        if (hand == Hand.MAIN_HAND && !Interactions.isHolding(item)) Interactions.setSlot(item.slot(), false);
        BlockPos pos = hitResult.getBlockPos();
        if (rotate) {
            if (CombatHelper.isInHole(mc.player)) rotate(pos, () -> sendInteract(hand, hitResult, packet));
            else Rotations.rotate(Rotations.getYaw(pos), Rotations.getPitch(pos), () -> sendInteract(hand, hitResult, packet));
        } else sendInteract(hand, hitResult, packet);
    }

    public static void sendInteract(Hand hand, BlockHitResult result, boolean packet) {
        TL.modules.execute(() -> {
            pendingPlaces.add(result);
            try {Thread.sleep(60);} catch (Exception ignored) {}
            pendingPlaces.remove(result);
        });
        if (packet) { // "packet placing" (sending interaction + swing packet directly)
            sendPacket(new PlayerInteractBlockC2SPacket(hand, result, 0));
            swingHand(hand == Hand.OFF_HAND);
        } else { // client placing
            mc.interactionManager.interactBlock(mc.player, hand, result);
            mc.player.swingHand(hand);
        }
    }
}