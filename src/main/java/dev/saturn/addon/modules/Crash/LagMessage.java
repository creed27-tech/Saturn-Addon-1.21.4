package dev.saturn.addon.modules.Crash;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class LagMessage extends Module {
    private final Setting<Integer> messageLength;
    private final Setting<Boolean> keepSending;
    private final Setting<Integer> delay;
    private final Setting<Boolean> whisper;
    private final Setting<Boolean> autoDisable;
    private int timer;

    public LagMessage() {
        super(Saturn.Crash, "lag-message", "Sends a large message of complex characters to lag other players on a server.");
        SettingGroup sgGeneral = this.settings.getDefaultGroup();
        this.messageLength = sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("message-length")).description("The length of the message.")).defaultValue(200)).min(1).sliderMin(1).sliderMax(1000).build());
        this.keepSending = sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("keep-sending")).description("Keeps sending the lag messages repeatedly.")).defaultValue(false)).build());
        IntSetting.Builder delayBuilder = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The delay between lag messages in ticks.")).defaultValue(100)).min(0).sliderMax(1000);
        this.delay = sgGeneral.add(delayBuilder.visible(this.keepSending::get).build());
        this.whisper = sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("whisper")).description("Whispers the lag message to a random player on the server.")).defaultValue(false)).build());
        this.autoDisable = sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-disable")).description("Disables the module upon being kicked.")).defaultValue(true)).build());
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (this.timer <= 0) {
            if (Utils.canUpdate() && this.keepSending.get()) {
                if (!this.whisper.get()) {
                    this.sendLagMessage();
                } else {
                    this.sendLagWhisper();
                }
            }
            this.timer = this.delay.get();
        } else {
            --this.timer;
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (this.autoDisable.get() && this.isActive()) {
            this.toggle();
        }
    }

    private void sendLagMessage() {
        String message = this.generateLagMessage();
    }

    private void sendLagWhisper() {
        if (mc.world != null && mc.world.getPlayers() != null) {
            String message = this.generateLagMessage();
        }
    }

    private String generateLagMessage() {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < this.messageLength.get(); ++i) {
            message.append((char)((int)(Math.random() * 119552) + 2048));
        }
        return message.toString();
    }
}