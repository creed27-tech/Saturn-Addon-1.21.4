package dev.saturn.addon.modules.Misc;

import dev.saturn.addon.modules.Combat.AutoCrystal;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;

public class Suicide extends Module {
    public Suicide() {
        super(Categories.Misc, "Suicide", "Kills yourself. Recommended.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> disableDeath = sgGeneral.add(new BoolSetting.Builder()
            .name("Disable On Death")
            .description("Disables the module on death.")
            .defaultValue(true)
            .build()
    );
    public final Setting<Boolean> enableCA = sgGeneral.add(new BoolSetting.Builder()
            .name("Enable Auto Crystal")
            .description("Enables auto crystal when enabled.")
            .defaultValue(true)
            .build()
    );

    @Override
    public void onActivate() {

        
    }

    @EventHandler(priority = 6969)
    private void onDeath(OpenScreenEvent event) {
        if (event.screen instanceof DeathScreen && disableDeath.get()) {
            toggle();
            sendDisableMsg("died");
        }
    }

    private void sendDisableMsg(String died) {
    }
}