package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import dev.saturn.addon.modules.VHModuleHelper;
import dev.saturn.addon.utils.venomhack.BlockUtils2;
import dev.saturn.addon.utils.venomhack.PathUtils;
import dev.saturn.addon.utils.venomhack.customObjects.RenderBlock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VHAutoTrap extends VHModuleHelper {
    private final SettingGroup sgRender = this.group("Render");
    private final Setting<Double> targetRange = this.setting("target-range", "The range at which players can be targeted.", Double.valueOf(5.0), 1.0, 6.0);
    private final Setting<Double> placeRange = this.setting("place-range", "The range at which blocks can be placed.", Double.valueOf(5.0), 1.0, 6.0);
    private final Setting<SortPriority> priority = this.setting("target-priority", "How to select the player to target.", SortPriority.ClosestAngle);
    private final Setting<Integer> delay = this.setting("place-delay", "How many ticks between block placements.", Integer.valueOf(1), 0.0, 10.0);
    private final Setting<Integer> blocksPerTick = this.setting("blocks-per-tick", "Maximum blocks to place per tick.", Integer.valueOf(5), 1.0, 10.0);
    private final Setting<TopMode> topPlacement = this.setting("top-blocks", "Which blocks to place on the top half of the target.", TopMode.Full);
    private final Setting<BottomMode> bottomPlacement = this.setting("bottom-blocks", "Which blocks to place on the bottom half of the target.", BottomMode.None);
    private final Setting<Boolean> antiClip = this.setting("anti-clip", "Prevents the target from vclipping out.", Boolean.valueOf(false));
    private final Setting<Boolean> airPlace = this.setting("air-place", "Places blocks midair, will try to find support blocks when off.", Boolean.valueOf(true));
    private final Setting<Integer> supportAmount = this.setting("max-support-length", "How many support blocks to place max.", Integer.valueOf(2), this.sgGeneral, () -> !this.airPlace.get(), 5.0);
    private final Setting<Boolean> selfToggle = this.setting("self-toggle", "Turns off after placing all blocks.", Boolean.valueOf(true));
    private final Setting<Integer> placeAttempts = this.setting("keep-alive", "How many seconds to wait until disabling when not all blocks can be placed.", Integer.valueOf(3), this.sgGeneral, this.selfToggle::get, 1.0, 5.0);
    private final Setting<Boolean> strictDirections = this.setting("strict-directions", "Places only on visible sides.", Boolean.valueOf(false));
    private final Setting<Boolean> rotate = this.setting("rotate", "Rotates towards blocks when placing.", Boolean.valueOf(true));
    private final Setting<Boolean> swing = this.setting("swing", "Whether to swing your hand client side or not.", Boolean.valueOf(true));
    private final Setting<Boolean> render = this.setting("render", "Renders an overlay where blocks will be placed.", Boolean.valueOf(true), this.sgRender);
    private final Setting<Integer> renderTime = this.setting("render-time", "Ticks to render the block for.", Integer.valueOf(8), this.sgRender, this.render::get);
    private final Setting<Boolean> fade = this.setting("fade", "Will reduce the opacity of the rendered block over time.", Boolean.valueOf(true), this.sgRender, this.render::get);
    private final Setting<ShapeMode> shapeMode = this.setting("shape-mode", "How the shapes are rendered.", ShapeMode.Both, this.sgRender);
    private final Setting<SettingColor> sideColor = this.setting("side-color", "The side color of the target block rendering.", 197, 137, 232, 10, this.sgRender);
    private final Setting<SettingColor> lineColor = this.setting("line-color", "The line color of the target block rendering.", 197, 137, 232, this.sgRender);
    private final List<BlockPos> placePositions = new ArrayList();
    private final List<RenderBlock> renderBlocks = new ArrayList<>();
    private PlayerEntity target;
    private boolean placed;
    private int delayLeft;
    private int sinceLastPlace;

    public VHAutoTrap() {
        super(Saturn.PVP, "VH-auto-trap", "Traps people in an obsidian box to prevent them from moving.");
    }

    public void onActivate() {
        this.target = null;
        this.placed = false;
        this.delayLeft = 0;
        this.sinceLastPlace = 0;
        this.placePositions.clear();
    }

    @EventHandler
    private void onTick(Pre event) {
        if (this.selfToggle.get() && this.placed && this.placePositions.isEmpty() && this.renderBlocks.isEmpty()) {
            this.toggle();
        } else {
            ++this.sinceLastPlace;
            FindItemResult obsidian = InvUtils.findInHotbar(Items.OBSIDIAN);
            if (!obsidian.found()) {
                this.error("No obsidian found! Disabling...");
                this.toggle();
            } else {
                this.target = TargetUtils.getPlayerTarget(this.targetRange.get(), this.priority.get());
                if (!TargetUtils.isBadTarget(this.target, this.targetRange.get())) {
                    if (this.selfToggle.get() && this.sinceLastPlace >= this.placeAttempts.get() * 20 && this.placed) {
                        this.toggle();
                    } else if (this.delayLeft <= 0) {
                        this.placePositions.sort(Comparator.comparingDouble(value -> -value.getSquaredDistance(this.mc.player.getBlockPos())));

                        for (int i = 0; i < this.blocksPerTick.get(); ++i) {
                            if (i >= this.placePositions.size()) {
                                return;
                            }

                            BlockPos pos = this.placePositions.get(i);
                            Vec3d vec = new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5);
                            if (this.mc.player.getEyePos().distanceTo(vec) <= this.placeRange.get() && BlockUtils2.placeBlock(obsidian, pos, this.rotate.get(), 35, this.airPlace.get(), false, this.swing.get(), this.strictDirections.get())) {
                                this.delayLeft = this.delay.get();
                                this.renderBlocks.add(new RenderBlock(pos, this.renderTime.get()));
                                this.placePositions.remove(i);
                                this.placed = true;
                                this.sinceLastPlace = 0;
                            }
                        }
                    } else {
                        --this.delayLeft;
                    }
                }
            }
        }
    }

    @EventHandler
    private void onTickPost(Post event) {
        RenderBlock.tick(this.renderBlocks);
    }

    private boolean findNeighbour(List<BlockPos> list, BlockPos pos, int iteration) {
        if (iteration > this.supportAmount.get()) {
            return false;
        } else {
            BlockState placeState = Blocks.OBSIDIAN.getDefaultState();

            for (Direction direction : Direction.values()) {
                if (this.mc.world.canPlace(placeState, pos.offset(direction), ShapeContext.absent()) && this.findNeighbour(list, pos.offset(direction), iteration + 1)) {
                    PathUtils.smartAdd(list, pos.offset(direction));
                    return true;
                }
            }

            return false;
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!this.renderBlocks.isEmpty() && this.render.get()) {
            this.renderBlocks.forEach(renderBlock -> renderBlock.render(event, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), this.fade.get()));
        }
    }

    public enum BottomMode {
        Single, Platform, Full, None
    }

    public enum TopMode {
        Full, Top, Face, None
    }
}