package dev.saturn.addon.modules.Movement;

import meteordevelopment.meteorclient.events.packets.PacketEvent.Send;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class GrimSpeedMine extends Module {

    public GrimSpeedMine() {
        super(Categories.Movement, "grim-speed-mine", "use with normal speed mine");
    }

    @EventHandler
    public void onPacket(Send event) {
        // Use the correct packet types and field names
        var packet = event.packet;
        }
    }