package dev.saturn.addon.modules.Combat;

import meteordevelopment.meteorclient.systems.modules.Categories;
import dev.saturn.addon.events.StopUsingItemEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class ArrowDmg extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Integer> packets = sgGeneral.add(new IntSetting.Builder()
            .name("packets")
            .description("Amount of packets to send. More packets = higher damage.")
            .defaultValue(200)
            .min(2)
            .sliderMax(2000)
            .build()
    );

    public final Setting<Boolean> tridents = sgGeneral.add(new BoolSetting.Builder()
            .name("tridents")
            .description("When enabled, tridents fly much further. Doesn't seem to affect damage or Riptide. WARNING: You can easily lose your trident by enabling this option!")
            .defaultValue(false)
            .build()
    );


    public ArrowDmg() {
        super(Categories.Combat, "arrow-damage", "Massively increases arrow damage, but also consumes a lot of hunger and reduces accuracy. Does not work with crossbows and seems to be patched on Paper servers.");
        }
    }
