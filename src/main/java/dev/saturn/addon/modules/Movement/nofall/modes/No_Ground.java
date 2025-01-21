package dev.saturn.addon.modules.Movement.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import dev.saturn.addon.modules.Movement.nofall.NoFallMode;
import dev.saturn.addon.modules.Movement.nofall.NoFallModes;
import dev.saturn.addon.mixin.minecraft.entity.PlayerMoveC2SPacketAccessor;

public class No_Ground extends NoFallMode {
    /*
    Tested on: oldfrog.org (NCP)
     */
    public No_Ground() {
        super(NoFallModes.No_Ground);
    }

    @Override
    public void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof IPlayerMoveC2SPacket move) {
            PlayerMoveC2SPacketAccessor move2 = (PlayerMoveC2SPacketAccessor) move;
            if (move2.getOnGround()) {
                move2.setOnGround(false);
            }
        }
    }
}