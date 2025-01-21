package dev.saturn.addon.utils;

import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.render.MeteorToast;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import static dev.saturn.addon.Saturn.Settings;
import static meteordevelopment.meteorclient.MeteorClient.mc;
public class cLogUtils {

    public static void sendMessage(String msg, Boolean stack) {
        if (mc.world == null) return;

        MutableText message = Text.empty();
        message.append(msg);

        int id;
        if (stack) {
            id = 93;
        } else {
            id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE); // unique id
        }

        if (!Config.get().deleteChatFeedback.get()) id = 0;

        ((IChatHud) mc.inGameHud.getChatHud()).meteor$add(message, id);
    }

    public static void sendTotemPopMessage(int pops, PlayerEntity player, Boolean stack, Boolean playerStack) {
        if (mc.world == null) return;

        MutableText message = Text.empty();
        message.append(player.getName().getString() + " popped " + Formatting.GOLD + Formatting.BOLD + pops + Formatting.RESET + (pops == 1 ? " totem" : " totems"));

        int id;
        if (stack) {
            if (playerStack) {
                id = player.getId();
            } else {
                id = 103;
            }
        } else {
            id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        }

        if (!Config.get().deleteChatFeedback.get()) id = 0;

        ((IChatHud) mc.inGameHud.getChatHud()).meteor$add(message, id);
    }

    public static void sendRawMessage(String msg) {
        if (mc.world == null) return;
        mc.inGameHud.getChatHud().addMessage(Text.of(msg));
    }

    public static void sendNotification(String message) {
        mc.getToastManager().add(new MeteorToast(Items.BROWN_STAINED_GLASS_PANE, message, message, 1000));
    }

    static String rgbaToHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public static String getStringPrefix() {
        return Formatting.GRAY + "[" + Formatting.GOLD + "Saturn" + Formatting.YELLOW + "Addon" + Formatting.GRAY + "]" + Formatting.RESET;
    }
}