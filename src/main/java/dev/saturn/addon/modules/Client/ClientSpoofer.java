package dev.saturn.addon.modules.Client;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class ClientSpoofer extends Module {
    private final SettingGroup sgVersion;
    private final SettingGroup sgWatermark;
    private final SettingGroup sgChatFeedback;
    private final SettingGroup sgWindow;
    public final Setting<Boolean> version;
    public final Setting<String> versionText;
    public final Setting<Boolean> watermark;
    public final Setting<String> watermarkText;
    public final Setting<Boolean> watermarkMeteorIcon;
    public final Setting<Boolean> chatFeedback;
    public final Setting<String> chatFeedbackText;
    public final Setting<Boolean> chatFeedbackChangeTextColor;
    public final Setting<SettingColor> chatFeedbackTextColor;
    public final Setting<Boolean> chatFeedbackMeteorIcon;

    public ClientSpoofer() {
        super(Saturn.Client, "client-spoof", "Allows you to change the name of the client.");
        this.sgVersion = this.settings.createGroup("Version");
        this.sgWatermark = this.settings.createGroup("Watermark");
        this.sgChatFeedback = this.settings.createGroup("Chat Feedback");
        this.sgWindow = this.settings.createGroup("Window");
        this.version = this.sgVersion.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enabled")).description("Changes the client version.")).defaultValue(false)).build());
        this.versionText = this.sgVersion.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("text")).description("The text to replace the version with.")).defaultValue("0.4.7")).build());
        this.watermark = this.sgWatermark.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enabled")).description("Changes the watermark client name.")).defaultValue(false)).build());
        this.watermarkText = this.sgWatermark.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("text")).description("The text to replace the watermark with.")).defaultValue("Meteor Client")).build());
        this.watermarkMeteorIcon = this.sgWatermark.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("meteor-icon")).description("Changes the watermark icon to Meteor.")).defaultValue(true)).build());
        this.chatFeedback = this.sgChatFeedback.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enabled")).description("Changes the chat feedback client name.")).defaultValue(false)).build());
        this.chatFeedbackText = this.sgChatFeedback.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("text")).description("The text to replace chat feedback with.")).defaultValue("Meteor")).build());
        this.chatFeedbackChangeTextColor = this.sgChatFeedback.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("change-color")).description("Changes the chat feedback client name color.")).defaultValue(true)).build());
        this.chatFeedbackTextColor = this.sgChatFeedback.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("The text color of the chat feedback.")).defaultValue(new SettingColor(145, 61, 226))).build());
        this.chatFeedbackMeteorIcon = this.sgChatFeedback.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("meteor-icon")).description("Changes the chat feedback icon to Meteor.")).defaultValue(true)).build());
    }

    // Optionally, if you want to include a main method for testing, you can do it like this
    public static void main(String[] args) {
        // Testing or launching logic can go here
    }

    public void onDeactivate() {
    }

    public boolean changeVersion() {
        return this.isActive() && (Boolean)this.version.get();
    }

    public boolean changeWatermark() {
        return this.isActive() && (Boolean)this.watermark.get();
    }

    public boolean changeWatermarkIcon() {
        return this.isActive() && (Boolean)this.watermark.get() && (Boolean)this.watermarkMeteorIcon.get();
    }

    public boolean changeChatFeedback() {
        return this.isActive() && (Boolean)this.chatFeedback.get();
    }

    public boolean changeChatFeedbackColor() {
        return this.isActive() && (Boolean)this.chatFeedback.get() && (Boolean)this.chatFeedbackChangeTextColor.get();
    }

    public boolean changeChatFeedbackIcon() {
        return this.isActive() && (Boolean)this.chatFeedback.get() && (Boolean)this.chatFeedbackMeteorIcon.get();
    }
}
    