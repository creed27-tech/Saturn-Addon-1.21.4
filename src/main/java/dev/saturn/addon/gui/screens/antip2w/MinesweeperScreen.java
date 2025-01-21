package dev.saturn.addon.gui.screens.antip2w;

import dev.saturn.addon.modules.Funny.Minesweeper;
import dev.saturn.addon.utils.antip2w.TimerUtils;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.fabricmc.loader.impl.lib.sat4j.minisat.core.Counter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;

import static meteordevelopment.meteorclient.MeteorClient.mc;

// TODO make the code better
public class MinesweeperScreen extends GenericContainerScreen {

    // TODO : methodba rakni a menu updateeket
    private final HashSet<Byte> mines = new HashSet<>();
    private byte mineAmount;
    private static long bestTime = 0;
    private static long lastTime = 0;
    private static final Module module = Modules.get().get("Minesweeper");
    private static final EnumSetting<Minesweeper.Difficulty> difficultySetting = (EnumSetting<Minesweeper.Difficulty>) module.settings.get("Choose difficulty");
    private static final IntSetting mineSetting = (IntSetting) module.settings.get("mines");
    private boolean firstClick = true;
    private final TimerUtils timer = new TimerUtils();

    public MinesweeperScreen() {
        // new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, -69, new PlayerInventory(mc.player), new SimpleInventory(81), 9)
        super(GenericContainerScreenHandler.createGeneric9x6(-69, new PlayerInventory(mc.player)), new PlayerInventory(mc.player), ((MutableText) Text.of("Minesweeper")).formatted(Formatting.UNDERLINE, Formatting.BLACK));
        this.mineAmount = difficultySetting.get() == Minesweeper.Difficulty.CUSTOM ? mineSetting.get().byteValue() : difficultySetting.get().value.get();
    }

    private enum MenuSlot {
        RESET(Items.ORANGE_CONCRETE,0, "RESET", Formatting.GOLD),
        TIME(Items.CLOCK, 4,"BEST TIME", Formatting.YELLOW),
        MINE_AMOUNT(Items.TNT_MINECART, 3, "MINES", Formatting.DARK_RED),
        DIFFICULTY(Items.RECOVERY_COMPASS, 8,"DIFFICULTY", Formatting.DARK_RED);

        public final ItemStack itemStack;
        public final int slotId;
        public final MutableText name;

        MenuSlot(Item item, int slotId, String name, Formatting... formattings) {
            this.slotId = slotId+81;
            this.name = ((MutableText) Text.of(name)).formatted(formattings);
            itemStack = item.getDefaultStack();

        }
    }
}