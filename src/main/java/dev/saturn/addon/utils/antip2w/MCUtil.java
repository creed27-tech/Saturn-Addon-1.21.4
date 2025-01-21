package dev.saturn.addon.utils.antip2w;

import dev.saturn.addon.Saturn;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class MCUtil {
    public static void sendPacket(Packet<?> packet) {
        Saturn.mc.getNetworkHandler().sendPacket(packet);
    }

    public static ClientPlayNetworkHandler networkHandler() {
        return Saturn.mc.getNetworkHandler();
    }

    public static void disconnect(String reason) {
        disconnect(Text.of(reason));
    }

    public static void disconnect(Text reason) {
        networkHandler().getConnection().disconnect(reason);
    }

    public static CustomPayloadC2SPacket createCustomPayloadPacket(Consumer<PacketByteBuf> consumer, Identifier id) {
        return new CustomPayloadC2SPacket(new CustomPayload() {
            @Override
            public Id<? extends CustomPayload> getId() {
                return null;
            }

            public void write(PacketByteBuf buf) {
                consumer.accept(buf);
            }

            public Identifier id() {
                return id;
            }
        });
    }
}