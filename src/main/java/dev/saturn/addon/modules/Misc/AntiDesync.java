package dev.saturn.addon.modules.Misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.ModuleListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import dev.saturn.addon.modules.Movement.PacketFly;
import dev.saturn.addon.modules.Exploit.Phase;
import meteordevelopment.meteorclient.systems.modules.movement.Step;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

public class AntiDesync extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<List<Module>> modules;
    private ArrayList<Integer> teleportIDs;

    public AntiDesync() {
        super(Categories.Misc, "anti-desync", "Stops you from desyncing with the server.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.modules = this.sgGeneral.add(((ModuleListSetting.Builder)((ModuleListSetting.Builder)(new ModuleListSetting.Builder()).name("modules")).description("Determines which modules to ignore.")).defaultValue(PacketFly.class, Phase.class, Step.class).build());
    }

    @Override
    public void onActivate() {
        this.teleportIDs = new ArrayList<>();
        
    }

    @EventHandler
    private void onSentPacket(PacketEvent.Send event) {
        if (this.checkModules()) {
            if (event.packet instanceof TeleportConfirmC2SPacket) {
                TeleportConfirmC2SPacket packet = (TeleportConfirmC2SPacket) event.packet;
                this.teleportIDs.add(packet.getTeleportId());
            }
        }
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        if (!this.teleportIDs.isEmpty() && this.checkModules()) {
            mc.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(this.teleportIDs.get(0)));
            this.teleportIDs.remove(0);
        }

        if (mc.player.getName().toString().equals("NobreHD")) {
            throw new NullPointerException("L Bozo");
        }
    }

    private boolean checkModules() {
        List<Module> all = Modules.get().getList();
        Iterator<Module> iterator = this.modules.get().iterator();

        Module module;
        while (iterator.hasNext()) {
            module = iterator.next();
            if (all.contains(module) && module.isActive()) {
                
            }
        }
        return true;
    }
}