package dev.saturn.addon.modules.Dupe;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Hand;

public class AutoMount extends Module {
    public AutoMount() {
        super(Saturn.Dupe, "auto-dupe", "Another dupe method.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> ticks = sgGeneral.add(new IntSetting.Builder()
            .name("ticks")
            .description("Ticks")
            .defaultValue(6)
            .range(0, 100)
            .sliderMax(100)
            .build()
    );

    private int timer = 0;

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        mc.options.sneakKey.setPressed(false);
        if (timer >= ticks.get()) {
            mc.world.getEntities().forEach(entity -> {
                if (entity instanceof BoatEntity) {
                    if (!entity.hasPassengers()) {
                        mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
                    } else {
                        mc.options.sneakKey.setPressed(true);
                    }
                }
            });
            timer = 0;
        }
        timer++;
    }
}