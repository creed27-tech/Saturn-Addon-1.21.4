package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import java.util.ArrayList;
import java.util.List;

public class SlimeChunk extends Module{
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgRender = this.settings.createGroup("Render");

    private final Setting<String> seed = sgGeneral.add(new StringSetting.Builder()
            .name("World seed")
            .description("The seed of the world")
            .defaultValue("696969420420")
            .build()
    );

    private final Setting<Integer> y_stretch = sgGeneral.add(new IntSetting.Builder()
            .name("Y Stretch")
            .description("The Y Stretch of the marker.")
            .defaultValue(100)
            .range(0, 384+64)
            .sliderRange(-64, 320)
            .build()
    );

    private final Setting<SettingColor> color = sgRender.add(new ColorSetting.Builder()
            .name("color")
            .description("The color of the marker.")
            .defaultValue(Color.GREEN)
            .build()
    );

    private Color getTransparentSidesColor(Color color, int a) {
        return new Color(color.r, color.g, color.b, a);
    }

    public SlimeChunk() {
        super(Categories.Render, "slime-chunks", "An module that highlights slime chunks given a seed");
    }

    private ChunkPos[] slimeChunks() {
        MinecraftClient client = MinecraftClient.getInstance();

        BlockPos playerPos = client.player.getBlockPos();
        ChunkPos playerChunkPos = new ChunkPos(playerPos);
        int renderDistance = client.options.getViewDistance().getValue();

        List<ChunkPos> chunkPositions = new ArrayList<>();

        for (int xOffset = -renderDistance; xOffset <= renderDistance; xOffset++) {
            for (int zOffset = -renderDistance; zOffset <= renderDistance; zOffset++) {
                int chunkX = playerChunkPos.x + xOffset;
                int chunkZ = playerChunkPos.z + zOffset;
                chunkPositions.add(new ChunkPos(chunkX, chunkZ));
            }
        }

        long longSeed = 0;

        try {
            longSeed = Long.parseLong(seed.get());
        } catch (NumberFormatException ignored) {

        }

        List<ChunkPos> slimeChunks = new ArrayList<>();

        for (ChunkPos chunkPos : chunkPositions) {
            boolean bl = ChunkRandom.getSlimeRandom(chunkPos.x, chunkPos.z, longSeed, 987234911L).nextInt(10) == 0;
            if (bl)
                slimeChunks.add(chunkPos);
        }
        return slimeChunks.toArray(new ChunkPos[0]);
    }

    private Box[] getRenderBoxes(ChunkPos[] chunkPosList) {
        List<Box> boxes = new ArrayList<>();

        for (ChunkPos chunkPos : chunkPosList) {
            int startX = chunkPos.x*16;
            int startY = -64;
            int startZ = chunkPos.z*16;
            int endX = startX + 16;
            int endY = startY + y_stretch.get();
            int endZ = startZ + 16;

            boxes.add(new Box(startX, startY, startZ, endX, endY, endZ));
        }
        return boxes.toArray(new Box[0]);
    }

    @EventHandler
    private void onRender3d(Render3DEvent event) {
        for (Box marker : getRenderBoxes(slimeChunks()))
            event.renderer.box(marker, getTransparentSidesColor(color.get(), 20), color.get(), ShapeMode.Both, 0);
    }
}