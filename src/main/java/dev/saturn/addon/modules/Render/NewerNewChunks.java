package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;

public class NewerNewChunks extends Module {
    public enum DetectMode {
        Normal,
        IgnoreBlockExploit,
        BlockExploitMode
    }
    private final SettingGroup specialGroup = settings.createGroup("Disable PaletteExploit if server version <1.18");
    private final SettingGroup specialGroup2 = settings.createGroup("Detection for chunks that were generated in old versions.");
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgCdata = settings.createGroup("Saved Chunk Data");
    private final SettingGroup sgcacheCdata = settings.createGroup("Cached Chunk Data");
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Boolean> PaletteExploit = specialGroup.add(new BoolSetting.Builder()
            .name("PaletteExploit")
            .description("Detects new chunks by scanning the order of chunk section palettes. Highlights chunks being updated from an old version.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> beingUpdatedDetector = specialGroup.add(new BoolSetting.Builder()
            .name("Detection for chunks that haven't been explored since <=1.17")
            .description("Marks chunks as their own color if they are currently being updated from old version.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> overworldOldChunksDetector = specialGroup2.add(new BoolSetting.Builder()
            .name("Pre 1.17 Overworld OldChunk Detector")
            .description("Marks chunks as generated in an old version if they have specific blocks above Y 0 and are in the overworld.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> netherOldChunksDetector = specialGroup2.add(new BoolSetting.Builder()
            .name("Pre 1.16 Nether OldChunk Detector")
            .description("Marks chunks as generated in an old version if they are missing blocks found in the new Nether.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> endOldChunksDetector = specialGroup2.add(new BoolSetting.Builder()
            .name("Pre 1.13 End OldChunk Detector")
            .description("Marks chunks as generated in an old version if they have the biome of minecraft:the_end.")
            .defaultValue(true)
            .build()
    );
    public final Setting<DetectMode> detectmode = sgGeneral.add(new EnumSetting.Builder<DetectMode>()
            .name("Chunk Detection Mode")
            .description("Anything other than normal is for old servers where build limits are being increased due to updates.")
            .defaultValue(DetectMode.Normal)
            .build()
    );
    private final Setting<Boolean> liquidexploit = sgGeneral.add(new BoolSetting.Builder()
            .name("LiquidExploit")
            .description("Estimates newchunks based on flowing liquids.")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> blockupdateexploit = sgGeneral.add(new BoolSetting.Builder()
            .name("BlockUpdateExploit")
            .description("Estimates newchunks based on block updates. THESE MAY POSSIBLY BE OLD. BlockExploitMode needed to help determine false positives.")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> remove = sgcacheCdata.add(new BoolSetting.Builder()
            .name("RemoveOnModuleDisabled")
            .description("Removes the cached chunks when disabling the module.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> worldleaveremove = sgcacheCdata.add(new BoolSetting.Builder()
            .name("RemoveOnLeaveWorldOrChangeDimensions")
            .description("Removes the cached chunks when leaving the world or changing dimensions.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> removerenderdist = sgcacheCdata.add(new BoolSetting.Builder()
            .name("RemoveOutsideRenderDistance")
            .description("Removes the cached chunks when they leave the defined render distance.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> save = sgCdata.add(new BoolSetting.Builder()
            .name("SaveChunkData")
            .description("Saves the cached chunks to a file.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> load = sgCdata.add(new BoolSetting.Builder()
            .name("LoadChunkData")
            .description("Loads the saved chunks from the file.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> autoreload = sgCdata.add(new BoolSetting.Builder()
            .name("AutoReloadChunks")
            .description("Reloads the chunks automatically from your savefiles on a delay.")
            .defaultValue(false)
            .visible(() -> load.get())
            .build()
    );
    private final Setting<Integer> removedelay = sgCdata.add(new IntSetting.Builder()
            .name("AutoReloadDelayInSeconds")
            .description("Reloads the chunks automatically from your savefiles on a delay.")
            .sliderRange(1,300)
            .defaultValue(60)
            .visible(() -> autoreload.get() && load.get())
            .build()
    );

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();
        WButton deletedata = table.add(theme.button("**DELETE CHUNK DATA**")).expandX().minWidth(100).widget();
        deletedata.action = () -> {
            if (deletewarning==0) error("PRESS AGAIN WITHIN 5s TO DELETE ALL CHUNK DATA FOR THIS DIMENSION.");
            deletewarningTicks=0;
            deletewarning++;
        };
        table.row();
        return table;
    }

    // render
    public final Setting<Integer> renderDistance = sgRender.add(new IntSetting.Builder()
            .name("Render-Distance(Chunks)")
            .description("How many chunks from the character to render the detected chunks.")
            .defaultValue(128)
            .min(6)
            .sliderRange(6,1024)
            .build()
    );
    public final Setting<Integer> renderHeight = sgRender.add(new IntSetting.Builder()
            .name("render-height")
            .description("The height at which new chunks will be rendered")
            .defaultValue(0)
            .sliderRange(-112,319)
            .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> newChunksSideColor = sgRender.add(new ColorSetting.Builder()
            .name("new-chunks-side-color")
            .description("Color of the chunks that are completely new.")
            .defaultValue(new SettingColor(255, 0, 0, 95))
            .visible(() -> (shapeMode.get() == ShapeMode.Sides || shapeMode.get() == ShapeMode.Both))
            .build()
    );
    private final Setting<SettingColor> tickexploitChunksSideColor = sgRender.add(new ColorSetting.Builder()
            .name("BlockExploitChunks-side-color")
            .description("MAY POSSIBLY BE OLD. Color of the chunks that have been triggered via block ticking packets")
            .defaultValue(new SettingColor(0, 0, 255, 75))
            .visible(() -> (shapeMode.get() == ShapeMode.Sides || shapeMode.get() == ShapeMode.Both) && detectmode.get()== DetectMode.BlockExploitMode)
            .build()
    );

    private final Setting<SettingColor> oldChunksSideColor = sgRender.add(new ColorSetting.Builder()
            .name("old-chunks-side-color")
            .description("Color of the chunks that have been loaded before.")
            .defaultValue(new SettingColor(0, 255, 0, 40))
            .visible(() -> shapeMode.get() == ShapeMode.Sides || shapeMode.get() == ShapeMode.Both)
            .build()
    );
    private final Setting<SettingColor> beingUpdatedOldChunksSideColor = sgRender.add(new ColorSetting.Builder()
            .name("being-updated-chunks-side-color")
            .description("Color of the chunks that haven't been explored since versions <=1.17.")
            .defaultValue(new SettingColor(255, 210, 0, 60))
            .visible(() -> shapeMode.get() == ShapeMode.Sides || shapeMode.get() == ShapeMode.Both)
            .build()
    );
    private final Setting<SettingColor> OldGenerationOldChunksSideColor = sgRender.add(new ColorSetting.Builder()
            .name("old-version-chunks-side-color")
            .description("Color of the chunks that have been loaded before in old versions.")
            .defaultValue(new SettingColor(190, 255, 0, 40))
            .visible(() -> shapeMode.get() == ShapeMode.Sides || shapeMode.get() == ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> newChunksLineColor = sgRender.add(new ColorSetting.Builder()
            .name("new-chunks-line-color")
            .description("Color of the chunks that are completely new.")
            .defaultValue(new SettingColor(255, 0, 0, 205))
            .visible(() -> (shapeMode.get() == ShapeMode.Lines || shapeMode.get() == ShapeMode.Both))
            .build()
    );
    private final Setting<SettingColor> tickexploitChunksLineColor = sgRender.add(new ColorSetting.Builder()
            .name("BlockExploitChunks-line-color")
            .description("MAY POSSIBLY BE OLD. Color of the chunks that have been triggered via block ticking packets")
            .defaultValue(new SettingColor(0, 0, 255, 170))
            .visible(() -> (shapeMode.get() == ShapeMode.Lines || shapeMode.get() == ShapeMode.Both) && detectmode.get()== DetectMode.BlockExploitMode)
            .build()
    );

    private final Setting<SettingColor> oldChunksLineColor = sgRender.add(new ColorSetting.Builder()
            .name("old-chunks-line-color")
            .description("Color of the chunks that have been loaded before.")
            .defaultValue(new SettingColor(0, 255, 0, 80))
            .visible(() -> shapeMode.get() == ShapeMode.Lines || shapeMode.get() == ShapeMode.Both)
            .build()
    );
    private final Setting<SettingColor> beingUpdatedOldChunksLineColor = sgRender.add(new ColorSetting.Builder()
            .name("being-updated-chunks-line-color")
            .description("Color of the chunks that haven't been explored since versions <=1.17.")
            .defaultValue(new SettingColor(255, 220, 0, 100))
            .visible(() -> shapeMode.get() == ShapeMode.Lines || shapeMode.get() == ShapeMode.Both)
            .build()
    );
    private final Setting<SettingColor> OldGenerationOldChunksLineColor = sgRender.add(new ColorSetting.Builder()
            .name("old-version-chunks-line-color")
            .description("Color of the chunks that have been loaded before in old versions.")
            .defaultValue(new SettingColor(190, 255, 0, 80))
            .visible(() -> shapeMode.get() == ShapeMode.Lines || shapeMode.get() == ShapeMode.Both)
            .build()
    );
    private static final ExecutorService taskExecutor = Executors.newCachedThreadPool();
    private int deletewarningTicks=666;
    private int deletewarning=0;
    private String serverip;
    private String world;
    private final Set<ChunkPos> newChunks = Collections.synchronizedSet(new HashSet<>());
    private final Set<ChunkPos> oldChunks = Collections.synchronizedSet(new HashSet<>());
    private final Set<ChunkPos> beingUpdatedOldChunks = Collections.synchronizedSet(new HashSet<>());
    private final Set<ChunkPos> OldGenerationOldChunks = Collections.synchronizedSet(new HashSet<>());
    private final Set<ChunkPos> tickexploitChunks = Collections.synchronizedSet(new HashSet<>());
    private static final Direction[] searchDirs = new Direction[] { Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.UP };
    private int errticks=0;
    private int autoreloadticks=0;
    private int loadingticks=0;
    private boolean worldchange=false;
    private int justenabledsavedata=0;
    private boolean saveDataWasOn = false;
    public int chunkcounterticks=0;
    public static boolean chunkcounter;
    public static int newchunksfound=0;
    public static int oldchunksfound=0;
    public static int beingUpdatedOldChunksfound=0;
    public static int OldGenerationOldChunksfound=0;
    public static int tickexploitchunksfound=0;
    private static final Set<Block> ORE_BLOCKS = new HashSet<>();
    static {
        ORE_BLOCKS.add(Blocks.COAL_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE);
        ORE_BLOCKS.add(Blocks.COPPER_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_COPPER_ORE);
        ORE_BLOCKS.add(Blocks.IRON_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);
        ORE_BLOCKS.add(Blocks.GOLD_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE);
        ORE_BLOCKS.add(Blocks.LAPIS_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_LAPIS_ORE);
        ORE_BLOCKS.add(Blocks.DIAMOND_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        ORE_BLOCKS.add(Blocks.REDSTONE_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        ORE_BLOCKS.add(Blocks.EMERALD_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_EMERALD_ORE);
    }
    private static final Set<Block> DEEPSLATE_BLOCKS = new HashSet<>();
    static {
        DEEPSLATE_BLOCKS.add(Blocks.DEEPSLATE);
        DEEPSLATE_BLOCKS.add(Blocks.DEEPSLATE_COPPER_ORE);
        DEEPSLATE_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);
        DEEPSLATE_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE);
        DEEPSLATE_BLOCKS.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        DEEPSLATE_BLOCKS.add(Blocks.DEEPSLATE_EMERALD_ORE);
        DEEPSLATE_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE);
        DEEPSLATE_BLOCKS.add(Blocks.DEEPSLATE_LAPIS_ORE);
        DEEPSLATE_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
    }
    private static final Set<Block> NEW_OVERWORLD_BLOCKS = new HashSet<>();
    static {
        NEW_OVERWORLD_BLOCKS.add(Blocks.DEEPSLATE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.AMETHYST_BLOCK);
        NEW_OVERWORLD_BLOCKS.add(Blocks.BUDDING_AMETHYST);
        NEW_OVERWORLD_BLOCKS.add(Blocks.AZALEA);
        NEW_OVERWORLD_BLOCKS.add(Blocks.FLOWERING_AZALEA);
        NEW_OVERWORLD_BLOCKS.add(Blocks.BIG_DRIPLEAF);
        NEW_OVERWORLD_BLOCKS.add(Blocks.BIG_DRIPLEAF_STEM);
        NEW_OVERWORLD_BLOCKS.add(Blocks.SMALL_DRIPLEAF);
        NEW_OVERWORLD_BLOCKS.add(Blocks.CAVE_VINES);
        NEW_OVERWORLD_BLOCKS.add(Blocks.CAVE_VINES_PLANT);
        NEW_OVERWORLD_BLOCKS.add(Blocks.SPORE_BLOSSOM);
        NEW_OVERWORLD_BLOCKS.add(Blocks.COPPER_ORE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.DEEPSLATE_COPPER_ORE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.DEEPSLATE_EMERALD_ORE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.DEEPSLATE_LAPIS_ORE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.GLOW_LICHEN);
        NEW_OVERWORLD_BLOCKS.add(Blocks.RAW_COPPER_BLOCK);
        NEW_OVERWORLD_BLOCKS.add(Blocks.RAW_IRON_BLOCK);
        NEW_OVERWORLD_BLOCKS.add(Blocks.DRIPSTONE_BLOCK);
        NEW_OVERWORLD_BLOCKS.add(Blocks.MOSS_BLOCK);
        NEW_OVERWORLD_BLOCKS.add(Blocks.MOSS_CARPET);
        NEW_OVERWORLD_BLOCKS.add(Blocks.POINTED_DRIPSTONE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.SMOOTH_BASALT);
        NEW_OVERWORLD_BLOCKS.add(Blocks.TUFF);
        NEW_OVERWORLD_BLOCKS.add(Blocks.CALCITE);
        NEW_OVERWORLD_BLOCKS.add(Blocks.HANGING_ROOTS);
        NEW_OVERWORLD_BLOCKS.add(Blocks.ROOTED_DIRT);
        NEW_OVERWORLD_BLOCKS.add(Blocks.AZALEA_LEAVES);
        NEW_OVERWORLD_BLOCKS.add(Blocks.FLOWERING_AZALEA_LEAVES);
        NEW_OVERWORLD_BLOCKS.add(Blocks.POWDER_SNOW);
    }
    private static final Set<Block> NEW_NETHER_BLOCKS = new HashSet<>();
    static {
        NEW_NETHER_BLOCKS.add(Blocks.ANCIENT_DEBRIS);
        NEW_NETHER_BLOCKS.add(Blocks.BASALT);
        NEW_NETHER_BLOCKS.add(Blocks.BLACKSTONE);
        NEW_NETHER_BLOCKS.add(Blocks.GILDED_BLACKSTONE);
        NEW_NETHER_BLOCKS.add(Blocks.POLISHED_BLACKSTONE_BRICKS);
        NEW_NETHER_BLOCKS.add(Blocks.CRIMSON_STEM);
        NEW_NETHER_BLOCKS.add(Blocks.CRIMSON_NYLIUM);
        NEW_NETHER_BLOCKS.add(Blocks.NETHER_GOLD_ORE);
        NEW_NETHER_BLOCKS.add(Blocks.WARPED_NYLIUM);
        NEW_NETHER_BLOCKS.add(Blocks.WARPED_STEM);
        NEW_NETHER_BLOCKS.add(Blocks.WARPED_NYLIUM);
        NEW_NETHER_BLOCKS.add(Blocks.CRIMSON_NYLIUM);
        NEW_NETHER_BLOCKS.add(Blocks.TWISTING_VINES);
        NEW_NETHER_BLOCKS.add(Blocks.WEEPING_VINES);
        NEW_NETHER_BLOCKS.add(Blocks.BONE_BLOCK);
        NEW_NETHER_BLOCKS.add(Blocks.CHAIN);
        NEW_NETHER_BLOCKS.add(Blocks.OBSIDIAN);
        NEW_NETHER_BLOCKS.add(Blocks.CRYING_OBSIDIAN);
        NEW_NETHER_BLOCKS.add(Blocks.SOUL_SOIL);
        NEW_NETHER_BLOCKS.add(Blocks.SOUL_FIRE);
    }
    Set<Path> FILE_PATHS = new HashSet<>(Set.of(
            Paths.get("OldChunkData.txt"),
            Paths.get("BeingUpdatedChunkData.txt"),
            Paths.get("OldGenerationChunkData.txt"),
            Paths.get("NewChunkData.txt"),
            Paths.get("BlockExploitChunkData.txt")
    ));

    public NewerNewChunks() {
        super(Categories.Render, "newer-new-chunks", "Detects new chunks by scanning the order of chunk section palettes. Can also check liquid flow, and block ticking packets.");
                                }
                            }