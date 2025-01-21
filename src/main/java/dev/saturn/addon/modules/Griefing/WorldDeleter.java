package dev.saturn.addon.modules.Griefing;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.WorldChunk;
import meteordevelopment.meteorclient.systems.modules.Module;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class WorldDeleter extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("The delay between fill commands in ticks.")
            .defaultValue(1)
            .range(1, 100)
            .sliderRange(1, 100)
            .build()
    );

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .description("Whether to render things.")
            .defaultValue(true)
            .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .visible(render::get)
            .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The side color.")
            .defaultValue(new SettingColor(255, 75, 0, 127))
            .visible(render::get)
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color.")
            .defaultValue(new SettingColor(255, 75, 0))
            .visible(render::get)
            .build()
    );

    private final Deque<ChunkPos> chunksToProcess = new ArrayDeque<>();
    private ChunkPos currentChunk = null;
    private int chunkToProcessStartSize = 0;

    public WorldDeleter() {
        super(Saturn.Griefing, "world-deleter", "Deletes loaded chunks around you but needs some time to finish. (requires OP)");
    }

    @Override
    public void onDeactivate() {
        chunksToProcess.clear();
    }

    @Override
    public String getInfoString() {
        if (!Utils.canUpdate()) return null;
        return "%.1f%%".formatted(MathHelper.getLerpProgress(
                (float) chunksToProcess.size(),
                (float) chunkToProcessStartSize,
                0f
        ) * 100);
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        ((IVec3d) mc.player.getVelocity()).meteor$set(0, 0, 0);
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (currentChunk == null) return;
        event.renderer.box(
                currentChunk.getStartX(),
                mc.world.getBottomY(),
                currentChunk.getStartZ(),
                currentChunk.getEndX(),
                mc.world.getHeight() + mc.world.getBottomY() - 1,
                currentChunk.getEndZ(),
                sideColor.get(),
                lineColor.get(),
                shapeMode.get(),
                0
        );
    }
}