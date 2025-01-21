package dev.saturn.addon.modules.Movement;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;

public class FlySpeed extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    /**
     * Example setting.
     * The {@code name} parameter should be in kebab-case.
     * If you want to access the setting from another class, simply make the setting {@code public}, and use
     * {@link meteordevelopment.meteorclient.systems.modules.Modules#get(Class)} to access the {@link Module} object.
     */
    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
            .name("Speed")
            .description("how fast u wanna go?")
            .defaultValue(0.250)
            .max(10)
            .min(0.0)
            .build()
    );


    public FlySpeed() {
        super(Categories.Movement, "Fly Speed", "Sets the flight speed for abilities");
    }

    @Override
    public void onActivate() {
        mc.player.getAbilities().setFlySpeed(speed.get().floatValue());
    }

    @Override
    public void onDeactivate() {
        mc.player.getAbilities().setFlySpeed(0.05f);
    }
}