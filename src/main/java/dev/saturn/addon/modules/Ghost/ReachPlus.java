package dev.saturn.addon.modules.Ghost;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class ReachPlus extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Double setting for reach modifier with a slider
    private final Setting<Double> reach = sgGeneral.add(new DoubleSetting.Builder()
            .name("reach")
            .description("Your reach modifier.")
            .defaultValue(3.5)
            .min(0.0)
            .sliderRange(0.0, 7.5)
            .build()
    );

    // Boolean setting to restrict reach to swords only
    private final Setting<Boolean> swordOnly = sgGeneral.add(new BoolSetting.Builder()
            .name("Sword Only")
            .description("Only reach if a sword is equipped")
            .defaultValue(false)
            .build()
    );

    // Constructor for Reach module
    public ReachPlus() {
        super(Saturn.Ghost, "reach+", "Gives you super long arms.");
    }

    // Method to check if the player is holding a sword
    private boolean isHoldingSword() {
        ItemStack heldItem = MinecraftClient.getInstance().player.getMainHandStack();
        return heldItem.getItem() instanceof SwordItem;
    }

    // Method to get the reach distance based on conditions
    public float getReach() {
        if (!this.isActive() || (!this.isHoldingSword() && swordOnly.get())) {
            return MinecraftClient.getInstance().player.isSneaking() ? 5.0F : 4.5F;
        } else {
            return reach.get().floatValue();
        }
    }

    // Method to return the current reach value as a string
    public String getInfoString() {
        return reach.get().toString();
    }
}