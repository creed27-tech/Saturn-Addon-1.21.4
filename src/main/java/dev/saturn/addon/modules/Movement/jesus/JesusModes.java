package dev.saturn.addon.modules.Movement.jesus;

public enum JesusModes {
    NCP,
    Matrix_Zoom,
    Matrix_Zoom_2,
    Vulcan_Exploit;

    @Override
    public String toString() {
        return super.toString().replace('_', ' ');
    }
}