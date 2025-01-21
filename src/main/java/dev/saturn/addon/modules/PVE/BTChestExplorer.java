package dev.saturn.addon.modules.PVE;

import dev.saturn.addon.Saturn;
import dev.saturn.addon.utils.bed.advance.Task;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;

import static dev.saturn.addon.utils.bed.basic.BlockInfo.*;
import static dev.saturn.addon.utils.bed.basic.EntityInfo.getBlockPos;

public class BTChestExplorer extends Module {
    //private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public BTChestExplorer() {
        super(Saturn.PVE, "BT-chest-explorer", "Automatically goes to closest chest, looting it and breaking.");
    }

    private FindItemResult axe;
    private BlockPos pos;
    private int x,y,z;
    private Stage stage;

    private final Task chatTask = new Task();

    @Override
    public void onActivate() {
        stage = Stage.Find;

        chatTask.reset();
        pos = null;
    }

    @Override
    public void onDeactivate() {
        // Send the #stop message to chat
        mc.player.sendMessage(Text.of("#stop"), false);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        // Find the Netherite or Diamond axe in the player's inventory
        axe = InvUtils.find(itemStack -> itemStack.getItem() == Items.NETHERITE_AXE || itemStack.getItem() == Items.DIAMOND_AXE);

        switch (stage) {
            case Find -> {
                // Find the chest position
                pos = findChests();

                if (pos == null) {
                    // Reset the task and set the stage to Stuck if no chests are found
                    chatTask.reset();
                    stage = Stage.Stuck;
                    // Uncomment if you want a message when no chests are found
                    // info("There's no chests in your view distance.");
                    // toggle();
                    return;
                }

                // Store chest position coordinates
                x = pos.getX();
                y = pos.getY();
                z = pos.getZ();

                // Reset the chat task and move to the next stage
                chatTask.reset();
                stage = Stage.Move;
            }
            case Move -> {
                // Send the #goto message with the chest position
                chatTask.run(() -> mc.player.sendMessage(Text.of("#goto " + x + " " + y + " " + z), false));

                // Check if the player has reached the chest position
                if (getBlockPos(mc.player).equals(pos)) {
                    stage = Stage.Reset;
                }
            }
            case Reset -> {
                // Reset the chat task and move to the Find stage
                chatTask.reset();
                stage = Stage.Find;
            }
            case Stuck -> {
                // If chests are found again, move to Find stage; otherwise, send the #goto message with new coordinates
                if (findChests() != null) {
                    stage = Stage.Find;
                } else {
                    chatTask.run(() -> mc.player.sendMessage(Text.of("#goto " + getDirection().getX() + " " + y + " " + getDirection().getZ()), false));
                }
            }
        }
    }

    private BlockPos getDirection() {
        int x = X(mc.player.getBlockPos());
        int y = Y(mc.player.getBlockPos());
        int z = Z(mc.player.getBlockPos());

        if (x > 0) x = 30000000;
        else x = -30000000;

        if (y > 0) y = 30000000;
        else y = -30000000;

        return new BlockPos(x,y,z);
    }

    private BlockPos findChests() {
        ArrayList<BlockPos> pos = new ArrayList<>();

        for (BlockEntity entity : Utils.blockEntities()) {
            if (entity instanceof ChestBlockEntity chestBlock) {
                if (!pos.contains(chestBlock.getPos())) pos.add(chestBlock.getPos());
            }
        }

        if (pos.isEmpty()) return null;

        pos.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));
        return pos.get(0);
    }

    public enum Stage {
        Find,
        Move,
        Reset,
        Stuck
    }
}
