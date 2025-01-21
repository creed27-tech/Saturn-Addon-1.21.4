package dev.saturn.addon.modules.Misc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Send;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import dev.saturn.addon.modules.VHModuleHelper;

import java.util.ArrayList;
import java.util.List;

public class VHPingSpoof extends VHModuleHelper {
    private final Setting<Integer> delay = this.setting("spoof-amount", "Delay in ms to add to your ping.", Integer.valueOf(50), this.sgGeneral, 0.0, 100.0);
    private final Object2LongMap<KeepAliveC2SPacket> packets = new Object2LongOpenHashMap();
    private final List<KeepAliveC2SPacket> packetList = new ArrayList();

    public VHPingSpoof() {
        super(Categories.Misc, "ping-spoof-vh", "Makes your ping appear higher than it actually is. | Ported from Venomhack");
    }

    @EventHandler
    private void onPacketSend(Send event) {
        if (!this.mc.isInSingleplayer()) {
            Packet<?> var3 = event.packet;
            if (var3 instanceof KeepAliveC2SPacket p) {
                if (this.packetList.contains(p)) {
                    this.packets.removeLong(p);
                    this.packetList.remove(p);
                    return;
                }

                this.packets.put(p, System.currentTimeMillis());
                this.packetList.add(p);
                event.cancel();
            }
        }
    }

    @EventHandler
    private void onPacketRecieve(Receive event) {
        for (KeepAliveC2SPacket packet : this.packetList) {
            if (this.packets.getLong(packet) + (long) ((Integer) this.delay.get()).intValue() <= System.currentTimeMillis()) {
                this.mc.player.networkHandler.sendPacket(packet);
                break;
            }
        }
    }

    public void onDeactivate() {
        for (KeepAliveC2SPacket packet : this.packetList) {
            if (this.packets.getLong(packet) + (long) ((Integer) this.delay.get()).intValue() <= System.currentTimeMillis()) {
                this.mc.player.networkHandler.sendPacket(packet);
            }
        }
    }
}