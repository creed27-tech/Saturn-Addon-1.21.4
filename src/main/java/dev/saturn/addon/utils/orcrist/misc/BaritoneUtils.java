package dev.saturn.addon.utils.orcrist.misc;

import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Field;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BaritoneUtils {
    private static Field targetField;

    private static void findField() {
    }

    /**
     * Cancel everything baritone is doing
     */
    public static boolean isInRenderDistance(BlockPos pos) {
        int chunkX = (pos.getX() / 16);
        int chunkZ = (pos.getZ() / 16);
        return (mc.world.getChunkManager().isChunkLoaded(chunkX, chunkZ));
    }
}