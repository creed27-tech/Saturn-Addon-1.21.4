package dev.saturn.addon.modules.Combat;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class BedrockWalk extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Double> activationWindow;
    private final Setting<Integer> driftToHeight;
    private final Setting<Double> horizontalPullStrength;
    private final Setting<Double> verticalPullStrength;
    private final Setting<Integer> searchRadius;
    private final Setting<Boolean> updatePositionFailsafe;
    private final Setting<Double> failsafeWindow;
    private final Setting<Double> successfulLandingMargin;
    private final BlockPos blockPos;
    private final ArrayList<BlockPos> validBlocks;
    private final TreeMap<Double, BlockPos> sortedBlocks;
    private boolean successfulLanding;

    public BedrockWalk() {
        super(Categories.Combat, "bedrock-walk", "Makes moving on bedrock easier.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.activationWindow = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("activation-window"))
                .description("The area above the target Y level at which pull activates.")).min(0.2D).max(5.0D).sliderMin(0.2D).sliderMax(5.0D).defaultValue(0.5D).build());
        this.driftToHeight = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("drift-to-height"))
                .description("Y level to find blocks to drift onto.")).min(0).max(256).sliderMin(0).sliderMax(256).defaultValue(5)).build());
        this.horizontalPullStrength = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("horizontal-pull"))
                .description("The horizontal speed/strength at which you drift to the goal block.")).min(0.1D).max(10.0D).sliderMin(0.1D).sliderMax(10.0D).defaultValue(1.0D).build());
        this.verticalPullStrength = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vertical-pull"))
                .description("The vertical speed/strength at which you drift to the goal block.")).min(0.1D).max(10.0D).sliderMin(0.1D).sliderMax(10.0D).defaultValue(1.0D).build());
        this.searchRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("search-radius"))
                .description("The radius at which envy mode searches for blocks (odd numbers only).")).min(3).max(15).sliderMin(3).sliderMax(15).defaultValue(3)).build());
        this.updatePositionFailsafe = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("failsafe"))
                .description("Updates your position to the top of the target block if you miss the jump.")).defaultValue(true)).build());
        this.failsafeWindow = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("failsafe-window"))
                .description("Window below the target block to fall to trigger failsafe.")).min(0.01D).max(1.0D).sliderMin(0.01D).sliderMax(1.0D).defaultValue(0.1D).build());
        this.successfulLandingMargin = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("landing-margin"))
                .description("The distance from a landing block to be considered a successful landing.")).min(0.01D).max(10.0D).sliderMin(0.01D).sliderMax(10.0D).defaultValue(1.0D).build());
        this.blockPos = new BlockPos(0, 0, 0);
        this.validBlocks = new ArrayList<>();
        this.sortedBlocks = new TreeMap<>();
    }

    public void onActivate() {
        if (this.searchRadius.get() % 2 == 0) {
            this.info("%d is not valid for radius, rounding up", this.searchRadius.get());
            this.searchRadius.set(this.searchRadius.get() + 1);
        }
    }
}