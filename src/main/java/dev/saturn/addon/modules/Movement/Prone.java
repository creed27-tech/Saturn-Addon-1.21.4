package dev.saturn.addon.modules.Movement;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Prone extends Module {
    private final List<BlockPos> waterModeTargets = Arrays.asList(
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0)
    );
    private int waterModeStage = 0;
    private final SettingGroup sgGeneral;
    private final Setting<ProneMode> mode;
    private final Setting<Boolean> autoMaintain;
    private final Setting<List<Block>> blocks;

    public Prone() {
        super(Categories.Movement, "prone", "Become prone on demand.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The mode used.")).defaultValue(ProneMode.WATER_BUCKET)).build());
        this.autoMaintain = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-maintain")).description("Switch to maintain mode when prone.")).defaultValue(true)).build());
        this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("Selected blocks.")).build());
    }

    public void onDeactivate() {
        this.waterModeStage = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (this.autoMaintain.get() && mc.player.isSneaking() && !mc.player.isSprinting()) {
            BlockUtils.place(mc.player.getBlockPos().up(), InvUtils.find((itemstack) -> {
                Item item = itemstack.getItem();
                if (item instanceof BlockItem) {
                    BlockItem blockItem = (BlockItem) item;
                    if (this.blocks.get().contains(blockItem.getBlock())) {
                        return true;
                    }
                }
                return false;
            }), true, 1);
        }
    }

    public enum ProneMode {
        WATER_BUCKET,
        JUST_MAINTAIN,
        COLLISION;

        private static ProneMode[] $values() {
            return new ProneMode[]{WATER_BUCKET, JUST_MAINTAIN, COLLISION};
        }
    }
}