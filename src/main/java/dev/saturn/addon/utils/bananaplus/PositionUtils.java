package dev.saturn.addon.utils.bananaplus;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PositionUtils {
    public static boolean allPlaced(List<BlockPos> posList) {
        for (BlockPos pos : posList) {
        }

        return true;
    }

    public static List<BlockPos> dynamicTopPos(PlayerEntity targetEntity, boolean predictMovement) {
        List<BlockPos> pos = new ArrayList<>();

        Box box = targetEntity.getBoundingBox().contract(0.001, 0, 0.001);
        if (predictMovement) {
            Vec3d v = targetEntity.getVelocity();
            box.offset(v.x, v.y, v.z);
        }
        return pos;
    }

    public static List<BlockPos> dynamicHeadPos(PlayerEntity targetEntity, boolean predictMovement) {
        List<BlockPos> pos = new ArrayList<>();
        Box box = targetEntity.getBoundingBox().contract(0.001, 0, 0.001);
        if (predictMovement) {
            Vec3d v = targetEntity.getVelocity();
            box.offset(v.x, v.y, v.z);
        }

        return pos;
    }

    public static List<BlockPos> dynamicBottomPos(PlayerEntity targetEntity, boolean predictMovement) {
        List<BlockPos> pos = new ArrayList<>();
        // First we get the player's hitbox and contract is by 0.001 because minecraft is weird
        Box box = targetEntity.getBoundingBox().contract(0.001, 0, 0.001);
        if (predictMovement) {
            Vec3d v = targetEntity.getVelocity();
            box.offset(v.x, v.y, v.z);
        }

        return pos;
    }

    public static List<BlockPos> dynamicFeetPos(PlayerEntity targetEntity, boolean predictMovement) {
        List<BlockPos> pos = new ArrayList<>();
        Box box = targetEntity.getBoundingBox().contract(0.001, 0, 0.001);
        if (predictMovement) {
            Vec3d v = targetEntity.getVelocity();
            box.offset(v.x, v.y, v.z);
        return pos;
    }
        return pos;
    }}