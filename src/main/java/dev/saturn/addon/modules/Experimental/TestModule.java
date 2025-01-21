package dev.saturn.addon.modules.Experimental;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;

public class TestModule extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Integer setting with a slider
    private final Setting<Integer> pitch = sgGeneral.add(new IntSetting.Builder()
            .name("Integer")
            .description("Int Slider.")
            .defaultValue(1)
            .range(1, 360)
            .sliderRange(1, 360)
            .build()
    );

    // Enum setting for mode selection
    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("Modes")
            .defaultValue(Mode.Test1)
            .build()
    );

    // Boolean setting
    private final Setting<Boolean> booleansTestModule = sgGeneral.add(new BoolSetting.Builder()
            .name("Boolean")
            .description("True or False")
            .defaultValue(false)
            .build()
    );

    // String setting
    private final Setting<String> stringTestModule = sgGeneral.add(new StringSetting.Builder()
            .name("String")
            .description("Anything here")
            .defaultValue("This is a string.")
            .build()
    );

    // Constructor for TestModule
    public TestModule() {
        super(Saturn.Experimental, "test-module", "Module that should have all settings in it. | This does nothing!");
    }

    // Enum definition for the modes
    public enum Mode {
        Test1,
        Test2
    }

    // Optionally, if you want to include a main method for testing, you can do it like this
    public static void main(String[] args) {
        // Testing or launching logic can go here
    }
}
