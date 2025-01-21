package dev.saturn.addon.modules.Player;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class Ghost extends Module {
    private boolean active = false;
    private int x = 0;
    private int z = 0;
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> fullFood;

    public Ghost() {
        super(meteordevelopment.meteorclient.systems.modules.Categories.Player, "ghost", "Allows you to move after death.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.fullFood = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("full-food")).description("Sets the food level client-side to max so you can sprint.")).defaultValue(true)).build());
    }

    @Override
    public void onDeactivate() {
        this.active = false;
        this.warning("You are no longer in ghost mode.");
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        this.active = false;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (this.active) {
            if (MinecraftClient.getInstance().player.getHungerManager().getFoodLevel() < 1) {
                MinecraftClient.getInstance().player.getHungerManager();
            }

            if (fullFood.get() && MinecraftClient.getInstance().player.getHungerManager().getFoodLevel() < 20) {
                MinecraftClient.getInstance().player.getHungerManager().setFoodLevel(20);
            }
        }
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
            event.cancel();
            if (!this.active) {
                this.active = true;
                this.info("You are now in ghost mode.");
            }
        }
    }