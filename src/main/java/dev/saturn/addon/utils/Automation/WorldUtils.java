package dev.saturn.addon.utils.Automation;

import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class WorldUtils {
    public static List<BlockPos> getSphere(BlockPos centerPos, int radius, int height) {
        ArrayList<BlockPos> blocks = new ArrayList<>();

        for (int i = centerPos.getX() - radius; i < centerPos.getX() + radius; i++) {
            for (int j = centerPos.getY() - height; j < centerPos.getY() + height; j++) {
                for (int k = centerPos.getZ() - radius; k < centerPos.getZ() + radius; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (distanceBetween(centerPos, pos) <= radius && !blocks.contains(pos)) blocks.add(pos);
                }
            }
        }

        return blocks;
    }

    public static double distanceBetween(BlockPos pos1, BlockPos pos2) {
        double d = pos1.getX() - pos2.getX();
        double e = pos1.getY() - pos2.getY();
        double f = pos1.getZ() - pos2.getZ();
        return MathHelper.sqrt((float) (d * d + e * e + f * f));
    }

    public static boolean interact(BlockPos pos, FindItemResult findItemResult, boolean rotate) {
        if (!findItemResult.found());

        Runnable action = () -> {
            boolean wasSneaking = mc.player.isSneaking(); // Get current sneaking state
            mc.player.setSneaking(false);                // Temporarily disable sneaking

            InvUtils.swap(findItemResult.slot(), true);  // Swap to the item
            mc.interactionManager.interactBlock(
                    mc.player,
                    Hand.MAIN_HAND,
                    new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false)
            ); // Interact with the block

            mc.player.swingHand(Hand.MAIN_HAND);         // Swing the hand
            InvUtils.swapBack();                         // Swap back to the original item
            mc.player.setSneaking(wasSneaking);          // Restore sneaking state
        };

        if (rotate) {
            Rotations.rotate(
                    Rotations.getYaw(pos),
                    Rotations.getPitch(pos),
                    -100,
                    action
            ); // Rotate and execute the action
        } else {
            action.run(); // Execute the action without rotation
        }

        return true;
    }
}