package dev.saturn.addon.enums.reaper;

import dev.saturn.addon.utils.reaper.player.PlayerUtil;
import net.minecraft.util.math.Vec3d;

public enum AntiCheat {
    Vanilla,
    NoCheat;

    public Vec3d origin() {
        if (this == NoCheat) return PlayerUtil.properEyePos();
        return PlayerUtil.properFeetPos();
    }
}