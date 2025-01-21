package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class NoBob extends Module {
    public NoBob() {
        super(Categories.Render, "no-bob", "Disables hand animation.");
    }
}