package dev.saturn.addon.modules.Funny;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.Quiver;
import meteordevelopment.meteorclient.systems.modules.player.EXPThrower;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.MathHelper;


public class FidgetSpinner extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
            .name("rotation-speed")
            .description("The speed at which you rotate.")
            .defaultValue(20)
            .sliderMin(0.0)
            .sliderMax(50.0)
            .build()
    );

    private double yaw = 0;

    public FidgetSpinner() {
        super(Saturn.Funny, "fidget-spinner", "Makes you spin right round.");
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        Modules modules = Modules.get();
        if (!modules.isActive(EXPThrower.class) &&
                !modules.isActive(Quiver.class) &&
                !modules.isActive(EXPThrower.class)
        ) {
            yaw = MathHelper.wrapDegrees(yaw + speed.get());
            Rotations.rotate(yaw, 0.0);
        }
    }
}