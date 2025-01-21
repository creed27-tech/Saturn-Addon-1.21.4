package dev.saturn.addon;

import dev.saturn.addon.enums.lemon.RotationType;
import dev.saturn.addon.enums.lemon.SwingHand;
import dev.saturn.addon.enums.lemon.SwingState;
import dev.saturn.addon.enums.lemon.SwingType;
import dev.saturn.addon.managers.lemon.Managers;
import dev.saturn.addon.modules.Misc.LCSwingAnimation;
import dev.saturn.addon.utils.lemon.SettingUtils;
import dev.saturn.addon.utils.lemon.timers.TimerUtils;
import dev.saturn.addon.utils.lemon.world.BlockInfo;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class ov4Module extends Module {
    private final String prefix = Formatting.GRAY + "[" + Formatting.RED + "ov4client" + Formatting.GRAY + "]";
    public int priority;

    public ov4Module(Category category, String name, String description) {
        super(category, name, description);
    }

    //  Messages
    public void sendToggledMsg() {
        if (Config.get().chatFeedback.get() && chatFeedback && mc.world != null) {
            ChatUtils.forceNextPrefixClass(getClass());
            String msg = prefix + " " + Formatting.GRAY + "[" + Formatting.LIGHT_PURPLE + name + Formatting.GRAY + "]" + Formatting.WHITE + " toggled" + (isActive() ? Formatting.GREEN + " ON" : Formatting.RED + " OFF");
            sendMessage(Text.of(msg), hashCode());
        }
    }

    public void sendToggledMsg(String message) {
        if (Config.get().chatFeedback.get() && chatFeedback && mc.world != null) {
            ChatUtils.forceNextPrefixClass(getClass());
            String msg = prefix + " " + Formatting.GRAY + "[" + Formatting.LIGHT_PURPLE + name + Formatting.GRAY + "]" + Formatting.WHITE + " toggled" + (isActive() ? Formatting.GREEN + " ON " : Formatting.RED + " OFF ") + Formatting.GRAY + message;
            sendMessage(Text.of(msg), hashCode());
        }
    }

    public void sendDisableMsg(String text) {
        if (mc.world != null) {
            ChatUtils.forceNextPrefixClass(getClass());
            String msg = prefix + " " + Formatting.GRAY + "[" + Formatting.LIGHT_PURPLE + name + Formatting.GRAY + "]" + Formatting.WHITE + " toggled" + Formatting.RED + " OFF " + Formatting.GRAY + text;
            sendMessage(Text.of(msg), hashCode());
        }
    }

    public void sendNotificationsInfo(String text) {
        if (mc.world != null) {
            ChatUtils.forceNextPrefixClass(getClass());
            String msg = prefix + " " + Formatting.GRAY + "[" + Formatting.LIGHT_PURPLE + name + Formatting.GRAY + "]" + " " + text;
            sendMessage(Text.of(msg), Objects.hash(name + "-info"));
        }
    }

    public void debug(String text) {
        if (mc.world != null) {
            ChatUtils.forceNextPrefixClass(getClass());
            String msg = prefix + " " + Formatting.GRAY + "[" + Formatting.LIGHT_PURPLE + name + Formatting.GRAY + "]" + " " + Formatting.AQUA + text;
            sendMessage(Text.of(msg), 0);
        }
    }

    public void sendMessage(Text text, int id) {
        ((IChatHud) mc.inGameHud.getChatHud()).meteor$add(text, id);
    }

    //  Packets
    public void sendPacket(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) return;
        mc.getNetworkHandler().sendPacket(packet);
    }

    public void attackNearbyCrystal(BlockPos pos, boolean eatingPause, boolean swing, SwingHand hand, long breakDelay) {
        TimerUtils timer = new TimerUtils();

        if (!timer.passedMs(breakDelay)) {
            return;
        }
        if (eatingPause && mc.player.isUsingItem()) {
            return;
        }
        for (Entity entity : mc.world.getOtherEntities(null, new Box(pos), entity -> entity == mc.player)) {
            if (!(entity instanceof EndCrystalEntity)) continue;
            timer.reset();
            sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));
            if (swing) clientSwing(hand, Hand.MAIN_HAND);
            if (!SettingUtils.shouldAttackTrace(RotationType.Attacking)) break;
            Managers.ROTATION.start(entity.getBoundingBox(), priority, RotationType.Attacking, Objects.hash(name + "attacking"));
            break;
        }
    }

    public void clientSwing(SwingHand swingHand, Hand realHand) {
        Hand hand = switch (swingHand) {
            case MainHand -> Hand.MAIN_HAND;
            case OffHand -> Hand.OFF_HAND;
            case RealHand -> realHand;
        };

        mc.player.swingHand(hand, true);
        Modules.get().get(LCSwingAnimation.class).startSwing(hand);
    }
}