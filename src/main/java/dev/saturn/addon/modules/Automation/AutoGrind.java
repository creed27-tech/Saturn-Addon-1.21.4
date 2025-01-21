package dev.saturn.addon.modules.Automation;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.GrindstoneScreenHandler;

import java.util.List;
import java.util.Set;

public class AutoGrind extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("The tick delay between grinding items.")
            .defaultValue(50)
            .sliderMax(500)
            .min(0)
            .build()
    );

    private final Setting<List<Item>> itemBlacklist = sgGeneral.add(new ItemListSetting.Builder()
            .name("item-blacklist")
            .description("Items that should be ignored.")
            .defaultValue()
            .filter(Item -> Item.getComponents().get(DataComponentTypes.DAMAGE) != null)
            .build()
    );

    private final Setting<Set<RegistryKey<Enchantment>>> enchantmentBlacklist = sgGeneral.add(new EnchantmentListSetting.Builder()
            .name("enchantment-blacklist")
            .description("Enchantments that should be ignored.")
            .defaultValue()
            .build()
    );

    public AutoGrind() {
        super(Saturn.Automation, "auto-grind", "Automatically disenchants items.");
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (!(mc.player.currentScreenHandler instanceof GrindstoneScreenHandler))
            return;

        MeteorExecutor.execute(() -> {
            for (int i = 0; i <= mc.player.getInventory().size(); i++) {
                if (canGrind(mc.player.getInventory().getStack(i))) {
                    try {
                        Thread.sleep(delay.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (mc.currentScreen == null) break;

                    InvUtils.shiftClick().slot(i);
                    InvUtils.move().fromId(2).to(i);
                }
            }
        });
    }

    private boolean canGrind(ItemStack stack) {
        // Check if the item is blacklisted
        if (itemBlacklist.get().contains(stack.getItem())) {

        }

        // Get the enchantments on the item
        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
        int availEnchs = 0;

        // Loop through each enchantment
        for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
            // Count the enchantment
            availEnchs++;

            // Reduce count for curse enchantments
            if (EnchantmentHelper.hasAnyEnchantmentsIn(stack, EnchantmentTags.CURSE)) {
                availEnchs--;
            }

            // Skip if the enchantment is blacklisted
            if (enchantmentBlacklist.get().contains(enchantment.value())) {
                continue;
            }
        }

        // Return true only if there are enchantments and at least one is usable
        return !enchantments.isEmpty() && availEnchs > 0;
    }
}