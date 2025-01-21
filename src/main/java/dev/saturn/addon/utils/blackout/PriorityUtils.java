package dev.saturn.addon.utils.blackout;

import dev.saturn.addon.modules.Combat.*;

/**
 * @author OLEPOSSU
 */

public class PriorityUtils {
    // Tell me a better way to do this pls
    public static int get(Object module) {
        if (module instanceof AnchorAuraPlus) return 9;
        if (module instanceof AutoCrystalPlus) return 10;
        if (module instanceof PistonCrystal) return 10;
        if (module instanceof AutoMine) return 9;
        if (module instanceof FastXP) return 3;
        if (module instanceof HoleFillPlus) return 7;
        if (module instanceof HoleFillRewrite) return 7;
        if (module instanceof SurroundPlus) return 0;

        return 100;
    }
}