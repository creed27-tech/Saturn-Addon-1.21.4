package dev.saturn.addon.modules.Render;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import dev.saturn.addon.events.venomhack.PlayerListChangeEvent;
import dev.saturn.addon.modules.VHModuleHelper;
import dev.saturn.addon.utils.venomhack.RandUtils;

import java.util.ArrayList;
import java.util.List;

public class VHLogoutSpotsRewrite extends VHModuleHelper {
    private final SettingGroup sgRender = this.group("Render");
    private final Setting<Boolean> msg = this.setting("message", "Sends a chat message when a logged person logs back in.", Boolean.valueOf(true));
    private final Setting<Boolean> coords = this.setting("coords-in-message", "Defines whether the log back in message should contain the coordinates of the logout spot.", Boolean.valueOf(true), this.sgGeneral, this.msg::get);
    private final Setting<SettingColor> nameColor = this.setting("name-color", "The color for the name of the nametag.", 255, 255, 255, this.sgRender);
    private final Setting<SettingColor> sideColor = this.setting("side-color", "The side color.", 255, 215, 0, 70, this.sgRender);
    private final Setting<SettingColor> lineColor = this.setting("line-color", "The line color.", 255, 215, 0, this.sgRender);
    private final Setting<ShapeMode> shapeMode = this.setting("shape-mode", "The shape mode", ShapeMode.Both, this.sgRender);
    private final Setting<Double> scale = this.setting("nametag-scale", "The size of the nametag that renders the name and health of the logged player.", Double.valueOf(1.0), this.sgRender, 0.0, 3.0);
    private final List<PlayerEntity> lastPlayers = new ArrayList();
    private final List<Spot> spots = new ArrayList<>();

    public VHLogoutSpotsRewrite() {
        super(Categories.Render, "VH-logoutspots-rewrite", "Keeps track of where players log out.");
    }

    @EventHandler
    private void onTick(Post event) {
        this.lastPlayers.clear();

        for (Entity entity : this.mc.world.getEntities()) {
            if (entity instanceof PlayerEntity player && player != this.mc.player) {
                this.lastPlayers.add(player);
            }
        }
    }

    @EventHandler
    private void onLogout(PlayerListChangeEvent.Leave event) {
        for (PlayerEntity player : this.lastPlayers) {
            if (event.getPlayer().getProfile().getId().equals(player.getUuid())) {
                this.spots.removeIf(spot -> spot.player.getUuid().equals(player.getUuid()));
                player.lastRenderX = player.getX();
                player.lastRenderY = player.getY();
                player.lastRenderZ = player.getZ();
                player.prevBodyYaw = player.bodyYaw;
                player.prevHeadYaw = player.getHeadYaw();
                player.prevPitch = player.getPitch();
                this.spots.add(new Spot(player, PlayerUtils.getDimension()));
                return;
            }
        }
    }

    @EventHandler
    private void onLogin(PlayerListChangeEvent.Join event) {
        for (Spot spot : this.spots) {
            PlayerEntity player = spot.player;
            if (player.getUuid().equals(event.getPlayer().getProfile().getId())) {
                this.spots.remove(spot);
                if (this.msg.get()) {
                    StringBuilder message = new StringBuilder(player.getName() + " logged back in ");
                    if (this.coords.get()) {
                        message.append("at X: ").append(player.getBlockX()).append(" Y: ").append(player.getBlockY()).append(" Z: ").append(player.getBlockZ()).append(" ");
                    }

                    message.append("in the ").append(spot.dimension).append(" removing their logout spot.");
                    this.info(message.toString());
                    return;
                }
            }
        }
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        this.lastPlayers.clear();
        this.spots.clear();
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        this.spots.forEach(spot -> {
            if (spot.dimension == PlayerUtils.getDimension() && RandUtils.horizontalDistance(spot.player.getPos(), this.mc.player.getPos()) < (double) this.mc.gameRenderer.getViewDistance()) {
                WireframeEntityRenderer.render(event, spot.player, 1.0, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get());
            }
        });
    }

    public void onActivate() {
        this.lastPlayers.clear();
        this.spots.clear();
    }

    public List<Spot> getLogs() {
        return this.spots;
    }

    public boolean remove(Spot spot) {
        return this.spots.remove(spot);
    }

    public record Spot(PlayerEntity player, Dimension dimension) {
    }
}