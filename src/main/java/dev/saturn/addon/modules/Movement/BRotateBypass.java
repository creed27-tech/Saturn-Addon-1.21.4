package dev.saturn.addon.modules.Movement;

import dev.saturn.addon.utils.aurora.meteor.BOEntityUtils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class BRotateBypass extends Module {
    public BRotateBypass() {
        super(Categories.Movement, "BRotateBypass", "BRotateBypass. | Ported from Aurora");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> burrowbypass = sgGeneral.add(new BoolSetting.Builder()
            .name("burrowbypass")
            .description("nolag in burrow.")
            .defaultValue(false)
            .build()
    );



    @EventHandler
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerPositionLookS2CPacket) {
            if (burrowbypass.get() && BOEntityUtils.isBlockLag(mc.player)) return;
        }
    }
}