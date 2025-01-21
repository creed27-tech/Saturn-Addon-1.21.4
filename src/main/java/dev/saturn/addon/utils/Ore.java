package dev.saturn.addon.utils;

import dev.saturn.addon.mixin.CountPlacementModifierAccessor;
import dev.saturn.addon.mixin.HeightRangePlacementModifierAccessor;
import dev.saturn.addon.mixin.RarityFilterPlacementModifierAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;

import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;

import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.*;


public class Ore {

    private static final Setting<Boolean> coal        = new BoolSetting.Builder().name("Coal").build();
    private static final Setting<Boolean> iron        = new BoolSetting.Builder().name("Iron").build();
    private static final Setting<Boolean> gold        = new BoolSetting.Builder().name("Gold").build();
    private static final Setting<Boolean> redstone    = new BoolSetting.Builder().name("Redstone").build();
    private static final Setting<Boolean> diamond     = new BoolSetting.Builder().name("Diamond").build();
    private static final Setting<Boolean> lapis       = new BoolSetting.Builder().name("Lapis").build();
    private static final Setting<Boolean> copper      = new BoolSetting.Builder().name("Kappa").build();
    private static final Setting<Boolean> emerald     = new BoolSetting.Builder().name("Emerald").build();
    private static final Setting<Boolean> quartz      = new BoolSetting.Builder().name("Quartz").build();
    private static final Setting<Boolean> debris      = new BoolSetting.Builder().name("Ancient Debris").build();
    public static final  List<Setting<Boolean>>   oreSettings = new ArrayList<>(Arrays.asList(coal, iron, gold, redstone, diamond, lapis, copper, emerald, quartz, debris));

    public static Map<RegistryKey<Biome>, List<Ore>> getRegistry(Dimension dimension) {
        return Map.of();
    }

    public int step;
    public int index;
    public Setting<Boolean> active;
    public IntProvider count = ConstantIntProvider.create(1);
    public HeightProvider heightProvider;
    public HeightContext heightContext;
    public float rarity = 1;
    public float discardOnAirChance;
    public int size;
    public Color color;
    public boolean scattered;

    private Ore(PlacedFeature feature, int step, int index, Setting<Boolean> active, Color color) {
        this.step = step;
        this.index = index;
        this.active = active;
        this.color = color;
        int bottom = MinecraftClient.getInstance().world.getBottomY();
        int height = MinecraftClient.getInstance().world.getDimension().logicalHeight();
        this.heightContext = new HeightContext(null, HeightLimitView.create(bottom, height));

        for (PlacementModifier modifier : feature.placementModifiers()) {
            if (modifier instanceof CountPlacementModifier) {
                this.count = ((CountPlacementModifierAccessor) modifier).getCount();

            } else if (modifier instanceof HeightRangePlacementModifier) {
                this.heightProvider = ((HeightRangePlacementModifierAccessor) modifier).getHeight();

            } else if (modifier instanceof RarityFilterPlacementModifier) {
                this.rarity = ((RarityFilterPlacementModifierAccessor) modifier).getChance();
            }
        }

        FeatureConfig featureConfig = feature.feature().value().config();

        if (featureConfig instanceof OreFeatureConfig oreFeatureConfig) {
            this.discardOnAirChance = oreFeatureConfig.discardOnAirChance;
            this.size = oreFeatureConfig.size;
        } else {
            throw new IllegalStateException("config for " + feature + "is not OreFeatureConfig.class");
        }

        if (feature.feature().value().feature() instanceof ScatteredOreFeature) {
            this.scattered = true;
        }
    }
}