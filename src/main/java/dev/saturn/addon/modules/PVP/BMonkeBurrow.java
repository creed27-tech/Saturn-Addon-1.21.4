package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.utils.bananaplus.BPlusEntityUtils;
import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class BMonkeBurrow extends Module {
    public enum RubberbandDirection {
        Up,
        Down
    }

    public enum CenterMode {
        Snap,
        Center,
        None
    }


    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgPlacing = settings.createGroup("Placing");

    // General
    private final Setting<Block> block = sgGeneral.add(new EnumSetting.Builder<Block>()
            .name("block")
            .description("The block to use for Burrow.")
            .defaultValue(Block.Anvil)
            .build()
    );

    private final Setting<Block> fallbackBlock = sgGeneral.add(new EnumSetting.Builder<Block>()
            .name("fallback-block")
            .description("The fallback block to use for Burrow.")
            .defaultValue(Block.EChest)
            .build()
    );

    private final Setting<Boolean> onlyInHole = sgGeneral.add(new BoolSetting.Builder()
            .name("only-in-holes")
            .description("Stops you from burrowing when not in a hole.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder()
            .name("only-on-ground")
            .description("Stops you from burrowing when not in a hole.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Faces the block you place server-side.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> debug = sgGeneral.add(new BoolSetting.Builder()
            .name("debug")
            .defaultValue(false)
            .build()
    );


    // Placing
    private final Setting<CenterMode> centerMode = sgPlacing.add(new EnumSetting.Builder<CenterMode>()
            .name("center")
            .description("How it should center you before burrowing.")
            .defaultValue(CenterMode.Center)
            .build()
    );

    private final Setting<Boolean> instant = sgPlacing.add(new BoolSetting.Builder()
            .name("instant")
            .description("Jumps with packets rather than vanilla jump.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> automatic = sgPlacing.add(new BoolSetting.Builder()
            .name("automatic")
            .description("Automatically burrows on activate rather than waiting for jump.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Double> triggerHeight = sgPlacing.add(new DoubleSetting.Builder()
            .name("trigger-height")
            .description("How high you have to jump before a rubberband is triggered.")
            .defaultValue(1.12)
            .range(0.01, 1.4)
            .sliderRange(0.01, 1.4)
            .build()
    );

    private final Setting<RubberbandDirection> rubberbandDirection = sgPlacing.add(new EnumSetting.Builder<RubberbandDirection>()
            .name("rubberband-direction")
            .description("Which direction to rubberband you when your are burrowing.")
            .defaultValue(RubberbandDirection.Up)
            .build()
    );

    private final Setting<Integer> minRubberbandHeight = sgPlacing.add(new IntSetting.Builder()
            .name("min-height")
            .description("Min height to rubberband.")
            .defaultValue(3)
            .min(2)
            .sliderRange(2,30)
            .build()
    );

    private final Setting<Integer> maxRubberbandHeight = sgPlacing.add(new IntSetting.Builder()
            .name("max-height")
            .description("Max height to rubberband.")
            .defaultValue(7)
            .min(2)
            .sliderRange(2,30)
            .build()
    );

    private final Setting<Double> timer = sgPlacing.add(new DoubleSetting.Builder()
            .name("timer")
            .description("Timer override.")
            .defaultValue(1)
            .min(0.01)
            .sliderRange(0.01, 10)
            .build()
    );


    public BMonkeBurrow() {
        super(Saturn.PVP, "B+-monke-burrow", "Attempts to clip you into a block.");
    }


    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private boolean shouldBurrow;


    @Override
    public void onActivate() {
        if (onlyOnGround.get() && !mc.player.isOnGround()) {
            error("Not on the ground, disabling.");
            toggle();
            
        }

        if (!BPlusEntityUtils.isInHole(mc.player, true, BPlusEntityUtils.BlastResistantType.Any) && onlyInHole.get()) {
            error("Not in a hole, disabling.");
            toggle();
            
        }

        if (!checkHead()) {
            error("Not enough headroom to burrow, disabling.");
            toggle();
            
        }

        FindItemResult result = getItem();
        if (!result.isHotbar() && !result.isOffhand()) result = getFallbackItem();

        if (!result.isHotbar() && !result.isOffhand()) {
            error("No burrow block found, disabling.");
            toggle();
            
        }

        Modules.get().get(Timer.class).setOverride(this.timer.get());

        shouldBurrow = false;

        if (automatic.get()) {
            if (instant.get()) shouldBurrow = true;
            else mc.player.jump();
        } else {
            info("Waiting for manual jump.");
        }
        
    }

    @Override
    public void onDeactivate() {
        Modules.get().get(Timer.class).setOverride(Timer.OFF);
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (instant.get() && !shouldBurrow) {
            if (event.action == KeyAction.Press && mc.options.jumpKey.matchesKey(event.key, 0)) {
                shouldBurrow = true;
            }
        }
    }

    private void burrow() {
        FindItemResult block = getItem();

        if ((!block.isHotbar() && !block.isOffhand())) block = getFallbackItem();

        if (!(mc.player.getInventory().getStack(block.slot()).getItem() instanceof BlockItem));

        else if (centerMode.get() == CenterMode.Center) PlayerUtils.centerPlayer();

        if (instant.get()) {
            return;
        }

        InvUtils.swap(block.slot(), true);

        mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

        InvUtils.swapBack();

        if (instant.get()) {
            if (rubberbandDirection.get() == RubberbandDirection.Up) {
                if (debug.get()) info(String.valueOf(upperSpace()));
            } else {
                if (debug.get()) info(String.valueOf(lowerSpace()));
            }
        } else {
            if (rubberbandDirection.get() == RubberbandDirection.Up) {
                mc.player.updatePosition(mc.player.getX(), mc.player.getY() + upperSpace(), mc.player.getZ());
                if (debug.get()) info(String.valueOf(upperSpace()));
            } else {
                mc.player.updatePosition(mc.player.getX(), mc.player.getY() + upperSpace(), mc.player.getZ());
                if (debug.get()) info(String.valueOf(lowerSpace()));
            }
        }
    }

    private FindItemResult getItem() {
        return switch (block.get()) {
            case EChest -> InvUtils.findInHotbar(Items.ENDER_CHEST);
            case Anvil -> InvUtils.findInHotbar(itemStack -> net.minecraft.block.Block.getBlockFromItem(itemStack.getItem()) instanceof AnvilBlock);
            case AncientDebris -> InvUtils.findInHotbar(Items.ANCIENT_DEBRIS);
            case Netherite -> InvUtils.findInHotbar(Items.NETHERITE_BLOCK);
            case Anchor -> InvUtils.findInHotbar(Items.RESPAWN_ANCHOR);
            case EnchantingTable -> InvUtils.findInHotbar(Items.ENCHANTING_TABLE);
            case Held -> new FindItemResult(mc.player.getInventory().selectedSlot, mc.player.getMainHandStack().getCount());
            default -> InvUtils.findInHotbar(Items.OBSIDIAN, Items.CRYING_OBSIDIAN);
        };
    }

    private FindItemResult getFallbackItem() {
        return switch (fallbackBlock.get()) {
            case EChest -> InvUtils.findInHotbar(Items.ENDER_CHEST);
            case Anvil -> InvUtils.findInHotbar(itemStack -> net.minecraft.block.Block.getBlockFromItem(itemStack.getItem()) instanceof AnvilBlock);
            case AncientDebris -> InvUtils.findInHotbar(Items.ANCIENT_DEBRIS);
            case Netherite -> InvUtils.findInHotbar(Items.NETHERITE_BLOCK);
            case Anchor -> InvUtils.findInHotbar(Items.RESPAWN_ANCHOR);
            case EnchantingTable -> InvUtils.findInHotbar(Items.ENCHANTING_TABLE);
            case Held -> new FindItemResult(mc.player.getInventory().selectedSlot, mc.player.getMainHandStack().getCount());
            default -> InvUtils.findInHotbar(Items.OBSIDIAN, Items.CRYING_OBSIDIAN);
        };
    }

    private boolean checkHead() {
        BlockState blockState1 = mc.world.getBlockState(blockPos.set(mc.player.getX() + .3, mc.player.getY() + 2.3, mc.player.getZ() + .3));
        BlockState blockState2 = mc.world.getBlockState(blockPos.set(mc.player.getX() + .3, mc.player.getY() + 2.3, mc.player.getZ() - .3));
        BlockState blockState3 = mc.world.getBlockState(blockPos.set(mc.player.getX() - .3, mc.player.getY() + 2.3, mc.player.getZ() - .3));
        BlockState blockState4 = mc.world.getBlockState(blockPos.set(mc.player.getX() - .3, mc.player.getY() + 2.3, mc.player.getZ() + .3));

        return false;
    }

    private int upperSpace() {
        for (int dy = minRubberbandHeight.get(); dy <= maxRubberbandHeight.get(); dy++) {
        }

        return 0;
    }

    private int lowerSpace() {
        for (int dy = -minRubberbandHeight.get(); dy >= -maxRubberbandHeight.get(); dy--) {
        }

        return 0;
    }

    public enum Block {
        EChest,
        Obsidian,
        Anvil,
        AncientDebris,
        Netherite,
        Anchor,
        EnchantingTable,
        Held
    }
}