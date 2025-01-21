package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class ExternalFeed extends Module {
    public ExternalFeed() {
        super(Categories.Render, "external-killfeed", "Renders a killfeed outside the client. | Ported from Reaper");
    }
}