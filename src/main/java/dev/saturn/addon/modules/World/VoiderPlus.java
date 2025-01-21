package dev.saturn.addon.modules.World;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class VoiderPlus extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<String> block;
    private final Setting<Integer> radius;
    public final Setting<Boolean> getplayerY;
    private final Setting<Integer> playerheight;
    private final Setting<Integer> maxheight;
    private final Setting<Integer> minheight;
    public final Setting<Boolean> threebythree;
    public final Setting<Boolean> tpfwd;
    public final Setting<Boolean> tgl;
    int i;
    private int passes;
    private int TPs;
    private int pX;
    private int pZ;
    private int sX;
    private int sY;
    private int sZ;

    public VoiderPlus() {
        super(Categories.World, "voider-plus", "Runs /fill on the world from the top down (Must have OP)");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.block = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("Block to be used for /fill")).description("What is created.")).defaultValue("air")).build());
        this.radius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("radius")).description("radius")).defaultValue(45)).sliderRange(1, 90).build());
        this.getplayerY = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("UsePlayerY")).description("Use the player's Y level for calculating where voider will start.")).defaultValue(true)).build());
        this.playerheight = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("maxheight(fromplayerY)")).description("maxheight")).defaultValue(0)).sliderRange(-64, 64).visible(() -> {
            return (Boolean)this.getplayerY.get();
        })).build());
        this.maxheight = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("maxheight")).description("maxheight")).defaultValue(128)).sliderRange(64, 319).visible(() -> {
            return !(Boolean)this.getplayerY.get();
        })).build());
        this.minheight = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("minheight")).description("minheight")).defaultValue(-64)).sliderRange(-64, 128).build());
        this.threebythree = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("VoiderBot3x3")).description("Runs voider nine times in a 3x3 grid pattern to replace a whole lot more")).defaultValue(false)).build());
        this.tpfwd = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("TP forward")).description("Teleports you double your radius forward after voiding to aid in voiding a perfect strip.")).defaultValue(false)).build());
        this.tgl = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Toggle off after TP forward")).description("Turn module off after TP, or not.")).defaultValue(false)).visible(() -> {
            return (Boolean)this.tpfwd.get();
        })).build());
        this.passes = 0;
        this.TPs = 0;
        }
    }