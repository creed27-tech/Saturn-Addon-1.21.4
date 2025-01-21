package dev.saturn.addon.modules.Movement.elytrafly.modes;

import dev.saturn.addon.modules.Movement.elytrafly.ElytraFlyMode;
import dev.saturn.addon.modules.Movement.elytrafly.ElytraFlyModes;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class Test extends ElytraFlyMode {

    int ticks = 0;

    public Test() {
        super(ElytraFlyModes.Test);
    }

    public void onPreTick(TickEvent.Pre event) {
        if(ticks == 0) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
        if(ticks < 25) {
            ChatUtils.sendMsg(Text.of(String.valueOf(ticks)));
            float movementSpeed = (float) Math.sqrt(Math.pow(mc.player.getVelocity().x, 2) + Math.pow(mc.player.getVelocity().z, 2));
            if(movementSpeed < 6.6) {
                float speed = 0.06f;
                Vec3d vec3d = mc.player.getRotationVector();
                mc.player.setVelocity(mc.player.getVelocity().x + (vec3d.x*speed), -0.05, mc.player.getVelocity().z + (vec3d.z*speed));
            }
            if(movementSpeed > 0.2) {
                float pitch = (float) -9.175d / movementSpeed;
                mc.player.setPitch(pitch);
            }
        }else {
            //mc.player.setVelocity(mc.player.getVelocity().x * 0.8, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.8);
        }
    }

    public void onPostTick(TickEvent.Post event) {

    }

    public void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket) {
            if(ticks == 25) ((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(true);
            ticks++;
            if(ticks > 30) ticks = 0;
        }
    }

}