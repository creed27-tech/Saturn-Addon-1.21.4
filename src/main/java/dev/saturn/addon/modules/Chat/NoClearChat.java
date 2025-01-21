package dev.saturn.addon.modules.Chat;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class NoClearChat extends Module {
    public NoClearChat() {
        super(Saturn.Chat, "no-chat-clear", "Stops shitty skript ClearChat");
    }

    @EventHandler
    private void revieveMessage(ReceiveMessageEvent event){
        String message = event.getMessage().getString();

        if (message.isEmpty() || (message.startsWith("§") && message.length() <= 2)){
            event.cancel();
        }
    }
}