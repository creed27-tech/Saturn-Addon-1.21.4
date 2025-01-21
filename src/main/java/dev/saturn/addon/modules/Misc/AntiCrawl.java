package dev.saturn.addon.modules.Misc;

import dev.saturn.addon.ov4Module;
import meteordevelopment.meteorclient.systems.modules.Categories;

public class AntiCrawl extends ov4Module {
    public AntiCrawl() {
        super(Categories.Misc, "anti-crawl", "Doesn't crawl or sneak when in low space (should be used on 1.12.2). | Ported from Lemon Client");
    }
}