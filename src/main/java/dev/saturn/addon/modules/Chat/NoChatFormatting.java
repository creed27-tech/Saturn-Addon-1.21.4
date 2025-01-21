package dev.saturn.addon.modules.Chat;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;

public class NoChatFormatting extends Module {
    public NoChatFormatting() {
        super(Saturn.Chat, "no-chat-formatting", "Stops Chat Colour Codes");
    }

    @EventHandler
    private void onRevieve(ReceiveMessageEvent event){
        event.setMessage(Text.literal(event.getMessage().getString()));
    }
}