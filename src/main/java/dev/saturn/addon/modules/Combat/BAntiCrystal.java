package dev.saturn.addon.modules.Combat;

import meteordevelopment.meteorclient.systems.modules.Categories;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import java.util.function.Predicate;
import java.util.Collections;
import net.minecraft.block.Blocks;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import java.util.List;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class BAntiCrystal extends Module
{
    private final SettingGroup sgGeneral;
    private final Setting<Keybind> wideKeybind;
    private final Setting<Boolean> antiFacePlace;
    private final Setting<Keybind> antiFacePlaceKeybind;
    private final Setting<Boolean> onlyOnGround;
    private final Setting<Boolean> onlyWhenSneaking;
    private final Setting<Boolean> turnOff;
    private final Setting<Boolean> center;
    private final Setting<Boolean> disableOnJump;
    private final Setting<Boolean> disableOnYChange;
    private final Setting<Boolean> rotate;
    private boolean return_;

    public BAntiCrystal() {
        super(Categories.Combat, "banana-anti-crystal", "Stops End Crystals from doing damage to you. | Ported from Banana+");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.wideKeybind = (Setting<Keybind>)this.sgGeneral.add((Setting)new KeybindSetting.Builder().name("force-wide-keybind").description("turns on wide surround when held").defaultValue(Keybind.fromKey(-1)).build());
        this.antiFacePlace = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("anti-face-place").description("Places a block on top of the original surround blocks to prevent people from face-placing you.").defaultValue(false).build());
        this.antiFacePlaceKeybind = (Setting<Keybind>)this.sgGeneral.add((Setting)new KeybindSetting.Builder().name("force-anti-face-place-keybind").description("turns on double height").defaultValue(Keybind.fromKey(-1)).build());
        this.onlyOnGround = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("only-on-ground").description("Works only when you standing on blocks.").defaultValue(true).build());
        this.onlyWhenSneaking = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("only-when-sneaking").description("Places blocks only after sneaking.").defaultValue(false).build());
        this.turnOff = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("turn-off").description("Toggles off when all blocks are placed.").defaultValue(false).build());
        this.center = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("center").description("Teleports you to the center of the block.").defaultValue(true).build());
        this.disableOnJump = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("disable-on-jump").description("Automatically disables when you jump.").defaultValue(true).build());
        this.disableOnYChange = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("disable-on-y-change").description("Automatically disables when your y level (step, jumping, atc).").defaultValue(true).build());
        this.rotate = (Setting<Boolean>)this.sgGeneral.add((Setting)new BoolSetting.Builder().name("rotate").description("Automatically faces towards the obsidian being placed.").defaultValue(true).build());
    }

    public void onActivate() {
        if (this.center.get()) {
            PlayerUtils.centerPlayer();
        }
    }

    @EventHandler
    private void onTick(final TickEvent.Post event) {
        if (((boolean)this.disableOnJump.get() && (this.mc.options.jumpKey.isPressed() || ((boolean)this.disableOnYChange.get() && this.mc.player.prevY < this.mc.player.getY())))) {
            this.toggle();
            return;
        }
        if ((boolean)this.onlyOnGround.get() && !this.mc.player.isOnGround()) {
            return;
        }
        if ((boolean)this.onlyWhenSneaking.get() && !this.mc.options.sneakKey.isPressed()) {
            return;
        }
        this.return_ = false;
        if (this.return_) {
            return;
        }
        if (this.return_) {
            return;
        }
        if (this.return_) {
            return;
        }
        if (this.return_) {
            return;
        }
        boolean antiFacePlaced = false;
        if ((boolean)this.antiFacePlace.get() || ((Keybind)this.antiFacePlaceKeybind.get()).isPressed()) {
                antiFacePlaced = true;
            }
        }

    private boolean blockFilter(final Block block) {
        return block == Blocks.ACACIA_PRESSURE_PLATE || block == Blocks.BIRCH_PRESSURE_PLATE || block == Blocks.CRIMSON_PRESSURE_PLATE || block == Blocks.DARK_OAK_PRESSURE_PLATE || block == Blocks.JUNGLE_PRESSURE_PLATE || block == Blocks.OAK_PRESSURE_PLATE || block == Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE || block == Blocks.SPRUCE_PRESSURE_PLATE || block == Blocks.STONE_PRESSURE_PLATE || block == Blocks.WARPED_PRESSURE_PLATE || block == Blocks.ACACIA_BUTTON || block == Blocks.BIRCH_BUTTON || block == Blocks.CRIMSON_BUTTON || block == Blocks.DARK_OAK_BUTTON || block == Blocks.JUNGLE_BUTTON || block == Blocks.OAK_BUTTON || block == Blocks.POLISHED_BLACKSTONE_BUTTON || block == Blocks.SPRUCE_BUTTON || block == Blocks.STONE_BUTTON || block == Blocks.WARPED_BUTTON || block == Blocks.TRIPWIRE;
    }
}