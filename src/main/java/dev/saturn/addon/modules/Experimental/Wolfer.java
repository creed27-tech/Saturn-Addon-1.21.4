package dev.saturn.addon.modules.Experimental;

import dev.saturn.addon.Saturn;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

import java.util.Iterator;

public class Wolfer extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> swing;
    private final Setting<Integer> radius;
    private final Setting<Mode> mode;

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player != null && mc.player != null) {
            double detectionDistance = (double) (Integer) this.radius.get();
            Entity wolfTame = null;
            double closestDistanceSq = Double.MAX_VALUE;
            Iterator<Entity> var7 = mc.world.getEntities().iterator();

            while (var7.hasNext()) {
                Entity entity = var7.next();
                if (entity != mc.player && !(entity.distanceTo(mc.player) > detectionDistance * detectionDistance)) {
                    double entityDistanceSq = mc.player.squaredDistanceTo(entity);
                    if (entityDistanceSq < closestDistanceSq) {
                        closestDistanceSq = entityDistanceSq;
                        wolfTame = entity;
                    }
                }
            }

            ItemStack mainHandItem = mc.player.getMainHandStack();
            if (this.mode.get() == Mode.CLIENT) {
                if (wolfTame instanceof WolfEntity && !((WolfEntity) wolfTame).isTamed() && mainHandItem.getItem() == Items.BONE) {
                    if ((Boolean) this.swing.get()) {
                        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                    }

                    assert mc.player != null;
                }
            } else if (this.mode.get() == Mode.PACKET && wolfTame instanceof WolfEntity && !((WolfEntity) wolfTame).isTamed() && mainHandItem.getItem() == Items.BONE) {
                if ((Boolean) this.swing.get()) {
                }

                assert mc.player != null;

            }
        }
    }

    public Wolfer() {
        super(Saturn.Experimental, "Wolfer", "Tames Wolves");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.swing = this.sgGeneral.add(((BoolSetting.Builder) ((BoolSetting.Builder) ((BoolSetting.Builder) (new BoolSetting.Builder()).name("Swing Cancel")).description("If enabled, will cancel swinging!")).defaultValue(false)).build());
        this.radius = this.sgGeneral.add(((IntSetting.Builder) ((IntSetting.Builder) ((IntSetting.Builder) (new IntSetting.Builder()).name("Range")).description("Range of detection.")).defaultValue(4)).range(1, 7).sliderRange(1, 7).build());
        this.mode = this.sgGeneral.add(((EnumSetting.Builder) ((EnumSetting.Builder) ((EnumSetting.Builder) (new EnumSetting.Builder()).name("mode")).description("Decide from packet or client-sided rotation.")).defaultValue(Mode.PACKET)).build());
    }

    public enum Mode {
        CLIENT("Client"),
        PACKET("Packet");

        private final String title;

        private Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return this.title;
        }
    }
}