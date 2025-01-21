package dev.saturn.addon.mixininterfaces.reaper;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public interface IBlink {
    Box getHitbox();
    Vec3d getEyePos();
    Vec3d getFeetPos();
}