package dev.saturn.addon.modules.Chat;

import dev.saturn.addon.Saturn;
import dev.saturn.addon.modules.VHModuleHelper;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import dev.saturn.addon.events.venomhack.PlayerDeathEvent;
import dev.saturn.addon.gui.screens.GuideScreen;
import dev.saturn.addon.utils.venomhack.TextUtils;

import java.util.List;

public class VHAutoEz extends VHModuleHelper {
    private final SettingGroup sgMessages = this.settings.createGroup("EZ Messages");
    private final SettingGroup sgKillstreak = this.settings.createGroup("Killstreak");
    private final Setting<Boolean> dms = this.setting("direct-messages", "Whether to send the ez message as a dm to the victim or not.", Boolean.valueOf(false), this.sgGeneral);
    private final Setting<Integer> delay = this.setting("delay", "How many ticks between being able to ez someone.", Integer.valueOf(5), this.sgGeneral, 0.0, 20.0);
    private final Setting<Boolean> chat = this.setting("only-on-kill-messages", "Will only send an ez message when you get the kill message in chat", Boolean.valueOf(false), this.sgGeneral);
    private final Setting<List<String>> messages = this.setting("", "A random message will be chosen to humiliate your victims with.", this.sgMessages, null, "{player} died to the power of Venomhack420!", "You can tell {player} isn't part of Venomforce:  https://discord.gg/VqRd4MJkbY", "EZ {player}. Venomhack owns me and all!");
    private final Setting<Boolean> addKsSuffix = this.setting("always-add-killstreak", "Will append your current killstreak to all your ez messages automatically.", Boolean.valueOf(false), this.sgKillstreak);
    private final Setting<String> ksSuffixMsg = this.setting("killstreak-suffix-message", "Use {ks} for the number of kills and {ksSuffix} for the ending like st, nd, rd or th.", " | {ks}{ksSuffix} kill in a row!", this.sgKillstreak, this.addKsSuffix::get);
    private final Setting<Boolean> sayKillStreak = this.setting("announce-killstreak", "Will send a special message each X kills instead of a normal ez one.", Boolean.valueOf(true), this.sgKillstreak);
    private final Setting<Integer> ksCount = this.setting("each-X-messages", "After how many X kills to send the killstreak message.", Integer.valueOf(5), this.sgKillstreak, this.sayKillStreak::get, 0.0, 10.0);
    private final Setting<String> ksMsg = this.setting("killstreak-message", "Use {ks} for the number of kills and {ksSuffix} for the ending like st, nd, rd or th.", "EZ. {ks}{ksSuffix} kill in a row. Venomhack on top.", this.sgKillstreak, this.sayKillStreak::get);
    private int delayLeft;

    public VHAutoEz() {
        super(Saturn.Chat, "VH-autoEZ", "WIP | Automatically sends a message in chat when you kill someone.");
    }

    public void onActivate() {
        this.delayLeft = 0;
    }

    @EventHandler
    private void onTick(Post event) {
        --this.delayLeft;
    }
    }