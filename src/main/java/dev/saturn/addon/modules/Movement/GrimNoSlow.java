package dev.saturn.addon.modules.Movement;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.network.ClientPlayerEntity;

public class GrimNoSlow extends Module {
    private final SettingGroup sgGeneral;
    private Setting<Integer> slotIndex;
    private Setting<Integer> button;
    private boolean packetSent;
    private int timer;

    public GrimNoSlow() {
        super(Categories.Movement, "grim-no-slow", "GRIM FAIL 2023 COPE VERY HALAL XDDDD");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.slotIndex = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("slot-index")).description("The slotIndex of the Packet")).noSlider().defaultValue(36)).build());
        this.button = this.sgGeneral.add(((Builder)((Builder)((Builder)(new Builder()).name("button")).description("The button of the Packet")).noSlider().defaultValue(0)).build());
        this.timer = 0;
    }

    @Override
    public void onActivate() {
        this.packetSent = false;
        this.timer = 0;
    }

    @EventHandler
    public void onTick(Post e) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;

        if (player != null) {
            if (this.timer > 0) {
                --this.timer;
            }

            if (this.timer == 0 && player.isSprinting()) {
                for (int i = 0; i < 2; ++i) {
                    Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
                    map.put(0, new ItemStack(net.minecraft.item.Items.GOLDEN_APPLE)); // Example item (replace with actual item if needed)
                    // Send custom packet for your action
                }

                this.packetSent = true;
                this.timer = 5;
            }
        }
    }

    public boolean canNoSlow() {
        return this.isActive() && this.packetSent;
    }
}