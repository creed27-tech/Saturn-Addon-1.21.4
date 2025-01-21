package dev.saturn.addon.modules.Chat;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import dev.saturn.addon.gui.screens.GuideScreen;
import dev.saturn.addon.modules.VHModuleHelper;
import dev.saturn.addon.utils.venomhack.TextUtils;

import java.util.List;

public class VHAutoCope extends VHModuleHelper {
    private final Setting<List<String>> messages = this.setting("messages", "The messages to send after you died.", this.sgGeneral, null, "Lag?", "I got 0 ticked");

    public VHAutoCope() {
        super(Saturn.Chat, "VH-auto-cope", "Sends a message in chat when you die.");
    }

    @EventHandler(priority = 200)
    private void onOpenScreenEvent(Receive event) {
        Packet<?> var3 = event.packet;
        if (var3 instanceof DeathMessageS2CPacket packet) {
            TextUtils.sendNewMessage(this.messages.get());
        }
    }

    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();
        WButton placeholders = list.add(theme.button("Placeholders")).expandX().widget();
        placeholders.action = () -> new GuideScreen().show();
        return list;
    }
}