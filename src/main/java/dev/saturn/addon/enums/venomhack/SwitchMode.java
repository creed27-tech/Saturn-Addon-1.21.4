package dev.saturn.addon.enums.venomhack;

public enum SwitchMode {
    NONE("No Switch"),
    AUTO("Auto Switch"),
    SILENT("Silent Switch");

    private final String title;

    private SwitchMode(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return this.title;
    }
}