package dev.saturn.addon.modules.Render;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class SkinChanger extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgSkin = settings.createGroup("Skin");
    private final SettingGroup sgMisc = settings.createGroup("Misc");

    private final Setting<Boolean> enableSkinChanger = sgGeneral.add(new BoolSetting.Builder()
            .name("Enable Skin Changer")
            .description("Enables Skin Changer")
            .defaultValue(true)
            .build()
    );

    private final Setting<SkinMode> skinMode = sgSkin.add(new EnumSetting.Builder<SkinMode>()
            .name("Skin Mode")
            .description("What Pre-built skins do you want to use")
            .defaultValue(SkinMode.Cute)
            .build()
    );

    private final Setting<CuteSkinMode> cuteSkinMode = sgSkin.add(new EnumSetting.Builder<CuteSkinMode>()
            .name("Cute Skin Mode")
            .description("What cute skin to use.")
            .defaultValue(CuteSkinMode.Fox)
            .build()
    );

    // String setting
    private final Setting<String> stringTestModule = sgGeneral.add(new StringSetting.Builder()
            .name("String")
            .description("Anything here")
            .defaultValue("This is a string.")
            .build()
    );

    public SkinChanger() {
        super(Categories.Render, "skin-changer", "WIP | Changes everyone's skin | Client-side only!");
    }

    // All Listed enum Modes
    public enum SkinMode {
        Cute,
        Femboy,
        Furry,
        Classic,
        Custom
    }

    public enum CuteSkinMode {
        Fox,
        Duck,
        Youtuber,
        Chicken,
        Cow
    }

    // Optionally, if you want to include a main method for testing, you can do it like this
    public static void main(String[] args) {
        // Testing or launching logic can go here
    }
}
