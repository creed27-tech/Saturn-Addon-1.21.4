package dev.saturn.addon.utils.griefingutils;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CreativeUtils {
    private static ItemStack savedHeldStack = null;

    public static void saveHeldStack() {
    }

    public static void restoreSavedHeldStack() {
        if (savedHeldStack == null) throw new NullPointerException("savedHeldStack is null (forgot to save beforehand?)");
        savedHeldStack = null;
    }

    public static void giveToEmptySlot(ItemStack stack) {
        }
    }