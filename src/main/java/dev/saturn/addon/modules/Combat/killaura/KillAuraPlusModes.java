package dev.saturn.addon.modules.Combat.killaura;

public enum KillAuraPlusModes {
    None,
    Matrix;

    @Override
    public String toString() {
        return super.toString().replaceAll("Plus", "+").replaceAll("_", " ");
    }
}