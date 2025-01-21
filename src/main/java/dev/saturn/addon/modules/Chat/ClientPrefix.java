package dev.saturn.addon.modules.Chat;

import dev.saturn.addon.Saturn;
import dev.saturn.addon.utils.cLogUtils;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

public class ClientPrefix extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> global = sgGeneral.add(
            new BoolSetting.Builder()
                    .name("Global")
                    .description("Use the prefix on meteor client. If toggled when module is active, restart module.")
                    .defaultValue(true)
                    .build()
    );

    public ClientPrefix() {
        super(Saturn.Chat, "prefix-modifier", "Modifies the client's prefix.");
    }
        }