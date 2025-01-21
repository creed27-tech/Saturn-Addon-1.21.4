package dev.saturn.addon.events.reaper;

import net.minecraft.client.network.PlayerListEntry;

public class PlayerLeaveEvent {
    private static final PlayerLeaveEvent INSTANCE = new PlayerLeaveEvent();

    public PlayerListEntry player;

    public static PlayerLeaveEvent get(PlayerListEntry player) {
        INSTANCE.player = player;
        return INSTANCE;
    }
}