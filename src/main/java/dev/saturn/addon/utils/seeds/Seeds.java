package dev.saturn.addon.utils.seeds;

import java.util.HashMap;

import dev.saturn.addon.events.SeedChangedEvent;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.util.math.random.RandomSeed;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Seeds extends System<Seeds> {
    private static final Seeds INSTANCE = new Seeds();

    public HashMap<String, Seed> seeds = new HashMap<>();

    public Seeds() {
        super("seeds");
        init();
        load(MeteorClient.FOLDER);
    }

    public static RandomSeed get() {
        return null;
    }
}