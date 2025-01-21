package dev.saturn.addon.modules.Chat;


import dev.saturn.addon.Saturn;
import dev.saturn.addon.utils.Automation.HIGUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;

public class ArmorNotify extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> threshold = sgGeneral.add(new DoubleSetting.Builder()
            .name("durability")
            .description("How low an armor piece needs to be to alert you (in %).")
            .defaultValue(20)
            .range(1, 100)
            .sliderRange(1, 100)
            .build()
    );

    public ArmorNotify() {
        super(Saturn.Chat, "armor-notify", "Notifies you when your armor pieces are low.");
    }

    private boolean alertedHelmet;
    private boolean alertedChestplate;
    private boolean alertedLeggings;
    private boolean alertedBoots;

    @Override
    public void onActivate() {
        alertedHelmet = false;
        alertedChestplate = false;
        alertedLeggings = false;
        alertedBoots = false;

    }
        }