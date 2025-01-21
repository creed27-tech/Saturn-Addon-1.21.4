package dev.saturn.addon.modules.Chat;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.systems.modules.Module;

public class SpamBypass extends Module {
    public SpamBypass() {
        super(Saturn.Chat, "spam-bypass", "put little string of text at the end");
    }
}