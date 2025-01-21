package dev.saturn.addon.modules.Movement.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import dev.saturn.addon.modules.Movement.nofall.NoFallMode;
import dev.saturn.addon.modules.Movement.nofall.NoFallModes;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Verus extends NoFallMode {

    public Verus() {
        super(NoFallModes.Verus);
    }

    @Override
    public void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket)event.packet;
            PlayerMoveC2SPacketAccessor accessor = (PlayerMoveC2SPacketAccessor)packet;

            if (mc.player.fallDistance > 3.35) {
                accessor.setOnGround(true);
                mc.player.fallDistance = 0f;
                var vel = mc.player.getVelocity();
                mc.player.setVelocity(vel.x, 0, vel.z);
            }
        }
    }
}