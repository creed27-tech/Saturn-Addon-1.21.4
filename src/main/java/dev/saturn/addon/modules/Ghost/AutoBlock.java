package dev.saturn.addon.modules.Ghost;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import net.minecraft.client.MinecraftClient;

public class AutoBlock extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Double> timer;

    public AutoBlock() {
        super(Saturn.Ghost, "auto-block", "Automatically blocks | Works best on 1.8 servers");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.timer = sgGeneral.add(new DoubleSetting.Builder()
                .name("timer")
                .description("The timer speed to use while Blocking.")
                .defaultValue(2.0D)
                .min(1.0D)
                .sliderMax(10.0D)
                .build());
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player.getMainHandStack().getItem() != null && mc.player.getMainHandStack().getItem().equals(MinecraftClient.getInstance().getItemRenderer().getName())) {
            mc.interactionManager.updateBlockBreakingProgress(mc.player.getBlockPos(), mc.player.getHorizontalFacing());
            ((Timer) Modules.get().get(Timer.class)).setOverride(timer.get());
        } else {
            mc.interactionManager.updateBlockBreakingProgress(mc.player.getBlockPos(), mc.player.getHorizontalFacing());
            ((Timer) Modules.get().get(Timer.class)).setOverride(1.0D);
        }
    }

    @Override
    public void onDeactivate() {
        ((Timer) Modules.get().get(Timer.class)).setOverride(1.0D);
    }
}