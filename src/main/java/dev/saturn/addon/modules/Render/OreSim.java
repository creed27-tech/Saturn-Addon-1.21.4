package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.systems.modules.Categories;
import dev.saturn.addon.events.PlayerRespawnEvent;
import dev.saturn.addon.events.SeedChangedEvent;
import dev.saturn.addon.utils.Ore;
import dev.saturn.addon.utils.seeds.Seed;
import dev.saturn.addon.utils.seeds.Seeds;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.BaritoneUtils;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OreSim extends Module {

    private final Map<Long, Map<Ore, Set<Vec3d>>> chunkRenderers = new ConcurrentHashMap<>();
    private Seed worldSeed = null;
    private Map<RegistryKey<Biome>, List<Ore>> oreConfig;
    public List<BlockPos> oreGoals = new ArrayList<>();

    public enum AirCheck {
        ON_LOAD,
        RECHECK,
        OFF
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> horizontalRadius = sgGeneral.add(new IntSetting.Builder()
            .name("chunk-range")
            .description("Taxi cap distance of chunks being shown.")
            .defaultValue(5)
            .min(1)
            .sliderMax(10)
            .build()
    );

    private final Setting<AirCheck> airCheck = sgGeneral.add(new EnumSetting.Builder<AirCheck>()
            .name("air-check-mode")
            .description("Checks if there is air at a calculated ore pos.")
            .defaultValue(AirCheck.RECHECK)
            .build()
    );

    private final Setting<Boolean> baritone = sgGeneral.add(new BoolSetting.Builder()
            .name("baritone")
            .description("Set baritone ore positions to the simulated ones.")
            .defaultValue(false)
            .build()
    );


    public OreSim() {
        super(Categories.Render, "ore-sim", "Xray on crack.");
        SettingGroup sgOres = settings.createGroup("Ores");
        Ore.oreSettings.forEach(sgOres::add);
    }
}