package dev.saturn.addon.modules.Automation;

import dev.saturn.addon.Saturn;
import dev.saturn.addon.utils.NF.NFUtils;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BaritoneMiner extends Module {

    /*
    todo:future features list
        1. option for it to stop at certain goal position
        2. option to disconnect on running out of pickaxes
        3. swarm websocket integration(some day)
    */

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgShape = settings.createGroup("Shape");

    private final Setting<BlockPos> cornerOne = sgShape.add(new BlockPosSetting.Builder()
            .name("corner-1")
            .description("Position of 1st corner.")
            .defaultValue(new BlockPos(10, 120, 10))
            .build()
    );

    private final Setting<BlockPos> cornerTwo = sgShape.add(new BlockPosSetting.Builder()
            .name("corner-2")
            .description("Position of 2nd corner.")
            .defaultValue(new BlockPos(-10, 120, -10))
            .build()
    );

    private final Setting<Integer> nukerOffset = sgGeneral.add(new IntSetting.Builder()
            .name("nuker-offset")
            .description("Distance for the bot to offset after reaching the end of a line.")
            .defaultValue(8)
            .range(0, 15)
            .sliderRange(1, 15)
            .build()
    );

    private final Setting<Boolean> pathStart = sgGeneral.add(new BoolSetting.Builder()
            .name("path-to-start")
            .description("Force baritone to path to a corner before starting.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> renderCorners = sgGeneral.add(new BoolSetting.Builder()
            .name("render-corners")
            .description("Renders the 2 corners.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> disableOnDisconnect = sgGeneral.add(new BoolSetting.Builder()
            .name("disable-on-disconnect")
            .description("Disables when you disconnect.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> getPickaxe = sgGeneral.add(new BoolSetting.Builder()
            .name("refill-pickaxes-from-shulker")
            .description("Refills pickaxes from shulkers when you run out.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Keybind> pauseBind = sgGeneral.add(new KeybindSetting.Builder()
            .name("pause")
            .description("Pauses baritone.")
            .defaultValue(Keybind.none())
            .build()
    );

    public BaritoneMiner() {
        super(Saturn.Automation, "baritone-miner", "WIP | Paths automatically.");
    }

    private BlockPos endOfLinePos, barPos, offsetPos, currPlayerPos, shulkerPlacePos, savedPos = null;
    private Direction toEndOfLineDir, toAdvanceDir, shulkerPlaceDir = null;
    private boolean offsetting, bindPressed, isPaused, refilling, placedShulker, defined = false;
    private int length, initialNetherrack, initialPicksBroken = 0;
    }