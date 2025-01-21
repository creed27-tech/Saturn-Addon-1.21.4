package dev.saturn.addon.modules.PVP;

import dev.saturn.addon.Saturn;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import static dev.saturn.addon.utils.bed.basic.BlockInfo.getBlock;

public class BTAntiRespawnLose extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> beds = sgGeneral.add(new BoolSetting.Builder().name("bed").description("Prevent from losing Bed respawn point.").defaultValue(true).build());
    private final Setting<Boolean> anchors = sgGeneral.add(new BoolSetting.Builder().name("anchor").description("Prevent from losing Anchor respawn point.").defaultValue(true).build());

    public BTAntiRespawnLose() {
        super(Saturn.PVP, "BT-anti-respawn-lose", "Prevent the player from losing the respawn point.");
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (!(event.packet instanceof PlayerInteractBlockC2SPacket)) return;

        BlockPos blockPos = ((PlayerInteractBlockC2SPacket) event.packet).getBlockHitResult().getBlockPos();

        // Get the current dimension key
        RegistryKey<World> dimensionKey = mc.world.getRegistryKey();

        // Compare the dimension key to determine the Overworld and Nether
        boolean isOverworld = dimensionKey.equals(World.OVERWORLD); // Check if it's the Overworld
        boolean isNether = dimensionKey.equals(World.NETHER); // Check if it's the Nether
        boolean isBed = getBlock(blockPos) instanceof BedBlock;
        boolean isAnchor = getBlock(blockPos).equals(Blocks.RESPAWN_ANCHOR);

        // Prevent losing respawn point if the player interacts with a bed in the Overworld
        if (beds.get() && isBed && isOverworld) {
            event.cancel();
        }

        // Prevent losing respawn point if the player interacts with a respawn anchor in the Nether
        if (anchors.get() && isAnchor && isNether) {
            event.cancel();
        }
    }
}
