package dev.saturn.addon.modules.Funny;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class Radio extends Module {
    private final SettingGroup sgGeneral = settings.createGroup("General");

    private final Setting<Radios> radioChannel = sgGeneral.add(new EnumSetting.Builder<Radios>()
            .name("Radio Channel")
            .defaultValue(Radios.Radio1)
            .build()
    );

    private final Setting<Integer> volumeInt = sgGeneral.add(new IntSetting.Builder()
            .name("Volume")
            .description("Adjusts the volume of the radio.")
            .defaultValue(50)
            .range(0, 100)
            .sliderRange(0, 100)
            .build()
    );

    public Radio() {
        super(Saturn.Funny, "Radio", "Its a radio bitch");
    }

    @EventHandler
    private void onTick(TickEvent.Pre e) {
        setVolume(volumeInt.get());
    }

    private void setVolume(int volume) {
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfo) {
            Mixer mixer = AudioSystem.getMixer(info);
            if (!mixer.isLineSupported(Port.Info.SPEAKER)) continue;
            Port port;
            try {
                port = (Port)mixer.getLine(Port.Info.SPEAKER);
                port.open();
            } catch (LineUnavailableException e) {
                info("ennek nem szabadna megtörténnie..");
                return;
            }
            if (port.isControlSupported(FloatControl.Type.VOLUME)) {
                FloatControl vol = (FloatControl)port.getControl(FloatControl.Type.VOLUME);
                vol.setValue(volume / 100.0f);
            }
            port.close();
        }
    }

    public enum Radios {
        Radio1("https://icast.connectmedia.hu/5202/live.mp3"),
        SlagerFM("https://slagerfm.netregator.hu:7813/slagerfm128.mp3"),
        RetroRadio("https://icast.connectmedia.hu/5002/live.mp3"),
        BestFM("https://icast.connectmedia.hu/5102/live.mp3"),
        RockFM("https://icast.connectmedia.hu/5301/live.mp3"),
        KossuthRadio("https://icast.connectmedia.hu/4736/mr1.mp3");

        public final String URL;

        Radios(String URL) {
            this.URL = URL;
        }
    }
}