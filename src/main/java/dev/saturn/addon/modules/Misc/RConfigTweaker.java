package dev.saturn.addon.modules.Misc;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class RConfigTweaker extends Module {
    public static RConfigTweaker INSTANCE;

    public RConfigTweaker() {
        super(Categories.Misc, "R-config-tweaker", "Tweaker for R Modules.");
        INSTANCE = this;
    }

    public final SettingGroup sgReaper = Config.get().settings.createGroup("Reaper");

}