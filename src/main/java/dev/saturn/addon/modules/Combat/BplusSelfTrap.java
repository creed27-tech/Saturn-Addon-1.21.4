package dev.saturn.addon.modules.Combat;

import dev.saturn.addon.utils.uwu.misc.Timer;
import dev.saturn.addon.modules.Combat.*;
import dev.saturn.addon.utils.bananaplus.BPlusWorldUtils;
import meteordevelopment.meteorclient.events.entity.player.FinishUsingItemEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BplusSelfTrap extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgToggle = settings.createGroup("Different Toggle Modes");

    private final Setting<TopMode> topPlacement = sgGeneral.add(new EnumSetting.Builder<TopMode>().name("Self Trap Mode").description("The mode at which SelfTrap+ operates in.").defaultValue(TopMode.Full).build());
    private final Setting<Primary> primary = sgGeneral.add(new EnumSetting.Builder<Primary>().name("Primary block").description("Primary block to use.").defaultValue(Primary.Obsidian).build());
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder().name("Delay").description("Delay in ticks between placing blocks.").defaultValue(0).sliderMin(0).sliderMax(10).build());
    private final Setting<Boolean> onlyGround = sgGeneral.add(new BoolSetting.Builder().name("Only On Ground").description("Will not attempt to place while you are not standing on ground.").defaultValue(false).build());
    private final Setting<Boolean> snap = sgGeneral.add(new BoolSetting.Builder().name("Center").description("Will align you at the center of your hole when you turn this on.").defaultValue(true).build());
    private final Setting<Integer> centerDelay = sgGeneral.add(new IntSetting.Builder().name("Center Delay").description("Delay in ticks before you get centered.").visible(snap::get).defaultValue(0).sliderMin(0).sliderMax(10).build());
    private final Setting<Boolean> placeOnCrystal = sgGeneral.add(new BoolSetting.Builder().name("Ignore entities").description("Will try to place even if there is an entity in its way.").defaultValue(true).build());
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder().name("Rotate").description("Whether to rotate or not.").defaultValue(false).build());
    private final Setting<Boolean> air = sgGeneral.add(new BoolSetting.Builder().name("Air-Place").description("Whether to place blocks midair or not.").defaultValue(true).build());
    private final Setting<Boolean> allBlocks = sgGeneral.add(new BoolSetting.Builder().name("Any-blastproof").description("Will allow any blast proof block to be used.").defaultValue(true).build());
    private final Setting<Boolean> disableOnYChange = sgToggle.add(new BoolSetting.Builder().name("disable-on-y-change").description("Automatically disables when your y level (step, jumping, atc).").defaultValue(false).build());
    private final Setting<Boolean> onEat = sgToggle.add(new BoolSetting.Builder().name("disable-on-chorus/pearl").description("Automatically disables when you eat chorus or throw a pearl (pearl dont work if u use middle click extra)").defaultValue(false).build());

    public static BplusSelfTrap INSTANCE;

    private BlockPos lastPos = new BlockPos(0, -100, 0);
    private int ticks = 0;

    private boolean hasCentered = false;
    private Timer onGroundCenter = new Timer();

    private static final Timer surroundInstanceDelay = new Timer();
    int timeToStart = 0;

    public BplusSelfTrap() {
        super(Categories.Combat, "banana-self-trap+", "Surrounds your top part in blocks to prevent you from taking lots of damage. | Ported from Banana+");
    }

    public static void setSurroundWait(int timeToStart) {
        INSTANCE.timeToStart = timeToStart;
    }

    boolean doSnap = true;

    public static void toggleCenter(boolean doSnap) {
        INSTANCE.doSnap = doSnap;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (onGroundCenter.passedTicks(centerDelay.get()) && snap.get() && doSnap && !hasCentered && mc.player.isOnGround()) {
            BPlusWorldUtils.snapPlayer(lastPos);
            hasCentered = true;
        }

        if (!hasCentered && !mc.player.isOnGround()) {
            onGroundCenter.reset();
            }
        }
    }

    enum TopMode {
        Full, Top, Side
    }

    enum Primary {
        Obsidian, EnderChest, CryingObsidian, NetheriteBlock, AncientDebris, RespawnAnchor, Anvil
    }