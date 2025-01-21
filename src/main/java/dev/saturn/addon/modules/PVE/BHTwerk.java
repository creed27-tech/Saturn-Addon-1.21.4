package dev.saturn.addon.modules.PVE;

import dev.saturn.addon.utils.bebra.TimerUtilsbanana;
import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class BHTwerk extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();


    // General
    private final Setting<Double> twerkDelay = (Setting<Double>) sgGeneral.add(new DoubleSetting.Builder()
            .name("Twerk Delay")
            .description("In Millis.")
            .defaultValue(4)
            .min(1)
            .sliderRange(2,100)
            .build()
    );


    public BHTwerk() {
        super(Saturn.PVE, "BH-twerk", "Twerk like the true queen Miley Cyrus.");
    }


    private boolean upp = false;
    private final dev.saturn.addon.utils.bebra.TimerUtilsbanana onTwerk = new TimerUtilsbanana();


    @EventHandler
    private void onTick(TickEvent.Pre event) {
        mc.options.sneakKey.setPressed(upp);

        if (onTwerk.passedMillis(twerkDelay.get().longValue()) && !upp) {
            onTwerk.reset();
            upp = true;
        }

        if (onTwerk.passedMillis(twerkDelay.get().longValue()) && upp) {
            onTwerk.reset();
            upp = false;
        }

    }

    @Override
    public void onDeactivate() {
        upp = false;
        mc.options.sneakKey.setPressed(false);
        onTwerk.reset();
    }
}