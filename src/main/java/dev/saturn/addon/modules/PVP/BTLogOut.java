package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

import java.util.List;

public class BTLogOut extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> totems = sgGeneral.add(new BoolSetting.Builder().name("totems").description("Disconnects if you don't have totems.").defaultValue(true).build());
    private final Setting<Integer> totemCount = sgGeneral.add(new IntSetting.Builder().name("count").description("How many totems need to disconnect.").defaultValue(1).sliderRange(0, 36).visible(totems::get).build());
    private final Setting<Boolean> message = sgGeneral.add(new BoolSetting.Builder().name("message").description("Sends message before disconnect.").defaultValue(false).build());
    private final Setting<String> messageText = sgGeneral.add(new StringSetting.Builder().name("text").description("The text which you send.").defaultValue("You are very boring, I went to play on another server.").visible(message::get).build());
    private final Setting<Boolean> error = sgGeneral.add(new BoolSetting.Builder().name("error").description("Uses minecraft error instead of totem notification.").defaultValue(false).build());
    private final Setting<Boolean> toggle = sgGeneral.add(new BoolSetting.Builder().name("toggle").description("Turn's off module if player left the server.").defaultValue(true).build());

    public BTLogOut() {
        super(Saturn.PVP, "BT-log-out", "WIP | Automatically disconnects you when certain requirements are met.");
    }

    @Override
    public void onActivate() {

    }

    int ticks = 1;

    @EventHandler
    private void onTick(TickEvent.Post event) {
        FindItemResult totem = InvUtils.find(Items.TOTEM_OF_UNDYING);
        if (totems.get() && totem.count() <= totemCount.get()) {
            if (message.get() && messageText.get() != null && ticks == 1) {
                mc.player.sendMessage(new Text() {
                    @Override
                    public Style getStyle() {
                        return null;
                    }

                    @Override
                    public TextContent getContent() {
                        return null;
                    }

                    @Override
                    public List<Text> getSiblings() {
                        return List.of();
                    }

                    @Override
                    public OrderedText asOrderedText() {
                        return null;
                    }
                }, false);
            }
            ticks--;

            if (ticks <= 0) disconnect(error.get());
        }
    }

    private void disconnect(boolean error) {
        mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new Text() {
            @Override
            public Style getStyle() {
                return null;
            }

            @Override
            public TextContent getContent() {
                return null;
            }

            @Override
            public List<Text> getSiblings() {
                return List.of();
            }

            @Override
            public OrderedText asOrderedText() {
                return null;
            }
        }));
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        ticks = 1;

        if (!toggle.get()) return;
        toggle();
        return;
    }
}
