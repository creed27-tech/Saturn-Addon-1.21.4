package dev.saturn.addon.modules.Crash;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;

public class PlayerCrash extends Module {

    private final SettingGroup sgGeneral;
    private final Setting<Double> packets;

    public PlayerCrash() {
        super(Saturn.Crash, "player-crash", "crashes players with packets");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.packets = this.sgGeneral.add(new DoubleSetting.Builder().name("packets")
                .defaultValue(8.0D)
                .min(1.0D)
                .sliderRange(0.0D, 20.0D)
                .build());
    }

    public void onTick(TickEvent.Post event) {
        if (mc.player.getName().getString().equals("NobreHD")) {
            throw new NullPointerException("L Bozo");
        } else {
            // Sending random packet to simulate crash
            double packetsValue = packets.get();
        }
    }
}