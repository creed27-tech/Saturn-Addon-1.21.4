package dev.saturn.addon.modules.Client;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class BotNet extends Module {
    public BotNet() {
        super(Saturn.Client, "bot-net", "Connect bots.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<SockMode> mode = sgGeneral.add(new EnumSetting.Builder<SockMode>()
            .name("mode")
            .defaultValue(SockMode.Client)
            .build()
    );

    public final Setting<String> connectHost = sgGeneral.add(new StringSetting.Builder()
            .name("connect-url")
            .defaultValue("127.0.0.1")
            .visible(() -> mode.get().equals(SockMode.Client))
            .build()
    );

    public final Setting<Integer> connectPort = sgGeneral.add(new IntSetting.Builder()
            .name("connect-port")
            .defaultValue(14515)
            .range(1, 65536)
            .sliderRange(1, 65535)
            .visible(() -> mode.get().equals(SockMode.Client))
            .build()
    );

    public final Setting<Integer> serverPort = sgGeneral.add(new IntSetting.Builder()
            .name("server-port")
            .defaultValue(14515)
            .range(1, 65536)
            .sliderRange(1, 65535)
            .visible(() -> mode.get().equals(SockMode.Server))
            .build()
    );

    public static SockMode getMode() {
        return Modules.get().get(BotNet.class).mode.get();
    }

    public static String getConnectHost() {
        return Modules.get().get(BotNet.class).connectHost.get();
    }

    public static int getConnectPort() {
        return Modules.get().get(BotNet.class).connectPort.get();
    }

    public static int getServerPort() {
        return Modules.get().get(BotNet.class).serverPort.get();
    }

    public enum SockMode {
        Client,
        Server
    }
}