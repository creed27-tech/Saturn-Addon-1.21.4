package dev.saturn.addon.modules.Combat;

import meteordevelopment.meteorclient.systems.modules.Categories;
import net.minecraft.item.ItemStack;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import java.util.function.Predicate;
import java.util.Collections;
import net.minecraft.block.Blocks;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import net.minecraft.block.Block;
import java.util.List;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class AntiCevBreaker extends Module
{
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> placeThingsIn;
    private final Setting<Boolean> placeThingsTop;
    private final Setting<Boolean> placeThingsTop2;
    private final Setting<Boolean> placeThingsTop3;
    private final Setting<Boolean> onlyInHole;

    public AntiCevBreaker() {
        super(Categories.Combat, "anti-cev-breaker", "Places buttons,pressure plates, strings to prevent you getting memed on. | Ported from Banana+");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.placeThingsIn = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("place-things-in").description("Places things in you.").defaultValue(false).build());
        this.placeThingsTop = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("place-things-top").description("Places things above you.").defaultValue(false).build());
        this.placeThingsTop2 = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("place-things-2-top").description("Places things 2 blocks on top.").defaultValue(true).build());
        this.placeThingsTop3 = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("place-things-3-top").description("Places things 3 blocks on top.").defaultValue(false).build());
        this.onlyInHole = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("only-in-hole").description("Only functions when you are standing in a hole.").defaultValue(true).build());
    }

    @EventHandler
    private void onTick(final TickEvent.Pre event) {
        if ((boolean)this.onlyInHole.get() && !PlayerUtils.isInHole(true)) {
            return;
        }
        final BlockPos head = this.mc.player.getBlockPos().up();
        if (this.placeThingsIn.get()) {
            this.place(this.mc.player.getBlockPos().up(1));
        }
        if (this.placeThingsTop.get()) {
            this.place(this.mc.player.getBlockPos().up(2));
        }
        if (this.placeThingsTop2.get()) {
            this.place(this.mc.player.getBlockPos().up(3));
        }
        if (this.placeThingsTop3.get()) {
            this.place(this.mc.player.getBlockPos().up(4));
        }
    }

    private boolean blockFilter(final Block block) {
        return block == Blocks.ACACIA_PRESSURE_PLATE || block == Blocks.BIRCH_PRESSURE_PLATE || block == Blocks.CRIMSON_PRESSURE_PLATE || block == Blocks.DARK_OAK_PRESSURE_PLATE || block == Blocks.JUNGLE_PRESSURE_PLATE || block == Blocks.OAK_PRESSURE_PLATE || block == Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE || block == Blocks.SPRUCE_PRESSURE_PLATE || block == Blocks.STONE_PRESSURE_PLATE || block == Blocks.WARPED_PRESSURE_PLATE || block == Blocks.ACACIA_BUTTON || block == Blocks.BIRCH_BUTTON || block == Blocks.CRIMSON_BUTTON || block == Blocks.DARK_OAK_BUTTON || block == Blocks.JUNGLE_BUTTON || block == Blocks.OAK_BUTTON || block == Blocks.POLISHED_BLACKSTONE_BUTTON || block == Blocks.SPRUCE_BUTTON || block == Blocks.STONE_BUTTON || block == Blocks.WARPED_BUTTON || block == Blocks.TRIPWIRE;
    }

    private void place(final BlockPos blockPos) {
    }
}