package dev.saturn.addon.modules.Player;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class PaperClickTp extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // Boolean setting for rendering a block overlay
    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .description("Renders a block overlay where you will be teleported.")
            .defaultValue(true)
            .build()
    );

    // Color settings for block rendering
    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color-solid-block")
            .description("The color of the sides of the blocks being rendered.")
            .defaultValue(new SettingColor(255, 0, 255, 15))
            .visible(render::get)
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color-solid-block")
            .description("The color of the lines of the blocks being rendered.")
            .defaultValue(new SettingColor(255, 0, 255, 255))
            .visible(render::get)
            .build()
    );

    // Constructor for PaperClickTp module
    public PaperClickTp() {
        super(Categories.Player, "paper-click-tp", "Teleports you to the block you are looking at on paper servers.");
    }

    // Method to handle teleportation when keybind is pressed
    private void teleportToBlock() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ClientWorld world = MinecraftClient.getInstance().world;

        if (player != null && world != null) {
            Vec3d cameraPos = player.getCameraPosVec(1.0f);
            float pitch = player.getPitch();
            float yaw = player.getYaw();
            Vec3d rotationVec = new Vec3d(Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                    Math.sin(Math.toRadians(pitch)),
                    Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            Vec3d raycastEnd = cameraPos.add(rotationVec.multiply(300.0));
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        assert MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ClientWorld world = MinecraftClient.getInstance().world;
        Vec3d cameraPos = player.getCameraPosVec(1.0f);
        float pitch = player.getPitch();
        float yaw = player.getYaw();
        Vec3d rotationVec = new Vec3d(Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                Math.sin(Math.toRadians(pitch)),
                Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        Vec3d raycastEnd = cameraPos.add(rotationVec.multiply(300.0));
        }
    }