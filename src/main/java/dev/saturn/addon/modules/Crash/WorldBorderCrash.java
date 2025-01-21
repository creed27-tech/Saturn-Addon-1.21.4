package dev.saturn.addon.modules.Crash;

import java.util.List;
import java.util.Objects;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.text.Text;

public class WorldBorderCrash extends Module {
    private final Setting<Integer> packets;
    private final Setting<Boolean> autoDisable;

    public WorldBorderCrash() {
        super(Saturn.Crash, "world-border-crash", "WIP | A module that will take advantage of the World Border packets.");
        SettingGroup generalSettings = this.settings.getDefaultGroup();

        this.packets = generalSettings.add(new IntSetting.Builder()
                .name("packets")
                .description("Number of packets to send per tick. Warning: this is multiplied by the number of unlocked recipes.")
                .defaultValue(24)
                .min(1)
                .sliderMax(50)
                .build());

        this.autoDisable = generalSettings.add(new BoolSetting.Builder()
                .name("autoDisable")
                .description("Disables the module when kicked.")
                .defaultValue(true)
                .build());
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!mc.isInSingleplayer() && mc.player != null) {
            if (mc.getNetworkHandler() == null) {
                return;
            }

            try {
                RecipeBookOptions recipeCollections = mc.player.getRecipeBook().getOptions();
                if (recipeCollections == null) {
                    this.error("Recipe collections are null. Disabling module.");
                    this.toggle();
                    return;
                }
            } catch (Exception e) {
                this.error("An error occurred. Disabling module.");
                this.toggle();
            }
        } else {
            this.error("You must be on a server. Disabling module.");
            this.toggle();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) {
            this.toggle();
        }
    }
}