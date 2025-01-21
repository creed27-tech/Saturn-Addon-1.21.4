package dev.saturn.addon.modules.Render;

import java.util.Iterator;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;

public class ScoreboardReplace extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<String> find;
    private final Setting<String> replace;

    public ScoreboardReplace() {
        super(Categories.Render, "scoreboard-replace", "Replaces the scoreboard with a custom one. || Broken someone fix this");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.find = this.sgGeneral.add(new StringSetting.Builder().name("find").description("The text to find").defaultValue("&ehypixel.net").build());
        this.replace = this.sgGeneral.add(new StringSetting.Builder().name("replace").description("The text to replace with").defaultValue("&cVolcanware.xyz").build());
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (MinecraftClient.getInstance().getServer() != null) {
            if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getScoreboard() != null) {
                Scoreboard scoreboard = MinecraftClient.getInstance().player.getScoreboard();
                Iterator<ScoreboardObjective> iterator = scoreboard.getObjectives().iterator();

                while (iterator.hasNext()) {
                    ScoreboardObjective objective = iterator.next();
                    if (objective.getName().equals(this.find.get())) {
                        objective.setDisplayName(Text.of(this.replace.get()));
                    }
                }
            }
        }
    }
}