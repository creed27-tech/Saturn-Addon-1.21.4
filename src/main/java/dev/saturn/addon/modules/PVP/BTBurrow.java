package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.Saturn;
//import me.bedtrapteam.addon.modules.BTOffHando;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.List;

import static dev.saturn.addon.utils.bed.basic.EntityInfo.*;


public class BTBurrow extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<BurrowMode> burrowMode = sgGeneral.add(new EnumSetting.Builder<BurrowMode>().name("mode").description("Mode for placing burrow block").defaultValue(BurrowMode.OneTime).build());
    private final Setting<Integer> reBurrow = sgGeneral.add(new IntSetting.Builder().name("re-burrow").description("Delay between re-burrowing.").defaultValue(5).min(1).sliderMax(20).visible(() -> burrowMode.get() == BurrowMode.Multiply).build());
    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder().name("center").description("Teleporting to center.").defaultValue(false).build());
    private final Setting<Boolean> onlyHole = sgGeneral.add(new BoolSetting.Builder().name("only-hole").description("Prevent working if player isnt in hole").defaultValue(false).build());
    private final Setting<Double> rubberbandHeight = sgGeneral.add(new DoubleSetting.Builder().name("rubberband-height").description("How far to attempt to cause rubberband.").defaultValue(12).sliderMin(-30).sliderMax(30).build());
    private final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder().name("timer").description("Timer").defaultValue(1.00).min(0.01).sliderMax(10).build());
    private final Setting<List<net.minecraft.block.Block>> block = sgGeneral.add(new BlockListSetting.Builder().name("block").description("Which blocks used for burrow.").defaultValue(Collections.singletonList(Blocks.OBSIDIAN)).filter(this::blockFilter).build());

    private final BlockPos.Mutable pos = new BlockPos.Mutable();
    private int ticks;
    private int slot = -1;

    public BTBurrow() {
        super(Saturn.PVP, "burrow-plus", "Clips you into a block.");
    }

    @Override
    public void onActivate() {
        ticks = 0;

        slot = InvUtils.findInHotbar(itemStack -> block.get().contains(net.minecraft.block.Block.getBlockFromItem(itemStack.getItem()))).slot();
        //if (Modules.get().get(BTOffHando.class).isActive() && Modules.get().get(BTOffHando.class).anvilOnBurrow.get()) slot = 45;

        if (slot == -1) {
            toggle();
            return;
        }

        if (isSurrounded(mc.player) && onlyHole.get()) {
            toggle();
            return;
        }
        if (!shouldBurrow2()) {
            toggle();
            return;
        }
        pos.set(getBlockPos(mc.player));
        Modules.get().get(Timer.class).setOverride(this.timer.get());
    }

    @Override
    public void onDeactivate() {
        Modules.get().get(Timer.class).setOverride(Timer.OFF);
    }

    private boolean blockFilter(net.minecraft.block.Block block) {
        return block.getBlastResistance() >= 600;
    }

    private void burrow() {
        // Center the player if the option is enabled
        if (center.get()) {
            PlayerUtils.centerPlayer();
        }

        // Get player coordinates
        double playerX = mc.player.getX();
        double playerY = mc.player.getY();
        double playerZ = mc.player.getZ();

        // Send position packets with the correct number of parameters
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(playerX, playerY + 0.4, playerZ, true, false));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(playerX, playerY + 0.75, playerZ, true, false));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(playerX, playerY + 1.01, playerZ, true, false));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(playerX, playerY + 1.15, playerZ, true, false));

        // Swap to the desired slot
        InvUtils.swap(slot, true);

        // Interact with the block
        mc.interactionManager.interactBlock(mc.player, slot == 45 ? Hand.OFF_HAND : Hand.MAIN_HAND,
                new BlockHitResult(Utils.vec3d(pos), Direction.UP, pos, false));

        // Send hand swing packet
        mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(slot == 45 ? Hand.OFF_HAND : Hand.MAIN_HAND));

        // Swap back to the original slot
        InvUtils.swapBack();

        // Send final position packet
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(playerX, playerY + rubberbandHeight.get(), playerZ, false, false));
    }

    private boolean shouldBurrow2() {
        BlockState blockState1 = mc.world.getBlockState(pos.set(mc.player.getX() + .3, mc.player.getY() + 2.3, mc.player.getZ() + .3));
        BlockState blockState2 = mc.world.getBlockState(pos.set(mc.player.getX() + .3, mc.player.getY() + 2.3, mc.player.getZ() - .3));
        BlockState blockState3 = mc.world.getBlockState(pos.set(mc.player.getX() - .3, mc.player.getY() + 2.3, mc.player.getZ() - .3));
        BlockState blockState4 = mc.world.getBlockState(pos.set(mc.player.getX() - .3, mc.player.getY() + 2.3, mc.player.getZ() + .3));
        return false;
    }

    public enum BurrowMode {
        Multiply,
        OneTime
    }
}
