package dev.saturn.addon.modules.Combat;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class SyracuseAimAssist extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Double setting for distance with a slider
    private final Setting<Double> distance = sgGeneral.add(new DoubleSetting.Builder()
            .name("distance")
            .description("The distance to aim at.")
            .defaultValue(6.0)
            .min(3.0)
            .sliderMax(10.0)
            .build()
    );

    // Double setting for smoothness with a slider
    private final Setting<Double> smoothness = sgGeneral.add(new DoubleSetting.Builder()
            .name("smoothness")
            .description("The smoothness of the aim.")
            .defaultValue(6.0)
            .min(0.0)
            .sliderMax(10.0)
            .build()
    );

    // Boolean setting to aim only at visible entities
    private final Setting<Boolean> seeonly = sgGeneral.add(new BoolSetting.Builder()
            .name("seeonly")
            .description("Only aim at entities you can see.")
            .defaultValue(true)
            .build()
    );

    // Boolean settings for vertical and horizontal aiming
    private final Setting<Boolean> verticle = sgGeneral.add(new BoolSetting.Builder()
            .name("verticle")
            .description("Aim at vertical entities.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> horizontal = sgGeneral.add(new BoolSetting.Builder()
            .name("horizontal")
            .description("Aim at horizontal entities.")
            .defaultValue(false)
            .build()
    );

    // Constructor for SyracuseAimAssist
    public SyracuseAimAssist() {
        super(Categories.Combat, "syracuse-aim-assist", "Syracuse Aim Assist");
    }

    // Check if the player is over an entity
    public static boolean isOverEntity() {
        if (MinecraftClient.getInstance().crosshairTarget == null) {
            return false;
        } else {
            var hitResult = MinecraftClient.getInstance().crosshairTarget;
                return true;
                        }
                    }
                }