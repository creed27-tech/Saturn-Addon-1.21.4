package dev.saturn.addon.modules.Movement;

import dev.saturn.addon.modules.Player.ScaffoldPlusV2;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class SprintPlus extends Module {
    public SprintPlus() {
        super(Categories.Movement, "sprint-plus", "Non shit sprint!");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<SprintMode> sprintMode = sgGeneral.add(new EnumSetting.Builder<SprintMode>()
            .name("Mode")
            .description("The method of sprinting.")
            .defaultValue(SprintMode.Vanilla)
            .build()
    );
    public final Setting <Boolean> hungerCheck = sgGeneral.add(new BoolSetting.Builder()
            .name("HungerCheck")
            .description("Should we check if we have enough hunger to sprint")
            .defaultValue(true)
            .build()
    );

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTick(TickEvent.Pre event) {
        if (ScaffoldPlusV2.shouldStopSprinting && Modules.get().isActive(ScaffoldPlusV2.class)) {return;}

        if (mc.player != null && mc.world != null) {
            if (hungerCheck.get()) {
                if (mc.player.getHungerManager().getFoodLevel() < 6) {
                    mc.player.setSprinting(false);
                    return;
                }
            }
            switch (sprintMode.get()) {
                case Vanilla -> {
                    if (mc.options.forwardKey.isPressed()) mc.player.setSprinting(true);
                }
                case Omni -> {
                    if (PlayerUtils.isMoving()) {
                        mc.player.setSprinting(true);
                    }
                }
                case Rage -> mc.player.setSprinting(true);
            }
        }
    }

    @Override
    public void onDeactivate() {
        if (mc.player != null && mc.world != null)
            mc.player.setSprinting(false);
    }

    public enum SprintMode {
        Vanilla,
        Omni,
        Rage
    }
}