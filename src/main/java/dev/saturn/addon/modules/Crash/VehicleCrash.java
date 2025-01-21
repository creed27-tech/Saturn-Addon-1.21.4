package dev.saturn.addon.modules.Crash;

import java.util.Objects;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.PlaySoundEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.server.network.ServerPlayerEntity;

public class VehicleCrash extends Module {
    private final Setting<Integer> amount;
    private final Setting<Boolean> noSound;
    private final Setting<Boolean> autoDisable;
    private final Setting<Boolean> onGround;

    public VehicleCrash() {
        super(Saturn.Crash, "vehicle-crash", "WIP | Attempts to crash the server while the player is in a vehicle (boat or minecart).");
        SettingGroup sgGeneral = this.settings.getDefaultGroup();
        this.amount = sgGeneral.add(((IntSetting.Builder) ((IntSetting.Builder) ((IntSetting.Builder) new IntSetting.Builder())
                .name("amount"))
                .description("How many packets to send to the server per tick."))
                .defaultValue(100)
                .min(1)
                .sliderMax(1000)
                .build());
        this.noSound = sgGeneral.add(((BoolSetting.Builder) ((BoolSetting.Builder) ((BoolSetting.Builder) new BoolSetting.Builder())
                .name("no-sound"))
                .description("Blocks the noisy paddle sounds."))
                .defaultValue(false)
                .build());
        this.autoDisable = sgGeneral.add(((BoolSetting.Builder) ((BoolSetting.Builder) ((BoolSetting.Builder) new BoolSetting.Builder())
                .name("auto-disable"))
                .description("Disables module on kick."))
                .defaultValue(true)
                .build());
        this.onGround = sgGeneral.add(((BoolSetting.Builder) ((BoolSetting.Builder) ((BoolSetting.Builder) new BoolSetting.Builder())
                .name("on-ground"))
                .description("Toggle on-ground packets."))
                .defaultValue(true)
                .build());
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!mc.isInSingleplayer()) {

            assert mc.player != null;

            Entity vehicle = mc.player.getVehicle();
            if (vehicle instanceof BoatEntity) {
                for (int i = 0; i < this.amount.get(); ++i) {
                }
            } else if (vehicle instanceof MinecartEntity) {
                for (int i = 0; i < this.amount.get(); ++i) {
                }
            } else {
                this.error("You must be in a boat or a minecart. Disabling module.");
                this.toggle();
            }
        } else {
            this.error("You must be on a server. Disabling module.");
            this.toggle();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (this.autoDisable.get()) {
            this.toggle();
        }
    }
}
