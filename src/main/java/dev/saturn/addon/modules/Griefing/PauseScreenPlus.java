package dev.saturn.addon.modules.Griefing;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;

public class PauseScreenPlus extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public Setting<Boolean> disconnectAndDeleteButton = sgGeneral.add(new BoolSetting.Builder()
            .name("Disconnect & Delete button")
            .description("Shows a disconnect & delete button")
            .defaultValue(true)
            .build()
    );

    public PauseScreenPlus() {
        super(Saturn.Griefing, "pause-screen-plus", "Shows \"More\" button instead of \"Give Feedback\" and makes CTRL + C copy the server IP.");
    }
}