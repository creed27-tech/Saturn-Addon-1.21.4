package dev.saturn.addon.modules.Combat;

import dev.saturn.addon.utils.atlas.Checker;
import dev.saturn.addon.utils.atlas.InitializeUtils;
import dev.saturn.addon.utils.atlas.enchansed.Render2Utils;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.*;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoCityRewrite extends Module {
    private final SettingGroup sgGeneral;
    private final SettingGroup sgRender;
    public final Setting<Double> targetRange;
    public final Setting<Double> range;
    private final Setting<Boolean> support;
    public final Setting<Boolean> old;
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> delayed;
    private final Setting<Boolean> selfToggle;
    private final Setting<Boolean> swing;
    private final Setting<Boolean> render;
    private final Setting<Boolean> thick;
    private final Setting<ShapeMode> shapeMode;
    private final Setting<SettingColor> sideColor;
    private final Setting<SettingColor> lineColor;
    private final Setting<Boolean> renderProgress;
    private final Setting<Double> progressScale;
    private final Setting<SettingColor> progressColor;
    private PlayerEntity target;
    private BlockPos blockPosTarget;
    private int timer;
    private int max;
    private boolean firstTime;

    public int i = 0;

    public AutoCityRewrite() {
        super(Categories.Combat, "auto-city-rewrite", "Automatically cities a target by mining the nearest obsidian next to them. | Ported from Atlas");
        sgGeneral = settings.getDefaultGroup();
        sgRender = settings.createGroup("Render");
        targetRange = sgGeneral.add((Setting<Double>) new DoubleSetting.Builder().name("target-range").description("The radius in which players get targeted.").defaultValue(4.0).min(0.0).sliderMax(5.0).build());
        range = sgGeneral.add((Setting<Double>) new DoubleSetting.Builder().name("range").description("The radius in which blocks are allowed to get broken").defaultValue(5.0).min(0.0).sliderMax(6.0).build());
        support = sgGeneral.add( new BoolSetting.Builder().name("support").description("If there is no block below a city block it will place one before mining.").defaultValue(true).build());
        old = sgGeneral.add( new BoolSetting.Builder().name("1.12-mode").description("Requires an air block above in order to target it.").defaultValue(false).build());
        rotate = sgGeneral.add( new BoolSetting.Builder().name("rotate").description("Automatically rotates you towards the city block.").defaultValue(true).build());
        delayed = sgGeneral.add( new BoolSetting.Builder().name("delayed-switch").description("Will only switch to the pickaxe when the block is ready to be broken.").defaultValue(true).build());
        selfToggle = sgGeneral.add( new BoolSetting.Builder().name("self-toggle").description("Automatically toggles off after activation.").defaultValue(true).build());
        swing = sgRender.add( new BoolSetting.Builder().name("swing").description("Renders your swing client-side.").defaultValue(true).build());
        render = sgRender.add( new BoolSetting.Builder().name("render").description("Renders the block you are mining.").defaultValue(true).build());
        thick = sgRender.add(new BoolSetting.Builder().name("thick").defaultValue(true).build());
        shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>().name("shape-mode").description("How the shapes are rendered.").visible(render::get).defaultValue(ShapeMode.Sides).build());
        sideColor = sgRender.add((Setting<SettingColor>) new ColorSetting.Builder().name("side-color").description("The side color.").visible(render::get).defaultValue(new SettingColor(255, 0, 0, 75, true)).build());
        lineColor = sgRender.add((Setting<SettingColor>) new ColorSetting.Builder().name("line-color").description("The line color.").visible(render::get).defaultValue(new SettingColor(255, 0, 0, 200)).build());
        renderProgress = sgRender.add( new BoolSetting.Builder().name("render-progress").description("Renders the block break progress").defaultValue(true).build());
        progressScale = sgRender.add((Setting<Double>) new DoubleSetting.Builder().name("progress-scale").description("The scale of the progress text.").visible(renderProgress::get).defaultValue(1.4).min(0.0).sliderMax(5.0).build());
        progressColor = sgRender.add((Setting<SettingColor>) new ColorSetting.Builder().name("progress-color").description("The color of the progress text.").visible(renderProgress::get).defaultValue(new SettingColor(0, 0, 0, 255)).build());
    }

    @Override
    public void onActivate() {
        Checker.Check();

        timer = 0;
        max = 0;
        target = null;
        blockPosTarget = null;
        firstTime = true;

        i = 0;
    }

    @Override
    public void onDeactivate() {
        if (blockPosTarget == null) {
            return;
        }
        mc.player.networkHandler.sendPacket((Packet) new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, blockPosTarget, Direction.UP));

        Checker.Check();
    }

    @EventHandler
    private void onTick(final TickEvent.Pre event) {
        if (i == 0) {
            InitializeUtils.banana();
            i++;
        }
        timer--;
        if (TargetUtils.isBadTarget(target, targetRange.get())) {
            target = TargetUtils.getPlayerTarget(targetRange.get(), SortPriority.LowestDistance);
        }
        if (TargetUtils.isBadTarget(target, targetRange.get())) {
            target = null;
            blockPosTarget = null;
            timer = 0;
            firstTime = true;
            if (selfToggle.get()) {
                ChatUtils.error(name, "No target found... disabling.");
                toggle();
            }
            return;
        }
    }

    private void mine(final BlockPos blockPos) {
        mc.getNetworkHandler().sendPacket((Packet) new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.UP));
        if (swing.get()) {
            mc.player.swingHand(Hand.MAIN_HAND);
        } else {
            mc.player.networkHandler.sendPacket((Packet) new HandSwingC2SPacket(Hand.MAIN_HAND));
        }
        mc.getNetworkHandler().sendPacket((Packet) new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        if (blockPosTarget == null || !render.get() || target == null || mc.player.getAbilities().creativeMode) {
            return;
        }

        if (thick.get()) {
            Render2Utils.thick_box(event, blockPosTarget, sideColor.get(), lineColor.get(), shapeMode.get());
        } else {
            event.renderer.box(blockPosTarget, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        }
    }
}