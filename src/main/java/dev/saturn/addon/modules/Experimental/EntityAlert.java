package dev.saturn.addon.modules.Experimental;

import dev.saturn.addon.Saturn;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;

public class EntityAlert extends Module {

    private final SettingGroup sgGeneral;
    private final Setting<Set<EntityType<?>>> entities;

    public EntityAlert() {
        super(Saturn.Experimental, "entity-alert", "Alerts you when an entity is nearby.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("Entities")).description("Entities to look for")).build());
            }
        }