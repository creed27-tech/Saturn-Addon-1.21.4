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

public class AutoCope extends VHModuleHelper {
    private final Setting<List<String>> messages = this.setting("messages", "The messages to send after you died.", this.sgGeneral, null, "Lag?", "I got 0 ticked");

    public AutoCope() {
        super(Saturn.Chat, "auto-cope", "Sends a message in chat when you die.");
    }

    @EventHandler(priority = 200)
    private void onOpenScreenEvent(Receive event) {
        Packet<?> var3 = event.packet;

        // Check if the packet is a DeathMessageS2CPacket
        if (var3 instanceof DeathMessageS2CPacket packet) {
            // Extract player-related information from the packet
            int entityId = extractEntityId(packet); // Replace with actual method if available

            // Compare the extracted entity ID with the player's ID
            if (entityId == this.mc.player.getId()) {
                TextUtils.sendNewMessage(this.messages.get());
            }
        }
    }

    // Helper method to extract the entity ID from the packet
    private int extractEntityId(DeathMessageS2CPacket packet) {
        // Add logic to retrieve the entity ID
        // For example, use reflection if no public method is available
        // Return a valid entity ID
        return -1; // Placeholder, replace with actual extraction logic
    }

    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();
        WButton placeholders = list.add(theme.button("Placeholders")).expandX().widget();
        placeholders.action = () -> new GuideScreen().show();
        return list;
    }
}