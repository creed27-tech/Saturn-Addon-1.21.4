package dev.saturn.addon.modules.Experimental;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import dev.saturn.addon.utils.player.LaunchPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class MatrixElytra extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Double> BoostAmount;
    private final Setting<Double> FallDistance;
    private final Setting<Mode> mode;
    private final Setting<Boolean> AirBoost;
    private final Setting<Boolean> SlowDown;
    private final Setting<Double> AirBoostAmount;

    private int timer;
    private boolean launch;
    private boolean Boosted;

    public MatrixElytra() {
        super(Saturn.Experimental, "matrix-elytra", "A bypass to allow for elytra flight on matrix.");

        sgGeneral = settings.getDefaultGroup();

        BoostAmount = sgGeneral.add(new DoubleSetting.Builder()
                .name("Boost")
                .description("Boost Amount")
                .defaultValue(2.0)
                .range(0.1, 5.0)
                .sliderRange(0.1, 5.0)
                .build());

        FallDistance = sgGeneral.add(new DoubleSetting.Builder()
                .name("Fall Distance")
                .description("Amount of fall distance needed to trigger the boost.")
                .defaultValue(0.5)
                .range(0.1, 1.5)
                .sliderRange(0.1, 1.5)
                .build());

        mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
                .name("Boost Mode")
                .description("Mode for boosting")
                .defaultValue(Mode.Client)
                .build());

        AirBoost = sgGeneral.add(new BoolSetting.Builder()
                .name("AirBoost")
                .description("Will boost when you press space.")
                .defaultValue(false)
                .build());

        SlowDown = sgGeneral.add(new BoolSetting.Builder()
                .name("SlowDown")
                .description("Slow down when you hold shift/crouch.")
                .defaultValue(false)
                .build());

        AirBoostAmount = sgGeneral.add(new DoubleSetting.Builder()
                .name("Air Boost")
                .description("Air Boost Amount")
                .defaultValue(0.5)
                .range(0.1, 5.0)
                .sliderRange(0.1, 5.0)
                .visible(() -> AirBoost.get())
                .build());

        timer = 0;
        launch = false;
        Boosted = false;
    }

    public void onTick() {
        if (mode.get() == Mode.Client) {
            assert mc.player != null;

            // Check for fall distance and trigger boost
            if (mc.player.getY() < FallDistance.get() && !mc.player.isSneaking() && !launch) {
                LaunchPlayer.MatrixElytra(BoostAmount.get());
                launch = true;
            }

            if (mc.player.isSneaking()) {
                launch = false;
            }

            // AirBoost functionality
            if (AirBoost.get() && mc.player.isOnGround()) {
                if (mc.options.jumpKey.isPressed() && !Boosted) {
                    LaunchPlayer.MatrixElytra(AirBoostAmount.get());
                    Boosted = true;
                } else if (!mc.options.jumpKey.isPressed()) {
                    Boosted = false;
                }
            }

            // SlowDown when holding shift
            if (SlowDown.get() && mc.options.sneakKey.isPressed()) {
                LaunchPlayer.MatrixElytra(0.1D); // Slow down boost
            }
        }
    }

    public enum Mode {
        Client("Client"),
        Packet("Packet");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}