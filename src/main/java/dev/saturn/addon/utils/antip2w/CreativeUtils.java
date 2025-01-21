package dev.saturn.addon.utils.antip2w;

import dev.saturn.addon.Saturn;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class CreativeUtils {
    public static void giveItemWithNbtToEmptySlot(Item item, @Nullable String nbt, @Nullable Text customName, int count) {
        ItemStack stack = item.getDefaultStack();
        stack.setCount(count);
        if (nbt != null) {
            try {
                stack.toNbt((RegistryWrapper.WrapperLookup) StringNbtReader.parse(nbt));
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        if(customName != null) stack.getName();
        if (Saturn.mc.player.getMainHandStack().isEmpty())
            Saturn.mc.interactionManager.clickCreativeStack(stack, 36 + Saturn.mc.player.getInventory().selectedSlot);
        else {
            int nextEmptySlot = Saturn.mc.player.getInventory().getEmptySlot();
            if (nextEmptySlot < 9) Saturn.mc.interactionManager.clickCreativeStack(stack, 36 + nextEmptySlot);
            else
                Saturn.mc.interactionManager.clickCreativeStack(stack, 36 + Saturn.mc.player.getInventory().selectedSlot);
        }
    }

    public static void giveItemWithNbtToSelectedSlot(Item item, @Nullable String nbt, @Nullable Text customName, int count) {
        ItemStack stack = item.getDefaultStack();
        stack.setCount(count);
        if (nbt != null) {
            try {
                stack.toNbt((RegistryWrapper.WrapperLookup) StringNbtReader.parse(nbt));
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        if(customName != null) stack.getName();
        Saturn.mc.interactionManager.clickCreativeStack(stack, 36 + Saturn.mc.player.getInventory().selectedSlot);
    }
}