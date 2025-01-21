package dev.saturn.addon.modules.Chat;

import dev.saturn.addon.Saturn;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import dev.saturn.addon.modules.VHModuleHelper;

import java.util.List;

public class VHArmorMessage extends VHModuleHelper {
    private final Setting<Boolean> toastNotification = this.setting("toast", "Will send the notification for your own armor as a toast instead of a chat message.", Boolean.valueOf(false));
    private final Setting<List<SoundEvent>> sound = this.setting("sound", "The sound it makes when the toast pops up.", this.sgGeneral, this.toastNotification::get, soundEvents -> {
        if (soundEvents.size() > 1) {
            soundEvents.remove(0);
        }
    }, SoundEvents.ENTITY_ITEM_BREAK);
    private final Setting<Integer> duration = this.setting("toast-duration", "For how long the toast should show up on your screen in seconds.", Integer.valueOf(6), this.toastNotification::get);
    private final Setting<Boolean> friends = this.setting("friends", "Whether to send a notification to friends or not.", Boolean.valueOf(true));
    private final Setting<Integer> threshold = this.setting("durability-threshold", "At what durability to notify.", Integer.valueOf(25), this.sgGeneral, 1.0, 100.0, 1, 100);
    private final Setting<String> message = this.setting("message", "defines the message to send when armor runs low", "Your {piece} {grammar} low on durability! ({percent}%)");
    private final Int2IntMap armor = new Int2IntOpenHashMap();

    public VHArmorMessage() {
        super(Saturn.Chat, "VH-armor-message", "WIP | Sends a message in chat when your or your friends armor runs low.");
        }
    }