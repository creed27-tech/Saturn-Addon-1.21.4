package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.Saturn;
import dev.saturn.addon.utils.bed.port.other.TimerUtils;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BTLogSpots extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // General
    private final Setting<Boolean> nameRender = sgGeneral.add(new BoolSetting.Builder().name("name").defaultValue(true).build());
    private final Setting<Boolean> healthRender = sgGeneral.add(new BoolSetting.Builder().name("health").defaultValue(true).build());
    private final Setting<Boolean> coordRender = sgGeneral.add(new BoolSetting.Builder().name("coordinates").defaultValue(false).build());
    private final Setting<Boolean> timePassed = sgGeneral.add(new BoolSetting.Builder().name("time-passed").defaultValue(true).build());
    private final Setting<Boolean> armorCheck = sgGeneral.add(new BoolSetting.Builder().name("armor-check").defaultValue(true).build());

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder().name("scale").defaultValue(1).min(0.2).sliderRange(0.2, 2).build());
    private final Setting<Boolean> notification = sgGeneral.add(new BoolSetting.Builder().name("notification").defaultValue(true).build());

    // Render
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>().name("shape-mode").defaultValue(ShapeMode.Both).build());
    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder().name("side-color").description("The side color.").defaultValue(new SettingColor(79, 90, 112, 10)).build());
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder().name("line-color").description("The line color.").defaultValue(new SettingColor(79, 90, 112)).build());
    private final Setting<SettingColor> nameColor = sgRender.add(new ColorSetting.Builder().name("name-color").description("The name color.").defaultValue(new SettingColor(255, 255, 255)).build());
    private final Setting<SettingColor> nameBackgroundColor = sgRender.add(new ColorSetting.Builder().name("name-background-color").description("The name background color.").defaultValue(new SettingColor(0, 0, 0, 75)).build());

    private final List<PlayerListEntry> lastPlayerList = new ArrayList<>();
    private final List<PlayerEntity> lastPlayers = new ArrayList<>();

    private int timer;
    private Dimension lastDimension;

    public BTLogSpots() {
        super(Saturn.PVP, "log-spots", "Coming soon | Displays a box where another player has logged out at.");
        lineColor.onChanged();
    }

    @Override
    public void onActivate() {
        lastPlayerList.addAll(mc.getNetworkHandler().getPlayerList());
        updateLastPlayers();

        timer = 10;
        lastDimension = PlayerUtils.getDimension();
    }

    private void updateLastPlayers() {
        lastPlayers.clear();
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity) lastPlayers.add((PlayerEntity) entity);
        }
    }

    private ItemStack getItem(int i,  PlayerEntity playerEntity) {
        if (playerEntity == null) return ItemStack.EMPTY;

        return switch (i) {
            case 4 -> playerEntity.getOffHandStack();
            case 5 -> playerEntity.getMainHandStack();
            default -> playerEntity.getInventory().getArmorStack(i);
        };
    }
}
