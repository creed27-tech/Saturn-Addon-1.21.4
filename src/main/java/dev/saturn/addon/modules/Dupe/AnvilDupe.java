package dev.saturn.addon.modules.Dupe;


import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

/**
 * Allows automatically duplicating items using the 1.17 anvil dupe, specifically the GoldenDupes version.
 */
public class AnvilDupe extends Module {
    /**
     * Constructor for the AnvilDupe module, setting the category and description of the module.
     */
    public AnvilDupe() {
        super(Saturn.Dupe, "auto-anvil-dupe", "Automatically dupes using anvil dupe.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> dupeAmount = sgGeneral.add(new IntSetting.Builder()
            .name("dupe-amount")
            .description("How many items to dupe before toggling.")
            .defaultValue(54)
            .min(1)
            .sliderMax(270)
            .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("How many items to dupe before toggling.")
            .defaultValue(1)
            .min(1)
            .sliderMax(20)
            .build()
    );

    private final Setting<Integer> bottleAmount = sgGeneral.add(new IntSetting.Builder()
            .name("Amount of bottles to throw")
            .description("How many XP bottles to throw.")
            .defaultValue(1)
            .min(1)
            .sliderMax(20)
            .build()
    );

            }