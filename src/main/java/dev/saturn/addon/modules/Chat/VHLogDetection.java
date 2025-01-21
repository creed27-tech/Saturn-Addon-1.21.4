package dev.saturn.addon.modules.Chat;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import dev.saturn.addon.events.venomhack.PlayerListChangeEvent;
import dev.saturn.addon.gui.screens.GuideScreen;
import dev.saturn.addon.modules.VHModuleHelper;
import dev.saturn.addon.utils.venomhack.TextUtils;

import java.util.List;

public class VHLogDetection extends VHModuleHelper {
    private final SettingGroup sgMessages = this.settings.createGroup("Log Messages");
    private final Setting<Integer> delay = this.setting("delay", "Minimum ticks between sending messages.", Integer.valueOf(5), this.sgGeneral, 0.0, 20.0);
    private final Setting<List<String>> messages = this.setting("", "A random message will be chosen to make fun of your victims.", this.sgMessages, null, "LMAO {player} just logged. Venomhack owns me and all!");
    private int delayLeft;

    public VHLogDetection() {
        super(Saturn.Chat, "VH-log-detection", "WIP | Sends a chat message when someone combat logs.");
    }
}