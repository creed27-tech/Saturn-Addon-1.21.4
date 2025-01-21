package dev.saturn.addon.modules.Misc;

import com.mojang.brigadier.suggestion.Suggestion;
import io.netty.buffer.Unpooled;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.*;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.common.*;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.stat.Stat;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class PacketLogger extends Module {
    public PacketLogger() {
        super(Categories.Misc, "packet-logger", "Logs packets or whatever you want. (only packets rn)");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // yoinked these settings from meteor
    private final Setting<Set<Class<? extends Packet<?>>>> receivePackets = sgGeneral.add(new PacketListSetting.Builder()
            .name("Receive")
            .description("Server-to-client packets to cancel.")
            .filter(aClass -> PacketUtils.getS2CPackets().contains(aClass))
            .build()
    );

    private final Setting<Set<Class<? extends Packet<?>>>> sendPackets = sgGeneral.add(new PacketListSetting.Builder()
            .name("Send")
            .description("Client-to-server packets to cancel.")
            .filter(aClass -> PacketUtils.getC2SPackets().contains(aClass))
            .build()
    );

    @EventHandler(priority = EventPriority.HIGHEST + 1000000000)
    private void onSend(PacketEvent.Sent event) {
        if (sendPackets.get().contains(event.packet.getClass())) {
            String message = packetMessage(event.packet);

            if (message == null) return;
            log(Formatting.AQUA + "Send: " + Formatting.GRAY + message);
        }
    }

    private String packetMessage(Packet<?> packet) {
        return "";
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1000000000)
    private void onReceive(PacketEvent.Receive event) {
        if (receivePackets.get().contains(event.packet.getClass())) {
            String message = packetMessage(event.packet);

            if (message == null) return;
            log(Formatting.LIGHT_PURPLE + "Receive: " + Formatting.GRAY + message);
        }
    }

    public void sendMessage(Text text, int id) {
        ((IChatHud) mc.inGameHud.getChatHud()).meteor$add(text, id);
    }

    private void log(String string) {
        sendMessage(Text.of(string), 0);
    }

    @Nullable
    private String onCustomPayload(Packet<?> p) throws InterruptedException {
        System.out.println("aaaaaaaaaaa");
        CustomPayload customPayload;
        Identifier id = null;
        if (p instanceof CustomPayloadS2CPacket p2) {
            customPayload = p2.payload();
        } else if (p instanceof CustomPayloadC2SPacket p2) {
            customPayload = p2.payload();
        } else {
            throw new IllegalArgumentException("p instanceof CustomPayloadPacket == false");
        }
        PacketByteBuf payloadBuffer = new PacketByteBuf(Unpooled.buffer());
        customPayload.wait();
        byte[] bytes = new byte[payloadBuffer.readableBytes()];
        String firstBytesASCII = new String(Arrays.copyOfRange(bytes, 0, 127), StandardCharsets.US_ASCII);
        String message = "CustomPayload channel:" + id.toString() + " payloadBuffer first 128 bytes (hover for more): " + firstBytesASCII;
        String bytesBase64 = Base64.getEncoder().encodeToString(bytes);
        sendMessage(Text.literal(message).styled((style) -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, bytesBase64))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to copy Base 64 data")))
        ), 0);
        return null;
    }

    private String fromCollection(Collection<String> c) {
        StringBuilder builder = new StringBuilder();
        for (String s : c) {
            builder.append(s).append("\n");
        }
        return builder.toString();
    }

    private String recipes(List<RecipeEntry<?>> list) {
        StringBuilder builder = new StringBuilder();
        for (RecipeEntry<?> r : list) {
        }
        return builder.toString();
    }

    private String stats(Map<Stat<?>, Integer> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Stat<?>, Integer> o : map.entrySet()) {
            builder.append(o.getKey().getName()).append(" --- ").append(o.getValue()).append("\n");
        }
        return builder.toString();
    }

    private String offers(TradeOfferList offers) {
        StringBuilder builder = new StringBuilder();
        for (TradeOffer o : offers) {
            builder.append(o.copySellItem().getItem().getName()).append(" -> ").append(o.getOriginalFirstBuyItem().getItem().getName()).append(" & ").append(o.getSecondBuyItem().get().getClass()).append("\n");
        }
        return builder.toString();
    }

    private String listProfiles(List<UUID> uuids) {
        StringBuilder builder = new StringBuilder();
        for (UUID i : uuids) {
            builder.append(i).append("\n");
        }
        return builder.toString();
    }

    private String listStacks(List<ItemStack> itemStacks) {
        StringBuilder builder = new StringBuilder();
        for (ItemStack i : itemStacks) {
            builder.append("name:").append(i.getName()).append("item").append(i.getItem().getName()).append("count").append(i.getCount()).append("\n");
        }
        return builder.toString();
    }

    private String mergeIds(int[] ids) {
        StringBuilder builder = new StringBuilder();
        for (int i : ids) {
            builder.append(i).append("\n");
        }
        return builder.toString();
    }

    private String mergeSuggestions(List<Suggestion> list) {
        StringBuilder builder = new StringBuilder();
        for (Suggestion s : list) {
            builder.append(s.getText()).append("\n");
        }
        return builder.toString();
    }

    private String merge(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s).append("\n");
        }
        return builder.toString();
    }

    private String signText(String[] strings) {
        StringBuilder builder = new StringBuilder();
        for (String s : strings) {
            builder.append(s).append("\n");
        }
        return builder.toString();
    }
}