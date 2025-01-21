package dev.saturn.addon.modules.Render;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import dev.saturn.addon.utils.reaper.misc.ReaperModule;
import dev.saturn.addon.utils.reaper.player.Interactions;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.NameProtect;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import java.util.*;

public class ReaperNametags extends ReaperModule {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgPlayers = settings.createGroup("Players");
    private final SettingGroup sgItems = settings.createGroup("Items");

    // General

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder().name("scale").description("The scale of the nametag.").defaultValue(1.5).min(0.1).build());
    private final Setting<Boolean> yourself = sgGeneral.add(new BoolSetting.Builder().name("self").description("Displays a nametag on your player if you're in Freecam.").defaultValue(true).build());
    private final Setting<SettingColor> background = sgGeneral.add(new ColorSetting.Builder().name("background-color").description("The color of the nametag background.").defaultValue(new SettingColor(0, 0, 0, 75)).build());
    private final Setting<SettingColor> names = sgGeneral.add(new ColorSetting.Builder().name("primary-color").description("The color of the nametag names.").defaultValue(new SettingColor()).build());
    private final Setting<Boolean> culling = sgGeneral.add(new BoolSetting.Builder().name("culling").description("Only render a certain number of nametags at a certain distance.").defaultValue(false).build());
    private final Setting<Double> maxCullRange = sgGeneral.add(new DoubleSetting.Builder().name("culling-range").description("Only render nametags within this distance of your player.").defaultValue(20).min(0).sliderMax(200).visible(culling::get).build());
    private final Setting<Integer> maxCullCount = sgGeneral.add(new IntSetting.Builder().name("culling-count").description("Only render this many nametags.").defaultValue(50).min(1).sliderRange(1, 100).visible(culling::get).build());

    //Players
    private final Setting<Boolean> displayItems = sgPlayers.add(new BoolSetting.Builder().name("display-items").description("Displays armor and hand items above the name tags.").defaultValue(true).build());
    private final Setting<Double> itemSpacing = sgPlayers.add(new DoubleSetting.Builder().name("item-spacing").description("The spacing between items.").defaultValue(2).range(0, 10).visible(displayItems::get).build());
    private final Setting<Boolean> ignoreEmpty = sgPlayers.add(new BoolSetting.Builder().name("ignore-empty-slots").description("Doesn't add spacing where an empty item stack would be.").defaultValue(true).visible(displayItems::get).build());
    private final Setting<Boolean> displayItemEnchants = sgPlayers.add(new BoolSetting.Builder().name("display-enchants").description("Displays item enchantments on the items.").defaultValue(true).visible(displayItems::get).build());
    private final Setting<meteordevelopment.meteorclient.systems.modules.render.Nametags.Position> enchantPos = sgPlayers.add(new EnumSetting.Builder<meteordevelopment.meteorclient.systems.modules.render.Nametags.Position>().name("enchantment-position").description("Where the enchantments are rendered.").defaultValue(meteordevelopment.meteorclient.systems.modules.render.Nametags.Position.Above).visible(displayItemEnchants::get).build());
    private final Setting<Integer> enchantLength = sgPlayers.add(new IntSetting.Builder().name("enchant-name-length").description("The length enchantment names are trimmed to.").defaultValue(3).range(1, 5).sliderRange(1, 5).visible(displayItemEnchants::get).build());
    private final Setting<Double> enchantTextScale = sgPlayers.add(new DoubleSetting.Builder().name("enchant-text-scale").description("The scale of the enchantment text.").defaultValue(1).range(0.1, 2).sliderRange(0.1, 2).visible(displayItemEnchants::get).build());
    private final Setting<Boolean> displayGameMode = sgPlayers.add(new BoolSetting.Builder().name("gamemode").description("Shows the player's GameMode.").defaultValue(true).build());
    private final Setting<Boolean> displayPing = sgPlayers.add(new BoolSetting.Builder().name("ping").description("Shows the player's ping.").defaultValue(true).build());
    private final Setting<Boolean> displayDistance = sgPlayers.add(new BoolSetting.Builder().name("distance").description("Shows the distance between you and the player.").defaultValue(true).build());

    //Items
    private final Setting<Boolean> itemCount = sgItems.add(new BoolSetting.Builder().name("show-count").description("Displays the number of items in the stack.").defaultValue(true).build());

    private final Color WHITE = new Color(255, 255, 255);
    private final Color RED = new Color(255, 25, 25);
    private final Color AMBER = new Color(255, 105, 25);
    private final Color GREEN = new Color(25, 252, 25);
    private final Color GOLD = new Color(232, 185, 35);
    private final Color GREY = new Color(150, 150, 150);
    private final Color BLUE = new Color(20, 170, 170);

    private final double[] itemWidths = new double[6];
    public final Object2IntMap<UUID> totemPops = new Object2IntOpenHashMap<>();

    private final Map<Enchantment, Integer> enchantmentsToShowScale = new HashMap<>();
    private final List<Entity> entityList = new ArrayList<>();

    public ReaperNametags() {
        super(Categories.Render, "reaper-nametags", "Displays customizable nametags above players. | Ported from Reaper");
    }

    private static String ticksToTime(int ticks) {
        if (ticks > 20 * 3600) {
            int h = ticks / 20 / 3600;
            return h + " h";
        } else if (ticks > 20 * 60) {
            int m = ticks / 20 / 60;
            return m + " m";
        } else {
            int s = ticks / 20;
            int ms = (ticks % 20) / 2;
            return s + "." + ms + " s";
        }
    }

    private int getRenderCount() {
        int count = culling.get() ? maxCullCount.get() : entityList.size();
        count = MathHelper.clamp(count, 0, entityList.size());

        return count;
    }

    @Override
    public String getInfoString() {
        return Integer.toString(getRenderCount());
    }

    private double getHeight(Entity entity) {
        double height = entity.getEyeHeight(entity.getPose());

        if (entity.getType() == EntityType.ITEM || entity.getType() == EntityType.ITEM_FRAME) height += 0.2;
        else height += 0.5;

        return height;
    }

    private void renderNametagPlayer(PlayerEntity player) {
        TextRenderer text = TextRenderer.get();

        // Gamemode
        GameMode gm = EntityUtils.getGameMode(player);
        String gmText = "BOT";
        if (gm != null) {
            gmText = switch (gm) {
                case SPECTATOR -> "Sp";
                case SURVIVAL -> "S";
                case CREATIVE -> "C";
                case ADVENTURE -> "A";
            };
        }

        gmText = "[" + gmText + "] ";
        String devText = " [DEV]";
        String betaText = " [BETA]";


        // Name
        String name;
        Color nameColor = PlayerUtils.getPlayerColor(player, names.get());

        // Health
        float absorption = player.getAbsorptionAmount();
        int health = Math.round(player.getHealth() + absorption);
        double healthPercentage = health / (player.getMaxHealth() + absorption);

        String healthText = String.valueOf(health);
        Color healthColor;

        if (healthPercentage <= 0.333) healthColor = RED;
        else if (healthPercentage <= 0.666) healthColor = AMBER;
        else healthColor = GREEN;

        // Ping
        int ping = EntityUtils.getPing(player);
        String pingText = " [" + ping + "ms]";

        // Distance
        double dist = Math.round(PlayerUtils.distanceToCamera(player) * 10.0) / 10.0;
        String distText = " " + dist + "m";

        // Calc widths
        double gmWidth = text.getWidth(gmText, true);
        double devWidth = text.getWidth(devText, true);
        double betawith = text.getWidth(betaText, true);
        double healthWidth = text.getWidth(healthText, true);
        double pingWidth = text.getWidth(pingText, true);
        double distWidth = text.getWidth(distText, true);

        // Render texts
        text.beginBig();

        if (displayItems.get()) {
            // Item calc
            Arrays.fill(itemWidths, 0);
            boolean hasItems = false;
            int maxEnchantCount = 0;
        }
    }

    private void renderNametagItem(ItemStack stack) {
        TextRenderer text = TextRenderer.get();

        String name = stack.getName().getString();
        String count = " x" + stack.getCount();

        double nameWidth = text.getWidth(name, true);
        double countWidth = text.getWidth(count, true);
        double heightDown = text.getHeight(true);

        double width = nameWidth;
        if (itemCount.get()) width += countWidth;
        double widthHalf = width / 2;

        drawBg(-widthHalf, -heightDown, width, heightDown);

        text.beginBig();
        double hX = -widthHalf;
        double hY = -heightDown;

        hX = text.render(name, hX, hY, names.get(), true);
        if (itemCount.get()) text.render(count, hX, hY, GOLD, true);
        text.end();

        NametagUtils.end();
    }

    private void renderGenericNametag(LivingEntity entity) {
        TextRenderer text = TextRenderer.get();

        //Name
        String nameText = entity.getType().getName().getString();
        nameText += " ";

        //Health
        float absorption = entity.getAbsorptionAmount();
        int health = Math.round(entity.getHealth() + absorption);
        double healthPercentage = health / (entity.getMaxHealth() + absorption);

        String healthText = String.valueOf(health);
        Color healthColor;

        if (healthPercentage <= 0.333) healthColor = RED;
        else if (healthPercentage <= 0.666) healthColor = AMBER;
        else healthColor = GREEN;

        double nameWidth = text.getWidth(nameText, true);
        double healthWidth = text.getWidth(healthText, true);
        double heightDown = text.getHeight(true);

        double width = nameWidth + healthWidth;
        double widthHalf = width / 2;

        drawBg(-widthHalf, -heightDown, width, heightDown);

        text.beginBig();
        double hX = -widthHalf;
        double hY = -heightDown;

        hX = text.render(nameText, hX, hY, names.get(), true);
        text.render(healthText, hX, hY, healthColor, true);
        text.end();

        NametagUtils.end();
    }

    private void renderTntNametag(TntEntity entity) {
        TextRenderer text = TextRenderer.get();

        String fuseText = ticksToTime(entity.getFuse());

        double width = text.getWidth(fuseText, true);
        double heightDown = text.getHeight(true);

        double widthHalf = width / 2;

        drawBg(-widthHalf, -heightDown, width, heightDown);

        text.beginBig();
        double hX = -widthHalf;
        double hY = -heightDown;

        text.render(fuseText, hX, hY, names.get(), true);
        text.end();

        NametagUtils.end();
    }

    private ItemStack getItem(PlayerEntity entity, int index) {
        return switch (index) {
            case 0 -> entity.getMainHandStack();
            case 1 -> entity.getInventory().armor.get(3);
            case 2 -> entity.getInventory().armor.get(2);
            case 3 -> entity.getInventory().armor.get(1);
            case 4 -> entity.getInventory().armor.get(0);
            case 5 -> entity.getOffHandStack();
            default -> ItemStack.EMPTY;
        };
    }

    private void drawBg(double x, double y, double width, double height) {
        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.quad(x - 1, y - 1, width + 2, height + 2, background.get());
        Renderer2D.COLOR.render(null);
    }

    public enum Position {
        Above,
        OnTop
    }
}