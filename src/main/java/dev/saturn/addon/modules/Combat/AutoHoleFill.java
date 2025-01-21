package dev.saturn.addon.modules.Combat;

import dev.saturn.addon.ov4Module;
import dev.saturn.addon.enums.lemon.HoleType;
import dev.saturn.addon.enums.lemon.RotationType;
import dev.saturn.addon.enums.lemon.SwingHand;
import dev.saturn.addon.managers.lemon.Managers;
import dev.saturn.addon.utils.lemon.ov4Utils;
import dev.saturn.addon.utils.lemon.SettingUtils;
import dev.saturn.addon.utils.lemon.player.InventoryUtils;
import dev.saturn.addon.utils.lemon.player.PlaceData;
import dev.saturn.addon.utils.lemon.timers.TimerList;
import dev.saturn.addon.utils.lemon.world.hole.Hole;
import dev.saturn.addon.utils.lemon.world.hole.HoleUtils;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;

import java.io.FileDescriptor;
import java.util.*;

public class AutoHoleFill extends ov4Module {
    public AutoHoleFill() {
        super(Categories.Combat, "auto-hole-fill", "Automatically is a cunt to your enemies. | Ported from Lemon Client");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgPlacing = settings.createGroup("Placing");
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgHole = settings.createGroup("Hole");

    //--------------------General--------------------//
    private final Setting<Boolean> pauseEat = sgGeneral.add(new BoolSetting.Builder()
            .name("Pause On Eat")
            .description("Pauses when you are eating")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> efficient = sgGeneral.add(new BoolSetting.Builder()
            .name("Efficient")
            .description("Only places if the hole is closer to target")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> above = sgGeneral.add(new BoolSetting.Builder()
            .name("Above")
            .description("Only places if target is above the hole")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> iHole = sgGeneral.add(new BoolSetting.Builder()
            .name("Ignore Hole")
            .description("Doesn't place if enemy is in a hole")
            .defaultValue(true)
            .build()
    );
    private final Setting<Double> holeRange = sgGeneral.add(new DoubleSetting.Builder()
            .name("Hole Range")
            .description("Places when enemy is close enough to target hole")
            .defaultValue(3)
            .min(0)
            .sliderMax(10)
            .build()
    );

    //--------------------Placing--------------------//
    private final Setting<SwitchMode> switchMode = sgPlacing.add(new EnumSetting.Builder<SwitchMode>()
            .name("Switch Mode")
            .description("Method of switching. Silent is the most reliable but delays crystals on some servers.")
            .defaultValue(SwitchMode.Silent)
            .build()
    );
    private final Setting<List<Block>> blocks = sgPlacing.add(new BlockListSetting.Builder()
            .name("Blocks")
            .description("Which blocks to use.")
            .defaultValue(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK)
            .build()
    );
    private final Setting<Double> placeDelay = sgPlacing.add(new DoubleSetting.Builder()
            .name("Place Delay")
            .description("Delay between places.")
            .defaultValue(0.125)
            .min(0)
            .sliderRange(0, 10)
            .build()
    );
    private final Setting<Integer> places = sgPlacing.add(new IntSetting.Builder()
            .name("Places")
            .description("Blocks placed per place")
            .defaultValue(1)
            .min(1)
            .sliderRange(1, 10)
            .build()
    );
    private final Setting<Double> delay = sgPlacing.add(new DoubleSetting.Builder()
            .name("Delay")
            .description("Delay between places at single spot.")
            .defaultValue(1)
            .min(0)
            .sliderRange(0, 10)
            .build()
    );

    //--------------------Hole--------------------//
    private final Setting<Boolean> single = sgHole.add(new BoolSetting.Builder()
            .name("Single")
            .description("Fills 1x1 holes")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> doubleHole = sgHole.add(new BoolSetting.Builder()
            .name("Double")
            .description("Fills 2x1 block holes")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> quad = sgHole.add(new BoolSetting.Builder()
            .name("Quad")
            .description("Fills 2x2 block holes")
            .defaultValue(true)
            .build()
    );

    //--------------------Render--------------------//
    private final Setting<Boolean> placeSwing = sgRender.add(new BoolSetting.Builder()
            .name("Swing")
            .description("Renders swing animation when placing a block.")
            .defaultValue(true)
            .build()
    );
    private final Setting<SwingHand> placeHand = sgRender.add(new EnumSetting.Builder<SwingHand>()
            .name("Swing Hand")
            .description("Which hand should be swung.")
            .defaultValue(SwingHand.RealHand)
            .visible(placeSwing::get)
            .build()
    );
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("Shape Mode")
            .description(".")
            .defaultValue(ShapeMode.Both)
            .build()
    );
    private final Setting<Double> renderTime = sgRender.add(new DoubleSetting.Builder()
            .name("Render Time")
            .description("How long the box should remain in full alpha.")
            .defaultValue(0.3)
            .min(0)
            .sliderRange(0, 10)
            .build()
    );
    private final Setting<Double> fadeTime = sgRender.add(new DoubleSetting.Builder()
            .name("Fade Time")
            .description("How long the fading should take.")
            .defaultValue(1)
            .min(0)
            .sliderRange(0, 10)
            .build()
    );
    public final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("Line Color")
            .description("Color of the outline.")
            .defaultValue(new SettingColor(255, 255, 255, 255))
            .build()
    );
    public final Setting<SettingColor> color = sgRender.add(new ColorSetting.Builder()
            .name("Color")
            .description("Color of the sides.")
            .defaultValue(new SettingColor(255, 255, 255, 50))
            .build()
    );

    private List<BlockPos> holes = new ArrayList<>();
    private final TimerList<BlockPos> timers = new TimerList<>();
    private double placeTimer = 0;
    private final Map<BlockPos, Double[]> toRender = new HashMap<>();

    @Override
    public void onActivate() {
        super.onActivate();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onRender(Render3DEvent event) {
        timers.update();

        double d = event.frameTime;
        if (mc.player != null && mc.world != null) {
            placeTimer = Math.min(placeTimer + event.frameTime, placeDelay.get());
            update();

            List<BlockPos> toRemove = new ArrayList<>();
            for (Map.Entry<BlockPos, Double[]> entry : toRender.entrySet()) {
                BlockPos pos = entry.getKey();
                Double[] alpha = entry.getValue();
                if (alpha[0] <= d) {
                    toRemove.add(pos);
                } else {
                    event.renderer.box(Box.from(new BlockBox(pos)),
                            new Color(color.get().r, color.get().g, color.get().b, (int) Math.round(color.get().a * Math.min(1, alpha[0] / alpha[1]))),
                            new Color(lineColor.get().r, lineColor.get().g, lineColor.get().b, (int) Math.round(lineColor.get().a * Math.min(1, alpha[0] / alpha[1]))), shapeMode.get(), 0);
                    entry.setValue(new Double[]{alpha[0] - d, alpha[1]});
                }
            }
            toRemove.forEach(toRender::remove);
        }
    }

    private void update() {
        List<BlockPos> placements = getValid(holes);

        FindItemResult result = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof BlockItem && blocks.get().contains(((BlockItem) itemStack.getItem()).getBlock()));
        FindItemResult invResult = InvUtils.find(itemStack -> itemStack.getItem() instanceof BlockItem && blocks.get().contains(((BlockItem) itemStack.getItem()).getBlock()));
        Hand hand = isValid(Managers.HOLDING.getStack()) ? Hand.MAIN_HAND : isValid(mc.player.getOffHandStack()) ? Hand.OFF_HAND : null;

        if (!placements.isEmpty() && (!pauseEat.get() || !mc.player.isUsingItem()) && placeTimer >= placeDelay.get()) {
            if (hand != null || (switchMode.get() == SwitchMode.Silent && result.slot() >= 0) || ((switchMode.get() == SwitchMode.PickSilent || switchMode.get() == SwitchMode.InvSwitch) && invResult.slot() >= 0)) {

                List<BlockPos> toPlace = new ArrayList<>();
                for (BlockPos pos : placements) {
                    if (toPlace.size() < places.get() && canPlace(pos)) {
                        toPlace.add(pos);
                    }
                }

                if (!toPlace.isEmpty()) {
                    int obsidian = hand == Hand.MAIN_HAND ? Managers.HOLDING.getStack().getCount() :
                            hand == Hand.OFF_HAND ? mc.player.getOffHandStack().getCount() : -1;

                    if (hand == null) {
                        switch (switchMode.get()) {
                            case Silent -> obsidian = result.count();
                            case PickSilent, InvSwitch -> obsidian = invResult.slot() >= 0 ? invResult.count() : -1;
                        }
                    }

                    if (obsidian >= 0) {
                        if (hand == null) {
                            switch (switchMode.get()) {
                                case Silent -> {
                                    obsidian = result.count();
                                    InvUtils.swap(result.slot(), true);
                                }
                                case PickSilent ->
                                        obsidian = InventoryUtils.pickSwitch(invResult.slot()) ? invResult.count() : -1;
                            }
                        }

                        placeTimer = 0;

                        for (int i = 0; i < Math.min(obsidian, toPlace.size()); i++) {
                            dev.saturn.addon.utils.PlaceData placeData = SettingUtils.getPlaceData(toPlace.get(i));
                            if (placeData.valid()) {
                                boolean rotated = !SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(placeData.pos(), priority, RotationType.BlockPlace, Objects.hash(name + "placing"));

                                if (!rotated) {
                                    break;
                                }
                            }
                        }

                        if (hand == null) {
                            switch (switchMode.get()) {
                                case Silent -> InvUtils.swapBack();
                                case PickSilent -> InventoryUtils.pickSwapBack();
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isValid(ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem && blocks.get().contains(((BlockItem) itemStack.getItem()).getBlock());
    }

    private List<BlockPos> getValid(List<BlockPos> positions) {
        List<BlockPos> list = new ArrayList<>();
        for (BlockPos pos : positions) {
            if (!timers.contains(pos)) {
                list.add(pos);
            }
        }
        return list;
    }

    private double closestDist(BlockPos pos) {
        double closest = -1;
        for (PlayerEntity pl : mc.world.getPlayers()) {
            double dist = pl.getPos().distanceTo(Vec3d.ofCenter(pos));

            if (/* In hole check */ (!iHole.get() || !inHole(pl)) &&
                    /* Above Check */ (!above.get() || pl.getY() > pos.getY()) &&
                    pl != mc.player && !Friends.get().isFriend(pl) && (closest < 0 || dist < closest)) {
                closest = dist;
            }
        }
        return closest;
    }

    private boolean inHole(PlayerEntity pl) {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            if (mc.world.getBlockState(pl.getBlockPos().offset(dir)).getBlock() == Blocks.AIR)
                return false;
        }
        return true;
    }

    private boolean canPlace(BlockPos pos) {
        return SettingUtils.getPlaceData(pos).valid();
    }

    private void place(PlaceData d, BlockPos ogPos, Hand hand) {
        timers.add(ogPos, delay.get());

        if (placeSwing.get()) clientSwing(placeHand.get(), hand);

        if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
            Managers.ROTATION.end(Objects.hash(name + "placing"));
        }


        if (!toRender.containsKey(ogPos)) {
            toRender.put(ogPos, new Double[]{fadeTime.get() + renderTime.get(), fadeTime.get()});
        } else {
            toRender.replace(ogPos, new Double[]{fadeTime.get() + renderTime.get(), fadeTime.get()});
        }
    }

    public enum SwitchMode {
        Disabled,
        Silent,
        PickSilent,
        InvSwitch
    }
}