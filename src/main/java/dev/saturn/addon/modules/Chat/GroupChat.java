package dev.saturn.addon.modules.Chat;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;

import java.util.List;

public class GroupChat extends Module {
    final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> players = sgGeneral.add(new StringListSetting.Builder()
            .name("players")
            .description("Players to message.")
            .build()
    );

    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
            .name("command")
            .description("How the message command is set up on the server.")
            .defaultValue("/msg {player} {message}")
            .build()
    );

    public GroupChat() {
        super(Saturn.Chat, "group-chat", "Talk with people privately with /msg.");
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) {
        for(String playerString: players.get()) {
            for(PlayerListEntry onlinePlayer: mc.getNetworkHandler().getPlayerList()) {
                if(onlinePlayer.getProfile().getName().equalsIgnoreCase(playerString)) {
                    break;
                }
            }
        }

        event.cancel();
    }


}