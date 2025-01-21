package dev.saturn.addon.modules.Movement.nofall;

public enum NoFallModes {
    Matrix_New,
    Vulcan,
    Vulcan_2dot7dot7,
    Verus,
    Elytra_Clip,
    Elytra_Fly,
    No_Ground,
    No_Ground_Elytra;
    @Override
    public String toString() {
        return super.toString().replace('_', ' ').replaceAll("dot", ".");
    }
}