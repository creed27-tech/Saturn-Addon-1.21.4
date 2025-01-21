package dev.saturn.addon.modules.Combat;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.*;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import dev.saturn.addon.utils.proxima.BlockUtil;
import dev.saturn.addon.utils.proxima.EntityUtil;
import dev.saturn.addon.utils.proxima.Timer;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dev.saturn.addon.utils.proxima.RenderUtil.*;


public class AnchorBoomer extends Module {
    public AnchorBoomer(){
        super(Categories.Combat, "anchor-boomer", "Anchor Boomer. | Ported from Proxima");
    }

    public enum TargetMode{
        Nearest,
        MinHealth,
        Openest
    }

    public enum SortMode{
        CloseToTarget,
        CloseToPlayer,
        MinSelfHealth
    }

    public enum MineMode {
        Vanilla,
        Packet
    }

    public enum RenderMode{
        SingleBox,
        Lines,
        BedForm
    }

    private final SettingGroup sgDebug = settings.createGroup("Debug");
    private final Setting<Boolean> debugChat = sgDebug.add(new BoolSetting.Builder().name("debug-chat").description("Automatically rotates you towards the city block.").defaultValue(false).build());

    private final SettingGroup sgTarget = settings.createGroup("Target");
    private final Setting<Double> enemyRange = sgTarget.add(new DoubleSetting.Builder().name("enemy-range").description("The radius in which players get targeted.").defaultValue(10).min(0).sliderMax(15).build());
    private final Setting<TargetMode> targetMode = sgTarget.add(new EnumSetting.Builder<TargetMode>().name("target-mode").description("Which way to swap.").defaultValue(TargetMode.Nearest).build());
    private final Setting<Boolean> ignoreBedrockBurrow = sgTarget.add(new BoolSetting.Builder().name("ignore-bedrock-burrow").description("Will not place and break beds if they will kill you.").defaultValue(true).build());
    private final Setting<Boolean> stopPlaceBurrowed = sgTarget.add(new BoolSetting.Builder().name("stop-place-burrowed").description("Will not place and break beds if they will kill you.").defaultValue(true).build());
    private final Setting<Boolean> stopPlaceSelfHole = sgTarget.add(new BoolSetting.Builder().name("stop-place-self-hole").description("Will not place and break beds if they will kill you.").defaultValue(true).build());

    private final SettingGroup sgFindPos = settings.createGroup("Find Pos");
    private final Setting<SortMode> sortMode = sgFindPos.add(new EnumSetting.Builder<SortMode>().name("sort-mode").description("Which way to swap.").defaultValue(SortMode.MinSelfHealth).build());

    private final SettingGroup sgPlace = settings.createGroup("Place");
    private final Setting<Boolean> oneDotTwelve = sgPlace.add(new BoolSetting.Builder().name("1.12-place").description("Automatically rotates you towards the city block.").defaultValue(false).build());
    private final Setting<Boolean> aSync = sgPlace.add(new BoolSetting.Builder().name("async").description("Will not place and break beds if they will kill you.").defaultValue(true).build());
    private final Setting<Double> placeRange = sgPlace.add(new DoubleSetting.Builder().name("place-range").description("The range at which players can be targeted.").defaultValue(5.5).min(0.0).sliderMax(10).build());
    private final Setting<Double> xPlaceRange = sgPlace.add(new DoubleSetting.Builder().name("x-range").description("The range at which players can be targeted.").defaultValue(5.5).min(0.0).sliderMax(10).build());
    private final Setting<Double> yPlaceRange = sgPlace.add(new DoubleSetting.Builder().name("y-range").description("The range at which players can be targeted.").defaultValue(4.5).min(0.0).sliderMax(10).build());

    private final SettingGroup sgBreak = settings.createGroup("Break");
    private final Setting<Double> breakRange = sgBreak.add(new DoubleSetting.Builder().name("break-pange").description("The range at which players can be targeted.").defaultValue(5.5).min(0.0).sliderMax(10).build());
    private final Setting<Double> xBreakRange = sgBreak.add(new DoubleSetting.Builder().name("x-range").description("The range at which players can be targeted.").defaultValue(5.5).min(0.0).sliderMax(10).build());
    private final Setting<Double> yBreakRange = sgBreak.add(new DoubleSetting.Builder().name("y-range").description("The range at which players can be targeted.").defaultValue(4.5).min(0.0).sliderMax(10).build());

    private final SettingGroup sgFind = settings.createGroup("Find");
    private final Setting<Integer> findPlaceDelay = sgFind.add(new IntSetting.Builder().name("find-place-delay").description("The delay between placing beds in ticks.").defaultValue(7).min(0).sliderMax(20).build());
    private final Setting<Integer> findBreakDelay = sgFind.add(new IntSetting.Builder().name("find-break-delay").description("The delay between placing beds in ticks.").defaultValue(0).min(0).sliderMax(20).build());

    private final SettingGroup sgMove = settings.createGroup("Move");
    public final Setting<Double> minSpeed = sgMove.add(new DoubleSetting.Builder().name("min-speed").description("The range at which players can be targeted.").defaultValue(3).min(0.0).sliderMax(36).build());
    private final Setting<Integer> movePlaceDelay = sgMove.add(new IntSetting.Builder().name("move-place-delay").description("The delay between placing beds in ticks.").defaultValue(7).min(0).sliderMax(20).build());
    private final Setting<Integer> moveBreakDelay = sgMove.add(new IntSetting.Builder().name("move-break-delay").description("The delay between placing beds in ticks.").defaultValue(0).min(0).sliderMax(20).build());

    private final SettingGroup sgTop = settings.createGroup("Top");
    private final Setting<Integer> topPlaceDelay = sgTop.add(new IntSetting.Builder().name("top-place-delay").description("The delay between placing beds in ticks.").defaultValue(0).min(0).sliderMax(20).build());
    private final Setting<Integer> topBreakDelay = sgTop.add(new IntSetting.Builder().name("top-break-delay").description("The delay between placing beds in ticks.").defaultValue(8).min(0).sliderMax(20).build());

    private final SettingGroup sgSurround = settings.createGroup("Surround");
    private final Setting<Integer> surroundPlaceDelay = sgSurround.add(new IntSetting.Builder().name("surround-place-delay").description("The delay between placing beds in ticks.").defaultValue(0).min(0).sliderMax(20).build());
    private final Setting<Integer> surroundBreakDelay = sgSurround.add(new IntSetting.Builder().name("surround-break-delay").description("The delay between placing beds in ticks.").defaultValue(7).min(0).sliderMax(20).build());

    private final SettingGroup sgDamages = settings.createGroup("Damages");
    public final Setting<Double> minDamage = sgDamages.add(new DoubleSetting.Builder().name("min-damage").description("The range at which players can be targeted.").defaultValue(6).min(0.0).sliderMax(36).build());
    private final Setting<Double> maxSelfDamage = sgDamages.add(new DoubleSetting.Builder().name("max-self-damage").description("The maximum damage to inflict on yourself.").defaultValue(4.5).range(0, 36).sliderMax(36).build());
    private final Setting<Boolean> lethalDamage = sgDamages.add(new BoolSetting.Builder().name("lethal-damage").description("Will not place and break beds if they will kill you.").defaultValue(true).build());
    public final Setting<Double> minHealth = sgDamages.add(new DoubleSetting.Builder().name("min-health").description("The range at which players can be targeted.").defaultValue(3).min(0.0).sliderMax(36).visible(lethalDamage::get).build());
    private final Setting<Boolean> antiSuicide = sgDamages.add(new BoolSetting.Builder().name("anti-suicide").description("Will not place and break beds if they will kill you.").defaultValue(true).build());

    private final SettingGroup sgTopBreak = settings.createGroup("Top Break");
    private final Setting<MineMode> topMineMode = sgTopBreak.add(new EnumSetting.Builder<MineMode>().name("mining").description("Block breaking method").defaultValue(MineMode.Packet).build());
    private final Setting<Boolean> onlyInHoleTop = sgTopBreak.add(new BoolSetting.Builder().name("only-in-hole").description("Will not place and break beds if they will kill you.").defaultValue(true).build());
    private final Setting<Boolean> onlyOnGroundTop = sgTopBreak.add(new BoolSetting.Builder().name("only-on-ground").description("Will not place and break beds if they will kill you.").defaultValue(true).build());
    private final Setting<Keybind> breakTop = sgTopBreak.add(new KeybindSetting.Builder().name("break-top").description("Change the pickaxe slot to an iron one when pressing the button.").defaultValue(Keybind.fromKey(8)).build());
    private final Setting<Boolean> autoMineTop = sgTopBreak.add(new BoolSetting.Builder().name("auto-mine-top").description("Will not place and break beds if they will kill you.").defaultValue(true).build());

    private final SettingGroup sgBurrowBreak = settings.createGroup("Burrow Break");
    private final Setting<MineMode> burrowMineMode = sgBurrowBreak.add(new EnumSetting.Builder<MineMode>().name("mining").description("Block breaking method").defaultValue(MineMode.Packet).build());
    private final Setting<Boolean> onlyInHoleBurrow = sgBurrowBreak.add(new BoolSetting.Builder().name("only-in-hole").description("Will not place and break beds if they will kill you.").defaultValue(true).build());
    private final Setting<Boolean> onlyOnGroundBurrow = sgBurrowBreak.add(new BoolSetting.Builder().name("only-on-ground").description("Will not place and break beds if they will kill you.").defaultValue(true).build());
    private final Setting<Keybind> breakBurrow = sgBurrowBreak.add(new KeybindSetting.Builder().name("break-burrow").description("Change the pickaxe slot to an iron one when pressing the button.").defaultValue(Keybind.fromKey(8)).build());
    private final Setting<Boolean> autoMineBurrow = sgBurrowBreak.add(new BoolSetting.Builder().name("auto-mine-burrow").description("Will not place and break beds if they will kill you.").defaultValue(true).build());

    private final SettingGroup sgAutoRefill = settings.createGroup("Auto Refill");
    private final Setting<Boolean> autoMove = sgAutoRefill.add(new BoolSetting.Builder().name("auto-move").description("Moves beds into a selected hotbar slot.").defaultValue(false).build());
    private final Setting<Integer> respawnMoveSlot = sgAutoRefill.add(new IntSetting.Builder().name("respawn-anchor-move-slot").description("The slot auto move moves beds to.").defaultValue(9).range(1, 9).sliderRange(1, 9).visible(autoMove::get).build());
    private final Setting<Integer> glowstoneMoveSlot = sgAutoRefill.add(new IntSetting.Builder().name("glowstone-move-slot").description("The slot auto move moves beds to.").defaultValue(8).range(1, 9).sliderRange(1, 9).visible(autoMove::get).build());
    private final Setting<Boolean> autoSwitch = sgAutoRefill.add(new BoolSetting.Builder().name("auto-switch").description("Switches to and from beds automatically.").defaultValue(true).build());
    private final Setting<Boolean> silentSwitch = sgAutoRefill.add(new BoolSetting.Builder().name("silent-switch").description("Switches to and from beds automatically.").defaultValue(true).build());

    private final SettingGroup sgMisc = settings.createGroup("Misc");
    private final Setting<Boolean> checkDimension = sgMisc.add(new BoolSetting.Builder().name("check-dimension").description("Will not place and break beds if they will kill you.").defaultValue(true).build());
    private final Setting<Boolean> checkItems = sgMisc.add(new BoolSetting.Builder().name("check-items").description("Will not place and break beds if they will kill you.").defaultValue(true).build());

    private final SettingGroup sgRender = settings.createGroup("Render");
    private final Setting<Boolean> swing = sgRender.add(new BoolSetting.Builder().name("swing").description("Whether to swing hand clientside clientside.").defaultValue(true).build());
    private final Setting<Boolean> renderMine = sgRender.add(new BoolSetting.Builder().name("render-mine").description("Renders the block where it is placing a bed.").defaultValue(true).build());
    private final Setting<ShapeMode> mineShapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>().name("mine-shape-mode").description("How the shapes are rendered.").defaultValue(ShapeMode.Both).visible(renderMine::get).build());
    private final Setting<SettingColor> mineLineColor = sgRender.add(new ColorSetting.Builder().name("mine-line-color").description("The side color for positions to be placed.").defaultValue(new SettingColor(15, 255, 211,75)).visible(renderMine::get).build());
    private final Setting<SettingColor> mineSideColor = sgRender.add(new ColorSetting.Builder().name("mine-side-color").description("The line color for positions to be placed.").defaultValue(new SettingColor(15, 255, 211)).visible(renderMine::get).build());

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder().name("render").description("Renders the block where it is placing a bed.").defaultValue(true).build());
    private final Setting<Boolean> fade = sgRender.add(new BoolSetting.Builder().name("fade").description("Renders the block where it is placing a bed.").defaultValue(true).build());
    private final Setting<Integer> fadeTick = sgRender.add(new IntSetting.Builder().name("fade-tick").description("The slot auto move moves beds to.").defaultValue(8).visible(fade::get).build());
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>().name("shape-mode").description("How the shapes are rendered.").defaultValue(ShapeMode.Both).visible(render::get).build());
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder().name("line-color").description("The side color for positions to be placed.").defaultValue(new SettingColor(15, 255, 211,75)).visible(render::get).build());
    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder().name("side-color").description("The line color for positions to be placed.").defaultValue(new SettingColor(15, 255, 211)).visible(render::get).build());
    private final Setting<SettingColor> lineColor2 = sgRender.add(new ColorSetting.Builder().name("line-color-2").description("The side color for positions to be placed.").defaultValue(new SettingColor(15, 255, 211,75)).visible(render::get).build());
    private final Setting<SettingColor> sideColor2 = sgRender.add(new ColorSetting.Builder().name("side-color-2").description("The line color for positions to be placed.").defaultValue(new SettingColor(15, 255, 211)).visible(render::get).build());
    private final Setting<Integer> width = sgRender.add(new IntSetting.Builder().name("width").defaultValue(1).min(1).max(5).sliderMin(1).sliderMax(4).build());

    private List<BlockPos> array = new ArrayList<>();
    private double placeMs;
    private double breakMs;

    private double time;
    private double currentDamage;

    private BlockPos breakPos;

    private final Timer placeTimer = new Timer();
    private final Timer breakTimer = new Timer();

    private BlockPos minePos;

    private BlockPos renderPos;
            }