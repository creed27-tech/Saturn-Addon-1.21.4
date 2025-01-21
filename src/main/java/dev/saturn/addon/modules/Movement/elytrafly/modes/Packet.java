package dev.saturn.addon.modules.Movement.elytrafly.modes;

import dev.saturn.addon.modules.Movement.elytrafly.ElytraFlyMode;
import dev.saturn.addon.modules.Movement.elytrafly.ElytraFlyModes;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Packet extends ElytraFlyMode {

    public Packet() {
        super(ElytraFlyModes.Packet);
    }

    public void onPreTick(TickEvent.Pre event) {
        if(!mc.player.isOnGround() && !mc.player.isSubmergedInWater() && mc.player.getVelocity().y <= 0) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            float movementSpeed = (float) Math.sqrt(Math.pow(mc.player.getVelocity().x, 2) + Math.pow(mc.player.getVelocity().z, 2));
            }
        }
    }