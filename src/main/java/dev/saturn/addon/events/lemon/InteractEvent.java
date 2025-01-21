package dev.saturn.addon.events.lemon;

public class InteractEvent {
    private static final InteractEvent INSTANCE = new InteractEvent();
    public boolean usingItem;

    public static InteractEvent get(boolean usingItem) {
        INSTANCE.usingItem = usingItem;
        return INSTANCE;
    }
}