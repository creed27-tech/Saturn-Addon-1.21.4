package dev.saturn.addon.modules.Combat;

import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.text.Text;
import dev.saturn.addon.modules.VHModuleHelper;

public class TotemLog extends VHModuleHelper {
    private final Setting<Integer> totems = this.setting("totem-amount", "The threshold to disconnect at.", Integer.valueOf(1));
    private final Setting<Boolean> toggleOff = this.setting("toggle-off", "Disables this after usage.", Boolean.valueOf(true));

    public TotemLog() {
        super(Categories.Combat, "totem-log-vh", "Automatically disconnects you when you drop to a certain totem count. | Ported from Venomhack");
    }

    @EventHandler
    private void onTick(Post event) {
        if (InvUtils.find(new Item[]{Items.TOTEM_OF_UNDYING}).count() <= this.totems.get()) {
            this.mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.literal("[AutoLog] You have " + this.totems.get() + " totems left!")));
            if (this.toggleOff.get()) {
                this.toggle();
            }
        }
    }
}