
package dev.saturn.addon.modules.Build;

import dev.saturn.addon.utils.Automation.HIGUtils;
import dev.saturn.addon.modules.Automation.OffhandManager;
import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.systems.modules.player.AutoEat;
import meteordevelopment.meteorclient.systems.modules.player.AutoGap;
import meteordevelopment.meteorclient.systems.modules.player.AutoTool;
import meteordevelopment.meteorclient.systems.modules.player.InstantRebreak;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.HorizontalDirection;
import meteordevelopment.meteorclient.utils.misc.MBlockPos;
import meteordevelopment.meteorclient.utils.player.CustomPlayerInput;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.*;
import net.minecraft.client.input.Input;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EmptyBlockView;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("ConstantConditions")
public class HighwayBuilderHIG extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgDigging = settings.createGroup("Digging");
    private final SettingGroup sgPaving = settings.createGroup("Paving");
    private final SettingGroup sgInventory = settings.createGroup("Inventory");
    private final SettingGroup sgRenderDigging = settings.createGroup("Render Digging");
    private final SettingGroup sgRenderPaving = settings.createGroup("Render Paving");
    private final SettingGroup sgStatistics = settings.createGroup("Statistics");

    private final Setting<Integer> width = sgGeneral.add(new IntSetting.Builder()
            .name("width")
            .description("Width of the highway.")
            .defaultValue(4)
            .range(1, 6)
            .sliderRange(1, 6)
            .build()
    );

    private final Setting<Integer> height = sgGeneral.add(new IntSetting.Builder()
            .name("height")
            .description("Height of the highway.")
            .defaultValue(3)
            .range(2, 5)
            .sliderRange(2, 5)
            .build()
    );

    private final Setting<Floor> floor = sgGeneral.add(new EnumSetting.Builder<Floor>()
            .name("floor")
            .description("What floor placement mode to use.")
            .defaultValue(Floor.Replace)
            .build()
    );

    private final Setting<Boolean> railings = sgGeneral.add(new BoolSetting.Builder()
            .name("railings")
            .description("Builds railings next to the highway.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> mineAboveRailings = sgGeneral.add(new BoolSetting.Builder()
            .name("mine-above-railings")
            .description("Mines blocks above railings.")
            .visible(railings::get)
            .defaultValue(true)
            .build()
    );

    private final Setting<Rotation> rotation = sgGeneral.add(new EnumSetting.Builder<Rotation>()
            .name("rotation")
            .description("Mode of rotation.")
            .defaultValue(Rotation.Both)
            .build()
    );

    private final Setting<Boolean> disconnectOnToggle = sgGeneral.add(new BoolSetting.Builder()
            .name("disconnect-on-toggle")
            .description("Automatically disconnects when the module is turned off, for example for not having enough blocks.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> pauseOnLag = sgGeneral.add(new BoolSetting.Builder()
            .name("pause-on-lag")
            .description("Pauses the current process while the server stops responding.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> resumeTPS = sgGeneral.add(new IntSetting.Builder()
            .name("resume-tps")
            .description("Server tick speed at which to resume building.")
            .defaultValue(16)
            .range(1, 19)
            .sliderRange(1, 19)
            .visible(pauseOnLag::get)
            .build()
    );

    // Digging

    private final Setting<Boolean> ignoreSigns = sgDigging.add(new BoolSetting.Builder()
            .name("ignore-signs")
            .description("Ignore breaking signs = preserving history (based).")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> dontBreakTools = sgDigging.add(new BoolSetting.Builder()
            .name("dont-break-tools")
            .description("Don't break tools.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Integer> endDurability = sgDigging.add(new IntSetting.Builder()
            .name("end-durability")
            .description("What durability do you want your tools to end up at?")
            .defaultValue(3)
            .range(1, 2031)
            .sliderRange(1, 100)
            .visible(() -> dontBreakTools.get())
            .build()
    );

    private final Setting<Integer> savePickaxes = sgDigging.add(new IntSetting.Builder()
            .name("save-pickaxes")
            .description("How many pickaxes to ensure are saved.")
            .defaultValue(0)
            .range(0, 36)
            .sliderRange(0, 36)
            .visible(() -> !dontBreakTools.get())
            .build()
    );

    private final Setting<Integer> breakDelay = sgDigging.add(new IntSetting.Builder()
            .name("break-delay")
            .description("The delay between breaking blocks.")
            .defaultValue(1)
            .min(0)
            .build()
    );

    private final Setting<Integer> blocksPerTick = sgDigging.add(new IntSetting.Builder()
            .name("blocks-per-tick")
            .description("The maximum amount of blocks that can be mined in a tick. Only applies to blocks instantly breakable.")
            .defaultValue(1)
            .range(1, 100)
            .sliderRange(1, 25)
            .build()
    );

    // Paving

    private final Setting<List<Block>> blocksToPlace = sgPaving.add(new BlockListSetting.Builder()
            .name("blocks-to-place")
            .description("Blocks it is allowed to place.")
            .defaultValue(Blocks.OBSIDIAN)
            .filter(block -> Block.isShapeFullCube(block.getDefaultState().getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)))
            .build()
    );

    private final Setting<Integer> placeDelay = sgPaving.add(new IntSetting.Builder()
            .name("place-delay")
            .description("The delay between placing blocks.")
            .defaultValue(1)
            .min(0)
            .build()
    );

    private final Setting<Integer> placementsPerTick = sgPaving.add(new IntSetting.Builder()
            .name("placements-per-tick")
            .description("The maximum amount of blocks that can be placed in a tick.")
            .defaultValue(1)
            .min(1)
            .build()
    );

    // Inventory

    private final Setting<List<Item>> trashItems = sgInventory.add(new ItemListSetting.Builder()
            .name("trash-items")
            .description("Items that are considered trash and can be thrown out.")
            .defaultValue(
                    Items.NETHERRACK, Items.QUARTZ, Items.GOLD_NUGGET, Items.GOLDEN_SWORD, Items.GLOWSTONE_DUST,
                    Items.GLOWSTONE, Items.BLACKSTONE, Items.BASALT, Items.GHAST_TEAR, Items.SOUL_SAND, Items.SOUL_SOIL,
                    Items.ROTTEN_FLESH
            )
            .build()
    );

    private final Setting<Boolean> mineEnderChests = sgInventory.add(new BoolSetting.Builder()
            .name("mine-ender-chests")
            .description("Mines ender chests for obsidian.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> saveEchests = sgInventory.add(new IntSetting.Builder()
            .name("save-ender-chests")
            .description("How many ender chests to ensure are saved.")
            .defaultValue(1)
            .range(0, 64)
            .sliderRange(0, 64)
            .visible(mineEnderChests::get)
            .build()
    );

    private final Setting<Boolean> instaMineEchests = sgInventory.add(new BoolSetting.Builder()
            .name("instant-rebreak-echests")
            .description("Uses the instaMine exploit to break echests.")
            .defaultValue(false)
            .visible(mineEnderChests::get)
            .build()
    );

    private final Setting<Integer> instaMineDelay = sgInventory.add(new IntSetting.Builder()
            .name("rebreak-delay")
            .description("Delay between instant rebreak attempts.")
            .defaultValue(0)
            .sliderMax(20)
            .visible(() -> mineEnderChests.get() && instaMineEchests.get())
            .build()
    );

    // Render Digging

    private final Setting<Boolean> renderMine = sgRenderDigging.add(new BoolSetting.Builder()
            .name("render-blocks-to-mine")
            .description("Render blocks to be mined.")
            .defaultValue(true)
            .build()
    );

    private final Setting<ShapeMode> renderMineShape = sgRenderDigging.add(new EnumSetting.Builder<ShapeMode>()
            .name("blocks-to-mine-shape-mode")
            .description("How the blocks to be mined are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> renderMineSideColor = sgRenderDigging.add(new ColorSetting.Builder()
            .name("blocks-to-mine-side-color")
            .description("Color of blocks to be mined.")
            .defaultValue(new SettingColor(225, 25, 25, 25))
            .build()
    );

    private final Setting<SettingColor> renderMineLineColor = sgRenderDigging.add(new ColorSetting.Builder()
            .name("blocks-to-mine-line-color")
            .description("Color of blocks to be mined.")
            .defaultValue(new SettingColor(225, 25, 25, 255))
            .build()
    );

    // Render Paving

    private final Setting<Boolean> renderPlace = sgRenderPaving.add(new BoolSetting.Builder()
            .name("render-blocks-to-place")
            .description("Render blocks to be placed.")
            .defaultValue(true)
            .build()
    );

    private final Setting<ShapeMode> renderPlaceShape = sgRenderPaving.add(new EnumSetting.Builder<ShapeMode>()
            .name("blocks-to-place-shape-mode")
            .description("How the blocks to be placed are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> renderPlaceSideColor = sgRenderPaving.add(new ColorSetting.Builder()
            .name("blocks-to-place-side-color")
            .description("Color of blocks to be placed.")
            .defaultValue(new SettingColor(25, 25, 225, 25))
            .build()
    );

    private final Setting<SettingColor> renderPlaceLineColor = sgRenderPaving.add(new ColorSetting.Builder()
            .name("blocks-to-place-line-color")
            .description("Color of blocks to be placed.")
            .defaultValue(new SettingColor(25, 25, 225, 255))
            .build()
    );

    // Statistics

    private final Setting<Boolean> printStatistics = sgStatistics.add(new BoolSetting.Builder()
            .name("print-statistics")
            .description("Prints statistics in chat when disabling Highway Builder+.")
            .defaultValue(true)
            .build()
    );

    public HighwayBuilderHIG() {

        super(Saturn.Build, "highway-builder-HIG", "Automatically builds highways.");
    }

    public enum Floor {
        Replace,
        PlaceMissing
    }

    public enum Rotation {
        None,
        Mine,
        Place,
        Both;
    }
}