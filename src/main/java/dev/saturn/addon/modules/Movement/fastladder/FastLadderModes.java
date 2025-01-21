package dev.saturn.addon.modules.Movement.fastladder;

public enum FastLadderModes {
    Spartan;

    @Override
    public String toString() {
        return super.toString().replace('_', ' ');
    }
}