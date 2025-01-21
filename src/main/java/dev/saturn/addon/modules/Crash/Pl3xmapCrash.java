package dev.saturn.addon.modules.Crash;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

public class Pl3xmapCrash extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Integer> power = sgGeneral.add(new IntSetting.Builder()
            .name("power")
            .description("packets sent in a tick")
            .range(1, 100)
            .defaultValue(6)
            .build()
    );

    private final Setting<Boolean> toggleOnLeave = sgGeneral.add(new BoolSetting.Builder()
            .name("toggle-on-leave")
            .defaultValue(true)
            .build()
    );

    public Pl3xmapCrash() {
        super(Saturn.Crash, "Pl3xmap-crash", "WIP | Spams the console as fuck if the server has pl3xmap.");
    }

    @EventHandler
    private void onTick(TickEvent.Post tickEvent) {
        if(mc.getNetworkHandler() == null) return;

    }
}