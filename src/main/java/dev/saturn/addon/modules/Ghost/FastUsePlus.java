package dev.saturn.addon.modules.Ghost;

import java.util.List;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItem;
import net.minecraft.client.MinecraftClient;

public class FastUsePlus extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<FastUsePlus.Mode> mode;
    private final Setting<List<Item>> items;
    private final Setting<Boolean> blocks;
    private final Setting<Integer> cooldown;

    public FastUsePlus() {
        super(Saturn.Ghost, "fast-use-plus", "Allows you to use items at very high speeds.");
        this.sgGeneral = this.settings.getDefaultGroup();

        this.mode = this.sgGeneral.add(new EnumSetting.Builder<FastUsePlus.Mode>()
                .name("mode")
                .description("Which items to fast use.")
                .defaultValue(FastUsePlus.Mode.All)
                .build());

        this.items = this.sgGeneral.add(new ItemListSetting.Builder()
                .name("items")
                .description("Which items should fast use in 'Some' mode.")
                .visible(() -> this.mode.get() == FastUsePlus.Mode.Some)
                .build());

        this.blocks = this.sgGeneral.add(new BoolSetting.Builder()
                .name("blocks")
                .description("Fast-places blocks if the mode is 'Some'.")
                .visible(() -> this.mode.get() == FastUsePlus.Mode.Some)
                .defaultValue(false)
                .build());

        this.cooldown = this.sgGeneral.add(new IntSetting.Builder()
                .name("cooldown")
                .description("Fast-use cooldown in ticks.")
                .defaultValue(0)
                .min(0)
                .sliderMax(5)
                .build());
    }

    private boolean shouldWorkSome() {
        return this.shouldWorkSome(MinecraftClient.getInstance().player.getMainHandStack()) || this.shouldWorkSome(MinecraftClient.getInstance().player.getOffHandStack());
    }

    private boolean shouldWorkSome(ItemStack itemStack) {
        return this.blocks.get() && itemStack.getItem() instanceof BlockItem || this.items.get().contains(itemStack.getItem());
    }

    public enum Mode {
        All("All"),
        Some("Some");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return this.title;
        }
    }
}