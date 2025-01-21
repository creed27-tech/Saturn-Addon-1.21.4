package dev.saturn.addon.modules.PVE;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BAntiInvisBlock extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> debug = sgGeneral.add(new BoolSetting.Builder()
            .name("debug")
            .defaultValue(false)
            .build());

    private final Setting<Integer> underFeet = sgGeneral.add(new IntSetting.Builder()
            .name("under-feet")
            .description("How many blocks under your feet it should start counting for horizontal")
            .defaultValue(0)
            .sliderRange(-5, 5)
            .build());

    private final Setting<Integer> horizontalRange = sgGeneral.add(new IntSetting.Builder()
            .name("horizontal-range")
            .defaultValue(4)
            .sliderRange(1, 6)
            .build());

    private final Setting<Integer> verticalRange = sgGeneral.add(new IntSetting.Builder()
            .name("vertical-range")
            .defaultValue(4)
            .sliderRange(1, 6)
            .build());

    public BAntiInvisBlock() {
        super(Saturn.PVE, "B+-anti-invis-block", "Tries to add nearby invisible blocks.");
    }

    private static final BlockPos.Mutable blockPos = new BlockPos.Mutable();

    @Override
    public void onActivate() {
        ClientPlayNetworkHandler conn = mc.getNetworkHandler();
        if (conn == null) {
            return;  // Early exit if there's no network connection
        }

        // Get player's current position
        BlockPos pos = mc.player.getBlockPos();

        // Loop through ranges for dz, dx, and dy to cover the area
        for (int dz = -horizontalRange.get(); dz <= horizontalRange.get(); dz++) {
            for (int dx = -horizontalRange.get(); dx <= horizontalRange.get(); dx++) {
                for (int dy = -verticalRange.get(); dy <= verticalRange.get(); dy++) {
                    // Set the new position for the block
                    BlockPos blockPos = pos.add(dx, dy + underFeet.get(), dz);  // Simplified position calculation

                    // Get the block state at the current position
                    BlockState blockState = mc.world.getBlockState(blockPos);

                    // Check if the block is air
                    if (blockState.isAir()) {
                        // Debug output if needed
                        if (debug.get()) {
                            info(String.valueOf(blockPos));
                        }

                        // Create and send packet to abort block destruction
                        PlayerActionC2SPacket packet = new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                                blockPos, Direction.UP
                        );
                        conn.sendPacket(packet);
                    }
                }
            }
        }

        // Toggle the module after activation
        toggle();
    }
}