package dev.saturn.addon.modules.Chat;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

public class BackupCaller extends Module {
    public BackupCaller() {
        super(Saturn.Chat, "backup-caller", "call for backup in chat");
    }

    @Override
    public void onActivate() {
        assert mc.player != null;
        ChatUtils.sendPlayerMsg("I Need Backup " + mc.player.getX() + " " + mc.player.getZ());

    }
}