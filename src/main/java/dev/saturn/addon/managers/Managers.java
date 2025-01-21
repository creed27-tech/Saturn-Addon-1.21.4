package dev.saturn.addon.managers;

import dev.saturn.addon.managers.Blackout.OnGroundManager;
import dev.saturn.addon.managers.Blackout.RotationManager;

public class Managers {
    public static final OnGroundManager ON_GROUND = new OnGroundManager();

    public static final swapManager swapMng = new swapManager();
    public static final swapManager HOLDING = null;
    public static final RotationManager ROTATION = null;
}
