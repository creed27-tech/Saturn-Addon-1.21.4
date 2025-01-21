package dev.saturn.addon.modules.Client;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;

public class MidClickFriend extends Module {
    public MidClickFriend() {
        super(Saturn.Client, "mid-click-friend", "WIP | Adds or removes a player as a friend using middle click.");
    }

    private final SettingGroup sgAdd = settings.createGroup("Add");
    private final SettingGroup sgRemove = settings.createGroup("Remove");

    // Add

    private final Setting<Boolean> friendAddMessage = sgAdd.add(new BoolSetting.Builder()
            .name("friend-add-message")
            .description("Sends a message to the player when you add them as a friend.")
            .defaultValue(false)
            .build()
    );

    private final Setting<String> friendAddMessageText = sgAdd.add(new StringSetting.Builder()
            .name("friend-add-message-text")
            .description("The message sent to the player after friending him.")
            .defaultValue("I just friended you on LemonClient!")
            .visible(friendAddMessage::get)
            .build()
    );

    // Remove

    private final Setting<Boolean> friendRemoveMessage = sgRemove.add(new BoolSetting.Builder()
            .name("friend-remove-message")
            .description("Sends a message to the player when you add them as a friend.")
            .defaultValue(false)
            .build()
    );

    private final Setting<String> friendRemoveMessageText = sgRemove.add(new StringSetting.Builder()
            .name("friend-remove-message-text")
            .description("The message sent to the player after unfriending him.")
            .defaultValue("I just unfriended you on LemonClient!")
            .visible(friendRemoveMessage::get)
            .build()
    );
            }