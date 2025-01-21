package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import dev.saturn.addon.modules.VHModuleHelper;
import dev.saturn.addon.utils.venomhack.customObjects.RenderBlock;
import dev.saturn.addon.utils.venomhack.customObjects.Timer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class VHSurround extends VHModuleHelper {
    private static final Direction[] ORDERED_DIRECTIONS_ARRAY = new Direction[]{Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.DOWN, Direction.UP};
    private final SettingGroup sgAntiCheat = this.group("Anti Cheat");
    private final SettingGroup sgAttack = this.group("Attack Crystals");
    private final SettingGroup sgModules = this.group("Automation");
    public final Setting<Boolean> auto = this.setting("auto-surround", "Automatically turns on surround when in an obsidian hole.", Boolean.valueOf(false), this.sgModules);
    private final SettingGroup sgRender = this.group("Rendering");
    private final Setting<Boolean> onlyGround = this.setting("only-on-ground", "Won't attempt to place while you're not standing on ground.", Boolean.valueOf(false));
    private final Setting<Boolean> center = this.setting("center", "Will move you to inside the hole so you can surround.", Boolean.valueOf(true));
    private final Setting<Boolean> hardSnap = this.setting("hard-center", "Will align you at the exact center of the hole.", Boolean.valueOf(false), this.center::get);
    private final Setting<Boolean> antiSurroundBreak = this.setting("anti-surround-break", "Places blocks around the city block that is being mined.", Boolean.valueOf(false));
    private final Setting<Boolean> antiPhase = this.setting("anti-phase", "Places blocks around players that are standing in your surround", Boolean.valueOf(true));
    private final Setting<Double> placeRange = this.setting("place-range", "How far you are able to place at max.", Double.valueOf(5.0), this.sgAntiCheat, 0.0, 6.0);
    private final Setting<Boolean> strictDirections = this.setting("strict-directions", "Places only on visible sides.", Boolean.valueOf(false), this.sgAntiCheat);
    private final Setting<Boolean> rotate = this.setting("rotate", "Rotate to where you are placing.", Boolean.valueOf(false), this.sgAntiCheat);
    private final Setting<Boolean> airPlace = this.setting("air-place", "Places blocks midair, will try to find support blocks when off.", Boolean.valueOf(true), this.sgAntiCheat);
    private final Setting<Integer> bpt = this.setting("blocks-per-tick", "How many blocks to place per tick max.", Integer.valueOf(5), this.sgAntiCheat, 1.0, 5.0);
    private final Setting<Integer> delay = this.setting("place-delay", "Delay between placing in ms", Integer.valueOf(25), this.sgAntiCheat, 25.0, 250.0, 25, Integer.MAX_VALUE);
    private final Setting<Boolean> attackCrystals = this.setting("attack-crystals", "Whether to attack crystals that are in the way.", Boolean.valueOf(true), this.sgAttack);
    private final Setting<Integer> attackSwapPenalty = this.setting("swap-penalty", "For how long to wait in ms after switching to obsidian until attacking.", Integer.valueOf(0), this.sgAttack, this.attackCrystals::get, 0.0, 500.0);
    private final Setting<Integer> attackMinAge = this.setting("min-age", "How many ticks the cystal has to be alive for until u can attack it.", Integer.valueOf(0), this.sgAttack, this.attackCrystals::get, 0.0, 5.0);
    private final Setting<Double> attackRange = this.setting("attack-range", "Maximum attack range.", Double.valueOf(4.0), this.sgAttack, this.attackCrystals::get, 0.0, 6.0);
    private final Setting<Integer> attackDelay = this.setting("attack-delay", "How many ticks to wait between attacks.", Integer.valueOf(1), this.sgAttack, this.attackCrystals::get, 1.0, 5.0, 1, Integer.MAX_VALUE);
    private final Setting<Boolean> attackRotate = this.setting("attack-rotate", "Whether to face the crystal you are attacking.", Boolean.valueOf(false), this.sgAttack, this.attackCrystals::get);
    private final Setting<Boolean> toggle = this.setting("toggle-on-y-change", "Will toggle off when you move upwards.", Boolean.valueOf(true), this.sgModules);
    private final Setting<Boolean> toggleStep = this.setting("toggle-step", "Toggles off step when activating surround.", Boolean.valueOf(false), this.sgModules);
    private final Setting<Boolean> toggleSpeed = this.setting("toggle-speed", "Toggles off vh speed when activating surround.", Boolean.valueOf(false), this.sgModules);
    private final Setting<Boolean> toggleBack = this.setting("toggle-back", "Toggles on speed and/or step when turning off surround.", Boolean.valueOf(false), this.sgModules);
    private final Setting<Boolean> swing = this.setting("swing", "Renders your swing client-side.", Boolean.valueOf(true), this.sgRender);
    private final Setting<Boolean> render = this.setting("render", "Renders the block where it is placing a crystal.", Boolean.valueOf(false), this.sgRender);
    private final Setting<Integer> renderTime = this.setting("render-time", "Ticks to render the block for.", Integer.valueOf(8), this.sgRender, this.render::get);
    private final Setting<Boolean> fade = this.setting("fade", "Will reduce the opacity of the rendered block over time.", Boolean.valueOf(true), this.sgRender, this.render::get);
    private final Setting<ShapeMode> shapeMode = this.setting("shape-mode", "How the shapes are rendered.", ShapeMode.Both, this.sgRender, this.render::get);
    private final Setting<SettingColor> sideColor = this.setting("side-color", "The side color.", 0, 0, 255, 10, this.sgRender, this.render::get);
    private final Setting<SettingColor> lineColor = this.setting("line-color", "The line color.", 0, 0, 255, 200, this.sgRender, this.render::get);
    private final List<BlockPos> extras = new CopyOnWriteArrayList();
    private final List<RenderBlock> renderBlocks = Collections.synchronizedList(new ArrayList<>());
    private final Timer swapPenaltyTimer = new Timer();
    private final Timer placeTimer = new Timer();
    private final ConcurrentHashMap<Integer, Long> toRemoveWithTime = new ConcurrentHashMap<>();
    private BlockPos playerPos;
    private boolean hasCentered;
    private boolean isOpen;
    private int attackDelayLeft;
    private int blocksPlaced;

    public VHSurround() {
        super(Saturn.PVP, "VH-surround", " Broken | Surrounds you in blocks to prevent you from taking lots of damage.");
    }

    public enum SurroundMode {
        NORMAL("Normal"), CANCER("Cancer");

        private final String title;

        SurroundMode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return this.title;
        }
            }
        }