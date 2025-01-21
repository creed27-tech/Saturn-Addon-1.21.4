package dev.saturn.addon.modules.Combat.BogeyMan;

import dev.saturn.addon.utils.bbc.entity.EntityHelper;
import dev.saturn.addon.utils.bbc.player.InvHelper;
import dev.saturn.addon.utils.bbc.player.PlayerHelper;
import dev.saturn.addon.utils.bbc.world.BlockHelper;
import dev.saturn.addon.utils.bbc.world.BlockPosX;
import dev.saturn.addon.utils.bbc.world.ItemHelper;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.item.BedItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Comparator;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BedCrafter {
    public static BogeyMan boogyMan = Modules.get().get(BogeyMan.class);

    public static void startWork(){
        if (isOutOfMaterial() || InvHelper.isInventoryFull() || !canWork()) return;

        BlockPosX craftingTablePos = findCraftingTable();

        boolean isCraftingTableHandler = mc.player.currentScreenHandler instanceof CraftingScreenHandler;
        boolean isNearCraftingTable = craftingTablePos != null;

        if (!isCraftingTableHandler){
            if (isNearCraftingTable){
                BlockHitResult interactResult = new BlockHitResult(craftingTablePos.closestVec3d(), Direction.UP, craftingTablePos, false);
                mc.interactionManager.interactBlock(mc.player, Hand.OFF_HAND, interactResult);
            }
            else {
                FindItemResult craftingTableItem = ItemHelper.findCraftTable();
                List<BlockPos> nearbyBlocks = BlockHelper.getSphere(mc.player.getBlockPos(), boogyMan.craftRadius.get().intValue(), boogyMan.craftRadius.get().intValue());
                nearbyBlocks.sort(Comparator.comparing(BedCrafter::getSafety));
                for (BlockPos block : nearbyBlocks) {
                    if (BlockHelper.getBlock(block) == Blocks.AIR) {
                        InvUtils.swap(craftingTableItem.slot(), true);
                        BlockHitResult placeResult = new BlockHitResult(BlockHelper.closestVec3d(block), Direction.UP, block, false);
                        mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, placeResult, 0));
                        InvUtils.swapBack();
                        break;
                    }
                }
            }
        }
        else {
            CraftingScreenHandler craftingScreenHandler = (CraftingScreenHandler) mc.player.currentScreenHandler;

            if (!canWork()) {
                mc.player.closeHandledScreen();
                mc.player.getInventory().updateItems();
                return;
            }
        }
    }

    private static boolean canWork(){
        if (!boogyMan.whileMoving.get() && EntityHelper.isMoving(mc.player)) return false;
        if (boogyMan.onlyOnHole.get() && !PlayerHelper.isInHole(true, mc.player)) return false;
        if (!mc.player.isOnGround()) return false;

        return !InvHelper.isInventoryFull();
    }


    private static int getSafety(BlockPos pos){
        int i = 6;

        for (Direction direction : Direction.values()){
            BlockPos offset = pos.offset(direction);
            if (BlockHelper.isBlastRes(offset)) i--;
        }
        return i;
    }

    private static BlockPosX findCraftingTable() {
        List<BlockPos> nearbyBlocks = BlockHelper.getSphere(mc.player.getBlockPos(), boogyMan.craftRadius.get().intValue(), boogyMan.craftRadius.get().intValue());
        for (BlockPos block : nearbyBlocks) if (BlockHelper.getBlock(block) == Blocks.CRAFTING_TABLE) return new BlockPosX(block);
        return null;
    }

    private static boolean isOutOfMaterial() {
        FindItemResult wool = InvUtils.find(itemStack -> ItemHelper.wools.contains(itemStack.getItem()));
        FindItemResult plank = InvUtils.find(itemStack -> ItemHelper.planks.contains(itemStack.getItem()));
        FindItemResult craftTable = ItemHelper.findCraftTable();
        if (!craftTable.found()) return true;
        if (!wool.found() || !plank.found()) return true;
        return mc.player.getInventory().getStack(wool.slot()).getCount() < 3 || mc.player.getInventory().getStack(plank.slot()).getCount() < 3;
    }
}