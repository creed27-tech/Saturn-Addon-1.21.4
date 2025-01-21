package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.Saturn;
import dev.saturn.addon.modules.VHModuleHelper;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Blink;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import net.minecraft.text.Text;
import org.joml.Vector3d;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.*;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BedItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import dev.saturn.addon.enums.venomhack.Mode;
import dev.saturn.addon.enums.venomhack.Origin;
import dev.saturn.addon.enums.venomhack.Type;
import dev.saturn.addon.mixininterfaces.venomhack.IBlink;
import dev.saturn.addon.mixininterfaces.venomhack.IModule;
import dev.saturn.addon.utils.venomhack.*;
import dev.saturn.addon.utils.venomhack.customObjects.RenderBlock;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class VHAutoBed extends VHModuleHelper implements IModule {
    private final SettingGroup sgPlace = this.group("Place");
    private final SettingGroup sgBreak = this.group("Break");
    private final SettingGroup sgRotations = this.group("Rotations");
    private final SettingGroup sgTargeting = this.group("Targeting");
    private final SettingGroup sgPause = this.group("Pause");
    private final SettingGroup sgMisc = this.group("Misc");
    private final SettingGroup sgCraft = this.group("Auto Craft");
    private final SettingGroup sgAuto = this.group("Automation");
    private final SettingGroup sgExperimental = this.group("Experimental");
    private final SettingGroup sgRender = this.group("Render");
    private final Setting<Double> range = this.setting("range", "The interact radius.", Double.valueOf(5.0), 0.0, 6.0);
    private final Setting<Double> wallsRange = this.setting("walls-range", "The interact radius through walls.", Double.valueOf(5.0), 0.0, 6.0);
    private final Setting<Boolean> xymb = this.setting("xymb-mode", "Will check for entity collisions also for the head part.", Boolean.valueOf(false));
    private final Setting<Origin> placeOrigin = this.setting("origin", "How to calculate ranges.", Origin.NCP);
    private final Setting<Boolean> onlyHoled = this.setting("only-in-hole", "Will only target players that are surrounded.", Boolean.valueOf(false), this.sgTargeting);
    private final Setting<Integer> breakDelay = this.setting("break-delay", "The delay between breaking beds in ticks.", Integer.valueOf(0), this.sgBreak, () -> !this.onlyHoled.get());
    private final Setting<Boolean> place = this.setting("place", "Allows Auto Bed to place beds.", Boolean.valueOf(true), this.sgPlace);
    private final Setting<Integer> placeDelay = this.setting("place-delay", "The delay between placing beds in ticks.", Integer.valueOf(10), this.sgPlace, () -> this.place.get() && !this.onlyHoled.get());
    private final Setting<Integer> holePlaceDelay = this.setting("in-hole-place-delay", "The delay between placing on targets that are in holes.", Integer.valueOf(0), this.sgPlace, this.place::get);
    private final Setting<Double> minPlaceDamage = this.setting("min-damage", "The minimum place damage to inflict on your target.", Double.valueOf(8.0), this.sgPlace, this.place::get, 0.0, 36.0);
    private final Setting<Double> maxPlaceSelfDamage = this.setting("max-self-damage", "The maximum damage to inflict on yourself.", Double.valueOf(4.0), this.sgPlace, this.place::get, 36.0);
    private final Setting<Boolean> noSuicidePlace = this.setting("no-suicide", "Will not place beds in for you lethal positions.", Boolean.valueOf(true), this.sgPlace, this.place::get);
    private final Setting<Integer> holeBreakDelay = this.setting("in-hole-break-delay", "The delay between breaking on targets that are in holes.", Integer.valueOf(7), this.sgBreak);
    private final Setting<Double> minBreakDamage = this.setting("min-break-damage", "The minimum break damage to inflict on your target.", Double.valueOf(6.0), this.sgBreak, 0.0, 36.0);
    private final Setting<Double> maxBreakSelfDamage = this.setting("max-break-self-damage", "The maximum damage to inflict on yourself.", Double.valueOf(4.0), this.sgBreak, 0.0, 36.0);
    private final Setting<Boolean> noSuicideBreak = this.setting("no-suicide", "Will not breaks beds in for you lethal positions.", Boolean.valueOf(true), this.sgBreak);
    private final Setting<Boolean> strictDirections = this.setting("strict-directions", "Places only on visible sides.", Boolean.valueOf(false), this.sgRotations);
    private final Setting<Boolean> rotateChecks = this.setting("strict-direction", "Compatibility for servers that have rotation checks (won't always find a spot to place in).", Boolean.valueOf(false), this.sgRotations);
    private final Setting<Boolean> predict = this.setting("predict", "Predicts the target's movement.", Boolean.valueOf(false), this.sgTargeting);
    private final Setting<Boolean> smartDelay = this.setting("smart-delay", "Only places/breaks a bed when the target can actually take damage.", Boolean.valueOf(false), this.sgTargeting);
    private final Setting<Integer> hurtTimeThreshold = this.setting("hurt-time-threshold", "Thing for smart delay.", Integer.valueOf(0), this.sgTargeting, this.smartDelay::get);
    private final Setting<dev.saturn.addon.enums.venomhack.Type> antiFriendPop = this.setting("anti-friend-pop", "Takes into account damage to friends.", Type.BOTH, this.sgTargeting);
    private final Setting<Integer> targetRange = this.setting("target-range", "The maximum range for players to be targeted.", 10, this.sgTargeting, 17);
    private final Setting<SortPriority> sortPrio = this.setting("target-priority", "What targets to prioritize.", SortPriority.ClosestAngle, this.sgTargeting);
    private final Setting<Boolean> netDamage = this.setting("total-damage", "Adds up the damage dealt to all targets to find the most efficient location.", Boolean.valueOf(false), this.sgTargeting);
    private final Setting<Integer> maxTargets = this.setting("number-of-targets", "Maximum number of targets to perform calculations on. Might lag your game when set too high.", Integer.valueOf(3), this.sgTargeting, 1.0, 5.0);
    private final Setting<Boolean> pauseWhileCrafting = this.setting("pause-while-crafting", "Self explanatory.", Boolean.valueOf(true), this.sgPause);
    private final Setting<Boolean> pauseOnEat = this.setting("pause-on-eat", "Pauses while eating.", Boolean.valueOf(true), this.sgPause);
    private final Setting<Boolean> pauseOnDrink = this.setting("pause-on-drink", "Pauses while drinking potions.", Boolean.valueOf(false), this.sgPause);
    private final Setting<Boolean> pauseOnMine = this.setting("pause-on-mine", "Pauses while mining blocks.", Boolean.valueOf(false), this.sgPause);
    private final Setting<Boolean> ignoreTerrain = this.setting("ignore-terrain", "Assumes non blast resistant blocks are air.", Boolean.valueOf(false), this.sgMisc);
    private final Setting<Integer> minBedAmount = this.setting("min-bed-amount", "The minimum amount of beds to have in your inventory for this to work.", 0, this.sgMisc, 9);
    private final Setting<Boolean> toxic = this.setting("hole-trap", "Places an obsidian block on top of the target if it's in a hole.", Boolean.valueOf(true), this.sgMisc);
    private final Setting<Boolean> ultraToxic = this.setting("anti-vclip", "Makes it so the target cannot v-vclip out of the trap.", Boolean.valueOf(false), this.sgMisc, this.toxic::get);
    private final Setting<Boolean> autoSwitch = this.setting("auto-switch", "Switches to beds automatically.", Boolean.valueOf(true), this.sgMisc);
    private final Setting<Boolean> switchBack = this.setting("switch-back", "Switches back after placing.", Boolean.valueOf(true), this.sgMisc, this.autoSwitch::get);
    private final Setting<Boolean> autoMove = this.setting("auto-move", "Moves beds into the selected hotbar slot.", Boolean.valueOf(true), this.sgMisc);
    private final Setting<Integer> autoMoveSlot = this.setting("auto-move-slot", "The slot Auto Move moves beds to.", Integer.valueOf(9), this.sgMisc, this.autoMove::get, 1.0, 9.0, 1, 9);
    private final Setting<Boolean> autoCraft = this.setting("auto-craft", "Will automatically look for crafting tables nearby and open them.", Boolean.valueOf(false), this.sgCraft);
    private final Setting<Integer> craftDelay = this.setting("craft-delay", "Delay between craft attempts.", Integer.valueOf(10), this.sgCraft, this.autoCraft::get, 20.0);
    private final Setting<Boolean> placeTables = this.setting("place-crafting-tables", "Whether to place crafting tables if it can't find any.", Boolean.valueOf(true), this.sgCraft, this.autoCraft::get);
    private final Setting<Integer> minCraftHealth = this.setting("min-health", "The minimum health for auto crafting.", Integer.valueOf(10), this.sgCraft, this.autoCraft::get, 36.0);
    private final Setting<Boolean> craftSafe = this.setting("only-when-surrounded", "Will only try to auto craft when you are surrounded or burrowed.", Boolean.valueOf(false), this.sgCraft, this.autoCraft::get);
    private final Setting<Boolean> craftStill = this.setting("stand-still", "Will only auto craft when you are not moving.", Boolean.valueOf(true), this.sgCraft, this.autoCraft::get);
    private final Setting<Integer> refillThreshold = this.setting("refill-threshold", "At what bed count to open a crafting table.", Integer.valueOf(3), this.sgCraft, this.autoCraft::get);
    private final Setting<Integer> minBedCraftAmount = this.setting("min-bed-craft-amount", "How many beds you need to be able to craft minimum to open a crafting table.", Integer.valueOf(3), this.sgCraft, this.autoCraft::get);
    private final Setting<Integer> minMineHealth = this.setting("min-health", "The minimum health for the automation modules to work.", 10, this.sgAuto, 36);
    private final Setting<Mode> breakMode = this.setting("break-mode", "Which mode to use for mining blocks.", Mode.PACKET, this.sgAuto);
    private final Setting<Boolean> mineSafe = this.setting("only-when-surrounded", "Will only try to mine when you are surrounded or burrowed.", Boolean.valueOf(false), this.sgAuto);
    private final Setting<Boolean> burrowMine = this.setting("burrow-miner", "Will automatically mine the targets burrow block.", Boolean.valueOf(false), this.sgAuto);
    private final Setting<Boolean> selfTrapMine = this.setting("self-trap-miner", "Will automatically mine the targets self trap block.", Boolean.valueOf(false), this.sgAuto);
    private final Setting<Boolean> mineHead = this.setting("mine-above-head", "Whether to mine the block above the targets head if it can't find any other valid spots.", Boolean.valueOf(true), this.sgAuto, this.selfTrapMine::get);
    private final Setting<Boolean> antiAntiBed = this.setting("anti-anti-bed", "Will mine blocks that can prevent beds from being placed e.g. strings.", Boolean.valueOf(true), this.sgAuto);
    private final Setting<Boolean> oldMode = this.setting("1.12-mode", "Won't airplace.", Boolean.valueOf(false), this.sgExperimental);
    private final Setting<Boolean> monkeyMode = this.setting("dependant-delays", "Bowlz monkey delays.", Boolean.valueOf(false), this.sgExperimental);
    private final Setting<Boolean> noAirplace = this.setting("no-airplace", "Foot part of the bed will need a support block.", Boolean.valueOf(false), this.sgExperimental);
    private final Setting<Boolean> debugText = this.setting("debug-text", "debug.", Boolean.valueOf(false), this.sgExperimental);
    private final Setting<Boolean> swing = this.setting("swing", "Renders your swing client-side.", Boolean.valueOf(true), this.sgRender);
    private final Setting<Boolean> render = this.setting("render", "Renders the block where it is placing/breaking a bed.", Boolean.valueOf(true), this.sgRender);
    private final Setting<ShapeMode> shapeMode = this.setting("shape-mode", "How the shapes are rendered.", ShapeMode.Both, this.sgRender, this.render::get);
    private final Setting<Integer> maxRenderBeds = this.setting("max-render-beds", "How many beds to render at once max.", Integer.valueOf(1), this.sgRender, this.render::get, 1.0, 10.0, 1, Integer.MAX_VALUE);
    private final Setting<Integer> renderTime = this.setting("render-time", "For how long the placed position will be visible.", Integer.valueOf(10), this.sgRender, this.render::get, 20.0);
    private final Setting<Boolean> fade = this.setting("fade", "Will reduce the opacity of the rendered block over time.", Boolean.valueOf(true), this.sgRender, this.render::get);
    private final Setting<Boolean> shrink = this.setting("shrink", "Makes the beds become smaller the more time passed.", Boolean.valueOf(false), this.sgRender, this.render::get);
    private final Setting<Integer> beforeFadeDelay = this.setting("before-fade-or-shrink-delay", "How long to wait before starting to fade or shrink. This value should to be smaller than your render time.", Integer.valueOf(5), this.sgRender, () -> this.render.get() && (this.fade.get() || this.shrink.get()), 0.0, 10.0);
    private final Setting<SettingColor> sideColor = this.setting("side-color", "The side color for positions to be placed/broken.", 255, 255, 0, 75, this.sgRender, this.render::get);
    private final Setting<SettingColor> lineColor = this.setting("line-color", "The line color for positions to be placed/broken.", 255, 255, 0, 200, this.sgRender, this.render::get);
    private final Setting<Boolean> gradient = this.setting("gradient", "Will render a smooth transition between two colors.", Boolean.valueOf(false), this.sgRender, this.render::get);
    private final Setting<SettingColor> sideColor2 = this.setting("side-color2", "The side color for positions to be placed/broken.", 255, 255, 0, 75, this.sgRender, () -> this.render.get() && this.gradient.get());
    private final Setting<SettingColor> lineColor2 = this.setting("line-color2", "The line color for positions to be placed/broken.", 255, 255, 0, 200, this.sgRender, () -> this.render.get() && this.gradient.get());
    private final Setting<Boolean> renderDamage = this.setting("render-damage", "Renders the damage of the bed where it is placing.", Boolean.valueOf(true), this.sgRender, this.render::get);
    private final Setting<Integer> roundDamage = this.setting("round-damage", "Round damage to x decimal places.", Integer.valueOf(2), this.sgRender, () -> this.render.get() && this.renderDamage.get(), 3.0);
    private final Setting<Double> damageScale = this.setting("damage-scale", "The scale of the damage text.", Double.valueOf(1.5), this.sgRender, () -> this.render.get() && this.renderDamage.get(), 5.0);
    private final Setting<SettingColor> damageColor = this.setting("damage-color", "The color of the damage text.", 255, 255, 255, 255, this.sgRender, () -> this.render.get() && this.renderDamage.get());
    private final Setting<Boolean> renderMine = this.setting("render-mining", "Renders the blocks being mined.", Boolean.valueOf(true), this.sgRender, this.render::get);
    private final Setting<Boolean> mineFade = this.setting("mine-fade", "Will reduce the opacity of the rendered block over time for the block being mined.", Boolean.valueOf(true), this.sgRender, () -> this.render.get() && this.renderMine.get());
    private final Setting<SettingColor> mineSideColor = this.setting("mine-side-color", "The side color for the block being mined.", 255, 0, 0, 75, this.sgRender, () -> this.render.get() && this.renderMine.get());
    private final Setting<SettingColor> mineLineColor = this.setting("mine-line-color", "The line color for the block being mined.", 255, 0, 0, 200, this.sgRender, () -> this.render.get() && this.renderMine.get());
    private final List<PlayerEntity> targets = new ArrayList();
    private final List<PlayerEntity> friends = new ArrayList();
    private final List<BedRenderBlock> renderBeds = new ArrayList<>();
    private final List<RenderBlock> mineBlocks = new ArrayList<>();
    private boolean hasWarnedNoCrafter;
    private boolean hasWarnedNoMats;
    private BedData dumbBreak;
    private BedData dumbPlace;
    private BedData lastMineTarget;
    private BlockPos dumbCraft;
    private PlayerEntity target;
    private PlayerEntity trapHoldTarget;
    private int placeDelayLeft;
    private int breakDelayLeft;
    private int craftDelayLeft;
    private int mineTimeLeft;
    private int prevSlot;
    private Vec3d playerPos;
    private ExecutorService executor;

    public VHAutoBed() {
        super(Saturn.PVP, "VH-auto-bed", "Best bed aura in da game.");
    }

    private void mineBlock(BlockPos pos) {
        float[] rotation = PlayerUtils.calculateAngle(new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5));
        switch (this.breakMode.get()) {
            case BYPASS:
                if (this.rotateChecks.get()) {
                    Rotations.rotate(rotation[0], rotation[1], 25, () -> this.mc.interactionManager.updateBlockBreakingProgress(pos, BlockUtils2.getClosestDirection(pos, false)));
                } else {
                    this.mc.interactionManager.updateBlockBreakingProgress(pos, BlockUtils2.getClosestDirection(pos, false));
                }
                break;
            case INSTANT:
                if (this.rotateChecks.get()) {
                    Rotations.rotate(rotation[0], rotation[1], 25, () -> this.mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, pos, BlockUtils2.getClosestDirection(pos, false))));
                } else {
                    this.mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, pos, BlockUtils2.getClosestDirection(pos, false)));
                }
                break;
            default:
                UtilsPlus.mine(pos, this.swing.get(), this.rotateChecks.get());
        }

        RandUtils.swing(this.swing.get());
    }

    private void doPlaceThreaded() {
        if (this.placeDelayLeft <= 0) {
            label237:
            for (BlockEntity blockEntity : Utils.blockEntities()) {
                if (blockEntity instanceof BedBlockEntity && !this.mc.world.getBlockState(blockEntity.getPos()).isAir()) {
                    Vec3d footPos = new Vec3d((double) blockEntity.getPos().getX() + 0.5, (double) blockEntity.getPos().getY() + 0.5, (double) blockEntity.getPos().getZ() + 0.5);
                    Vec3d headPos = footPos.add(BedBlock.getDirection(this.mc.world, blockEntity.getPos()).getOffsetX(), 0.0, BedBlock.getDirection(this.mc.world, blockEntity.getPos()).getOffsetZ());
                    if ((this.inRange(footPos) || this.inRange(headPos)) && !BedBlock.getBedPart(this.mc.world.getBlockState(blockEntity.getPos())).equals(net.minecraft.block.DoubleBlockProperties.Type.FIRST) && !((double) DamageCalcUtils.explosionDamage(this.target, headPos, this.predict.get(), false, false, 5) < this.minBreakDamage.get()) && !((double) DamageCalcUtils.explosionDamage(this.target, headPos, this.ignoreTerrain.get(), this.predict.get(), true, 5) < this.minBreakDamage.get())) {
                        float multiSelfDmg = DamageCalcUtils.explosionDamage(this.mc.player, headPos, false, this.ignoreTerrain.get(), true, 5);
                        if (!((double) multiSelfDmg > this.maxBreakSelfDamage.get()) && (!this.noSuicideBreak.get() || !(PlayerUtils.getTotalHealth() - (double) multiSelfDmg < 0.5))) {
                            if (!this.antiFriendPop.get().breakTrue()) {
                                return;
                            }

                            for (PlayerEntity friend : this.friends) {
                                if (!((double) DamageCalcUtils.explosionDamage(friend, headPos, this.predict.get(), false, false, 5) < this.maxBreakSelfDamage.get())) {
                                    double friendDmg = DamageCalcUtils.explosionDamage(friend, headPos, this.predict.get(), this.ignoreTerrain.get(), true, 5);
                                    if (friendDmg > this.maxBreakSelfDamage.get() || this.noSuicideBreak.get() && (double) EntityUtils.getTotalHealth(friend) - friendDmg < 0.5) {
                                        continue label237;
                                    }
                                }
                            }

                            return;
                        }
                    }
                }
            }

            if (this.trapHoldTarget != null && (this.trapHoldTarget.isDead() || (double) this.trapHoldTarget.distanceTo(this.mc.player) > this.range.get() + 1.0 || this.trapHoldTarget.distanceTo(this.mc.player) > (float) this.targetRange.get().intValue() || !UtilsPlus.isSurrounded(this.trapHoldTarget, true, false))) {
                this.trapHoldTarget = null;
            }

            BedData bestBed = new BedData();
            List<PlayerEntity> getPooped = new ArrayList();
            int range = (int) Math.ceil(this.range.get() + 1.0);
            Vec3d origin = this.placeOrigin.get().getOrigin(this.playerPos);
            this.dumbBreak = null;

            if (bestBed.pos != null) {
                this.target = bestBed.target;
                if (this.debugText.get()) {
                    this.info("normal place: " + bestBed.placeDirection);
                }

                this.placeBed(bestBed, false);
            }
        }
    }

    private void doBreak() {
        if (this.breakDelayLeft <= 0) {
            this.dumbPlace = null;
            BedData bestBed = new BedData();

            label125:
            for (BlockEntity blockEntity : Utils.blockEntities()) {
                if (blockEntity instanceof BedBlockEntity && !this.mc.world.getBlockState(blockEntity.getPos()).isAir() && !BedBlock.getBedPart(this.mc.world.getBlockState(blockEntity.getPos())).equals(net.minecraft.block.DoubleBlockProperties.Type.FIRST)) {
                    Vec3d footPos = new Vec3d((double) blockEntity.getPos().getX() + 0.5, (double) blockEntity.getPos().getY() + 0.5, (double) blockEntity.getPos().getZ() + 0.5);
                    Vec3d headPos = footPos.add(BedBlock.getDirection(this.mc.world, blockEntity.getPos()).getOffsetX(), 0.0, BedBlock.getDirection(this.mc.world, blockEntity.getPos()).getOffsetZ());
                    boolean headInRange = this.inRange(headPos);
                    boolean feetInRange = this.inRange(footPos);
                    if (headInRange || feetInRange) {
                        double selfDmg = DamageCalcUtils.explosionDamage(this.mc.player, headPos, this.predict.get(), this.ignoreTerrain.get(), true, 5);
                        if (!(selfDmg > this.maxBreakSelfDamage.get()) && (!this.noSuicideBreak.get() || !(PlayerUtils.getTotalHealth() - selfDmg < 0.5))) {
                            if (this.antiFriendPop.get().breakTrue()) {
                                for (PlayerEntity friend : this.friends) {
                                    if (!((double) DamageCalcUtils.explosionDamage(friend, headPos, this.predict.get(), false, false, 5) < this.maxBreakSelfDamage.get())) {
                                        double friendDmg = DamageCalcUtils.explosionDamage(friend, headPos, this.predict.get(), this.ignoreTerrain.get(), true, 5);
                                        if (friendDmg > this.maxBreakSelfDamage.get() || this.noSuicideBreak.get() && (double) EntityUtils.getTotalHealth(friend) - friendDmg < 0.5) {
                                            continue label125;
                                        }
                                    }
                                }
                            }

                            float totalDamage = 0.0F;
                            boolean enoughSingleDamage = false;

                            for (PlayerEntity target : this.targets) {
                                if (!this.smartDelay.get() || target.hurtTime <= this.hurtTimeThreshold.get()) {
                                    if (!this.netDamage.get()) {
                                        float simpleDmg = DamageCalcUtils.explosionDamage(target, headPos, this.predict.get(), false, false, 5);
                                        if ((double) simpleDmg < this.minBreakDamage.get() || simpleDmg < bestBed.damage) {
                                            continue;
                                        }
                                    }

                                    float damage = DamageCalcUtils.explosionDamage(target, headPos, this.predict.get(), this.ignoreTerrain.get(), true, 5);
                                    if (this.netDamage.get()) {
                                        totalDamage += damage;
                                    } else if (damage < bestBed.damage) {
                                        continue;
                                    }

                                    if (!((double) damage < this.minBreakDamage.get())) {
                                        enoughSingleDamage = true;
                                        if (!this.netDamage.get()) {
                                            bestBed.set(new Mutable(this.footOrHead(footPos, headPos).x, this.footOrHead(footPos, headPos).y, this.footOrHead(footPos, headPos).z), BedBlock.getDirection(this.mc.world, blockEntity.getPos()), damage, target);
                                        }
                                    }
                                }
                            }

                            if (this.netDamage.get() && enoughSingleDamage && !(totalDamage < bestBed.damage)) {
                                bestBed.set(new Mutable(this.footOrHead(footPos, headPos).x, this.footOrHead(footPos, headPos).y, this.footOrHead(footPos, headPos).z), BedBlock.getDirection(this.mc.world, blockEntity.getPos()), totalDamage, this.trapHoldTarget == null ? this.targets.get(0) : this.trapHoldTarget);
                            }
                        }
                    }
                }
            }

            if (bestBed.pos != null) {
                this.target = bestBed.target;
                if (this.rotateChecks.get()) {
                    Rotations.rotate(Rotations.getYaw(bestBed.pos), Rotations.getPitch(bestBed.pos), 50, false, () -> this.breakBed(bestBed, false));
                } else {
                    this.breakBed(bestBed, false);
                }
            }
        }
    }

    private Vec3d footOrHead(Vec3d feet, Vec3d head) {
        return this.inRange(feet) ? feet : head;
    }

    private void openTable(BlockPos blockPos) {
        if (this.debugText.get()) {
            this.info("Attempting to open crafting table at " + blockPos + ".");
        }

        boolean wasSneaking = this.mc.player.isSneaking();
        if (wasSneaking) {
            this.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(this.mc.player, net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }

        if (this.rotateChecks.get()) {
            RandUtils.rotate(blockPos, () -> this.mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(blockPos), BlockUtils2.getClosestDirection(blockPos, false), blockPos, true), 0)));
        } else {
            this.mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(blockPos), BlockUtils2.getClosestDirection(blockPos, false), blockPos, true), 0));
        }

        RandUtils.swing(this.swing.get());
        if (wasSneaking) {
            this.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(this.mc.player, net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
    }

    private void doPlace(FindItemResult result, BlockPos pos) {
        boolean airPlace = !this.noAirplace.get() && !this.oldMode.get();
        BlockPos neighbor = pos;
        Direction placeSide = BlockUtils2.getClosestDirection(pos, !airPlace);
        if (!airPlace) {
            if (this.oldMode.get()) {
                neighbor = pos.down();
                placeSide = Direction.UP;
            } else {
                neighbor = pos.offset(placeSide);
                placeSide = placeSide.getOpposite();
            }
        }

        if (result.getHand() == null) {
            InvUtils.swap(result.slot(), this.switchBack.get());
        }

        Hand hand = RandUtils.hand(result);
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos), placeSide, neighbor, false);
        boolean sneak = !this.mc.player.isSneaking() && BlockUtils2.clickableBlock(this.mc.world.getBlockState(hit.getBlockPos()), hit, hand);
        if (sneak) {
            this.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(this.mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }

        this.mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, hit, 0));
        RandUtils.swing(this.swing.get(), hand);
        if (sneak) {
            this.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(this.mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }

        if (this.switchBack.get()) {
            InvUtils.swapBack();
        }
    }

    private void placeBed(BedData bedData, boolean fastPlace) {
        FindItemResult result = InvUtils.find(itemStack -> itemStack.getItem() instanceof BedItem);
        if (result.found()) {
            if (result.isMain() && this.autoMove.get()) {
                this.doAutoMove();
            }

            if (!result.isHotbar() && !result.isOffhand()) {
                result = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof BedItem);
            }

            if (result.found() && (result.getHand() != null || this.autoSwitch.get())) {
                BedRenderBlock.addBedRender(this.renderBeds, bedData, this.renderTime.get(), this.maxRenderBeds.get());
                FindItemResult finalRes = result;
                if (this.rotateChecks.get()) {
                    Vec3d vec = Vec3d.ofCenter(bedData.pos).add(Vec3d.ofCenter(bedData.placeDirection.getVector()).multiply(0.5));
                    Rotations.rotate(Rotations.getYaw(vec), Rotations.getPitch(bedData.pos), 50, () -> this.doPlace(finalRes, bedData.pos));
                } else {
                    Rotations.rotate(UtilsPlus.yawFromDir(bedData.placeDirection), Rotations.getPitch(bedData.pos), 50, () -> this.doPlace(finalRes, bedData.pos));
                }

                if (UtilsPlus.isSurrounded(bedData.target, true, true) && UtilsPlus.isSelfTrapBlock(bedData.target, bedData.pos)) {
                    if (this.toxic.get()) {
                        FindItemResult obby = InvUtils.findInHotbar(Items.OBSIDIAN);
                        if (this.inRange(bedData.target.getBlockPos().up(2))) {
                            BlockUtils2.placeBlock(obby, bedData.target.getBlockPos().up(2), this.rotateChecks.get(), 20, !this.oldMode.get() && !this.noAirplace.get(), false, this.swing.get(), this.strictDirections.get());
                        }

                        if (this.ultraToxic.get()) {
                            if (this.inRange(bedData.target.getBlockPos().up(4))) {
                                BlockUtils2.placeBlock(obby, bedData.target.getBlockPos().up(4), this.rotateChecks.get(), 15, !this.oldMode.get() && !this.noAirplace.get(), false, this.swing.get(), this.strictDirections.get());
                            }

                            if (this.inRange(bedData.target.getBlockPos().up(5))) {
                                BlockUtils2.placeBlock(obby, bedData.target.getBlockPos().up(5), this.rotateChecks.get(), 15, !this.oldMode.get() && !this.noAirplace.get(), false, this.swing.get(), this.strictDirections.get());
                            }
                        }
                    }

                    this.placeDelayLeft = this.holePlaceDelay.get();
                    if (this.monkeyMode.get()) {
                        this.breakDelayLeft = this.holeBreakDelay.get();
                    }

                    if (this.holeBreakDelay.get() > 0) {
                        this.trapHoldTarget = bedData.target;
                    }
                } else {
                    this.placeDelayLeft = this.placeDelay.get();
                    if (this.monkeyMode.get()) {
                        this.breakDelayLeft = this.breakDelay.get();
                    }
                }

                if (!fastPlace && bedData.target.isAlive() && this.targets.contains(bedData.target) && bedData.damage != 0.0F) {
                    this.dumbBreak = bedData;
                }
            }
        }
    }

    private void breakBed(BedData data, boolean fastBreak) {
        boolean wasSneaking = this.mc.player.isSneaking();
        if (wasSneaking) {
            this.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(this.mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }

        Hand hand = this.mc.player.getMainHandStack().getItem() instanceof BlockItem ? Hand.OFF_HAND : Hand.MAIN_HAND;
        this.mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, new BlockHitResult(Vec3d.ofCenter(data.pos), BlockUtils2.getClosestDirection(data.pos, false), data.pos, false), 0));
        RandUtils.swing(this.swing.get(), hand);
        if (wasSneaking) {
            this.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(this.mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }

        if (UtilsPlus.isSurrounded(data.target, true, false) && UtilsPlus.isSelfTrapBlock(data.target, data.pos)) {
            this.breakDelayLeft = this.holeBreakDelay.get();
            if (this.monkeyMode.get()) {
                this.placeDelayLeft = this.holePlaceDelay.get();
            }
        } else {
            this.breakDelayLeft = this.breakDelay.get();
            if (this.monkeyMode.get()) {
                this.placeDelayLeft = this.placeDelay.get();
            }
        }

        if (!fastBreak && data.target.isAlive() && this.targets.contains(data.target) && data.placeDirection != null) {
            this.dumbPlace = data;
        }
    }

    private float getBedDamage(Vec3d bedPos) {
        float damage = 0.0F;

        for (PlayerEntity target : this.targets) {
            float dmg = DamageCalcUtils.explosionDamage(target, bedPos, this.predict.get(), this.ignoreTerrain.get(), true, 5);
            if (this.netDamage.get()) {
                damage += dmg;
            } else if (dmg > damage) {
                damage = dmg;
            }
        }

        return damage;
    }

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        this.lastMineTarget = null;
    }

    private boolean isGoodTarget(PlayerEntity target) {
        return target != null && target.isAlive() && target.isInRange(this.mc.player, this.targetRange.get().intValue()) && (!this.onlyHoled.get() || UtilsPlus.isSurrounded(target, true, false));
    }

    private boolean inRange(BlockPos blockPos) {
        return this.inRange(Vec3d.ofCenter(blockPos));
    }

    private boolean inRange(Vec3d pos) {
        if (pos == null) {
            
        } else {
            double distance = this.placeOrigin.get().getOrigin(this.playerPos).distanceTo(pos);
            if (UtilsPlus.cantSee(new Mutable(pos.x, pos.y, pos.z), this.strictDirections.get())) {
                return distance <= this.wallsRange.get();
            } else {
                return distance <= this.range.get();
            }
        }
        return false;
    }

    private void doAutoMove() {
        if (!InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof BedItem).found()) {
            int slot = InvUtils.find(itemStack -> itemStack.getItem() instanceof BedItem).slot();
            InvUtils.move().from(slot).toHotbar(this.autoMoveSlot.get() - 1);
        }
    }

    private boolean shouldPause() {
        if (this.pauseAll()) {
            return true;
        } else {
            FindItemResult beds = InvUtils.find(itemStack -> itemStack.getItem() instanceof BedItem);
            return beds.count() < this.minBedAmount.get() || beds.count() <= 0;
        }
    }

    private boolean pauseAll() {
        return this.pauseWhileCrafting.get() && this.mc.currentScreen instanceof CraftingScreen || PlayerUtils.shouldPause(this.pauseOnMine.get(), this.pauseOnEat.get(), this.pauseOnDrink.get());
    }

    public boolean active() {
        return !this.shouldPause() && this.place.get() && !this.targets.isEmpty() && this.isActive();
    }

    public void onActivate() {
        this.placeDelayLeft = 0;
        this.breakDelayLeft = 0;
        this.craftDelayLeft = 0;
        this.mineTimeLeft = -1;
        this.prevSlot = -1;
        this.hasWarnedNoMats = false;
        this.hasWarnedNoCrafter = false;
        this.target = null;
        this.dumbBreak = null;
        this.dumbPlace = null;
        this.trapHoldTarget = null;
        this.lastMineTarget = null;
        this.dumbCraft = null;
        this.targets.clear();
        this.friends.clear();
        this.renderBeds.clear();
        this.mineBlocks.clear();
        this.executor = Executors.newSingleThreadExecutor();
        
    }

    public void onDeactivate() {
        this.target = null;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (this.render.get()) {
            try {
                for (BedRenderBlock block : this.renderBeds) {
                    if (this.gradient.get()) {
                        RenderUtils2.renderBed(event.renderer, this.sideColor.get(), this.sideColor2.get(), this.lineColor.get(), this.lineColor2.get(), this.shapeMode.get(), this.fade.get(), block.ticks, this.renderTime.get(), this.beforeFadeDelay.get(), block.placeDirection, block.pos, this.shrink.get());
                    } else {
                        RenderUtils2.renderBed(event.renderer, this.sideColor.get(), this.sideColor.get(), this.lineColor.get(), this.lineColor.get(), this.shapeMode.get(), this.fade.get(), block.ticks, this.renderTime.get(), this.beforeFadeDelay.get(), block.placeDirection, block.pos, this.shrink.get());
                    }
                }

                if (this.renderMine.get()) {
                    for (RenderBlock renderBlock : this.mineBlocks) {
                        renderBlock.render(event, this.mineSideColor.get(), this.mineLineColor.get(), this.shapeMode.get(), this.mineFade.get());
                    }
                }
            } catch (ConcurrentModificationException var4) {
                if (this.debugText.get()) {
                    this.warning("cme");
                }
            }
        }
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        if (this.render.get() && this.renderDamage.get() && !this.renderBeds.isEmpty()) {
            int preA = this.damageColor.get().a;

            try {
                for (BedRenderBlock block : this.renderBeds) {
                    Vector3d pos = new Vector3d((double) block.pos.getX() + (double) block.placeDirection.getOffsetX() * 0.5 + 0.5, (double) block.pos.getY() + 0.3, (double) block.pos.getZ() + (double) block.placeDirection.getOffsetZ() * 0.5 + 0.5);
                    if (NametagUtils.to2D(pos, this.damageScale.get())) {
                        NametagUtils.begin(pos);
                        TextRenderer.get().begin(1.0, false, true);
                        String damageText = String.valueOf(TextUtils.round(block.damage, this.roundDamage.get()));
                        double w = TextRenderer.get().getWidth(damageText) * 0.5;
                        if (this.fade.get()) {
                            SettingColor var10000 = this.damageColor.get();
                            var10000.a = (int) ((float) var10000.a * (block.ticks / (float) this.renderTime.get().intValue()));
                        }

                        TextRenderer.get().render(damageText, -w, 0.0, this.damageColor.get(), true);
                        TextRenderer.get().end();
                        NametagUtils.end();
                        this.damageColor.get().a = preA;
                    }
                }
            } catch (Exception var10) {
            }
        }
    }

    @Override
    public PlayerEntity getTarget() {
        return this.target == null ? null : this.target;
    }

    private static class BedData {
        public BlockPos pos;
        public Direction placeDirection;
        public float damage;
        public PlayerEntity target;

        public BedData() {
        }

        public BedData(BlockPos pos, Direction placeDirection, float damage, PlayerEntity target) {
            this.pos = pos;
            this.placeDirection = placeDirection;
            this.damage = damage;
            this.target = target;
        }

        public void set(BlockPos pos, Direction placeDirection, float damage, PlayerEntity target) {
            this.pos = pos;
            this.placeDirection = placeDirection;
            this.damage = damage;
            this.target = target;
        }
    }

    private static class BedRenderBlock extends RenderBlock {
        public final Direction placeDirection;

        public BedRenderBlock(BlockPos pos, Direction placeDirection, int ticks, float damage) {
            super(pos, ticks, damage);
            this.placeDirection = placeDirection;
        }

        public static synchronized void addBedRender(List<BedRenderBlock> beds, BedData bedData, int renderTime, int maxBeds) {
            AtomicBoolean found = new AtomicBoolean(false);
            synchronized (beds) {
                beds.forEach(bed -> {
                    if (bed.pos.equals(bedData.pos) && bed.placeDirection.equals(bedData.placeDirection)) {
                        bed.set(renderTime, bedData.damage);
                        found.set(true);
                    }
                });
                if (!found.get()) {
                    if (beds.size() > 0) {
                        while (beds.size() >= maxBeds) {
                            beds.remove(0);
                        }
                    }

                    beds.add(new BedRenderBlock(bedData.pos, bedData.placeDirection, renderTime, bedData.damage));
                }
            }
        }

        public static void bedTick(List<BedRenderBlock> renderBlocks) {
            renderBlocks.forEach(block -> --block.ticks);
            renderBlocks.removeIf(block -> block.ticks <= 0.0F);
        }
    }
}