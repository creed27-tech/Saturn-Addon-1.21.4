package dev.saturn.addon.modules.Misc;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class TpaSpammer extends Module {
    public TpaSpammer() {
        super(Categories.Misc, "tpa-spammer", "Spams /tpa to everyone.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder().name("delay").defaultValue(20).min(0).max(50).build());
    private final Setting<String> prefix = sgGeneral.add(new StringSetting.Builder().name("tpa").defaultValue("/tpa").build());
    private final Setting<List<String>> blackList = sgGeneral.add(new StringListSetting.Builder().name("black-list").defaultValue("vanya_pro").build());
    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder().name("ignore-friends").defaultValue(true).build());

    private int timer;

    @Override
    public void onActivate() {
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (timer >= delay.get()) {
            AtomicReference<String> target = new AtomicReference<>(generate());
            if (target.get().equals(mc.player.getName())) {
                target.set(generate());
            }
            if (ignoreFriends.get()) {
                Friends.get().forEach(friend -> {
                    if (friend.name.equals(target.get())) {
                        target.set(generate());
                    }
                });
            }
            for (PlayerEntity entity : mc.world.getPlayers()) {
                if (entity.getName().equals(target.get())) {
                    target.set(generate());
                }
            }
            if (blackList.get().contains(target.get())) {
                target.set(generate());
            }
            mc.getNetworkHandler().sendChatMessage(prefix.get() + " " + target.get());
            timer = 0;
        } else {
            timer++;
        }
    }

    private String generate() {
        ArrayList<String> names = new ArrayList<>();
        for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
            names.add(entry.getProfile().getName());
        }
        return names.get(random(names.size()));
    }

    private int random(int max) {
        return new Random().nextInt(max);
    }
}