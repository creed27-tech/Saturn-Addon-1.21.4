package dev.saturn.addon.modules.Movement;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class FakeExplosion extends Module {

    public FakeExplosion() {
        super(Categories.Movement, "fake-explosion", "Fakes a Small Explosion that creates Client Ghost Block Fire");
        }
    }