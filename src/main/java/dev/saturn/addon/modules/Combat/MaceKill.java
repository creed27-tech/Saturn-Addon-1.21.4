package dev.saturn.addon.modules.Combat;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Method;

public class MaceKill extends Module {
    private final SettingGroup specialGroup2 = settings.createGroup("Disable \"Smash Attack\" in the Criticals module to make this module work.");
    private final SettingGroup specialGroup = settings.createGroup("Values higher than 22 only work on Paper/Spigot");
    private final Setting<Integer> fallHeight = specialGroup.add(new IntSetting.Builder()
            .name("Mace Power (Fall height)")
            .description("Simulates a fall from this distance")
            .defaultValue(22)
            .sliderRange(1, 170)
            .min(1)
            .build());
    private final Setting<Boolean> maxPower = specialGroup.add(new BoolSetting.Builder()
            .name("Maximum Mace Power (Paper/Spigot servers only)")
            .description("Simulates a fall from the highest air gap within 170 blocks")
            .defaultValue(false)
            .build());
    private final Setting<Boolean> packetDisable = specialGroup.add(new BoolSetting.Builder()
            .name("Disable When Blocked")
            .description("Does not send movement packets if the attack was blocked. (prevents death)")
            .defaultValue(true)
            .build());

    public MaceKill() {
        super(Categories.Combat, "MaceKill", "Makes the Mace powerful when swung.");
    }
}