package dev.saturn.addon.modules.Movement;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import dev.saturn.addon.modules.VHModuleHelper;

public class VHMoses extends VHModuleHelper {
    public final Setting<Boolean> lava = this.setting("lava", "Applies to lava too.", Boolean.valueOf(false));

    public VHMoses() {
        super(Categories.Movement, "VH-moses", "Lets you walk through water as if it was air.");
    }
}