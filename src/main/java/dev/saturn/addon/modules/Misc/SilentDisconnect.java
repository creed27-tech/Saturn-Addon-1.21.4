package dev.saturn.addon.modules.Misc;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class SilentDisconnect extends Module {
    public SilentDisconnect() {
        super(Categories.Misc, "silent-disconnect", "Won't show a disconnect screen when you disconnect.");
    }
}