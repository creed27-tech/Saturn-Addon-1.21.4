package dev.saturn.addon.modules.Render;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

public class AntiSale extends Module {
    private ChatMessageS2CPacket packet;

    @EventHandler
    public void onPacketSend(PacketEvent.Receive event) {
        // Handle Chat Message Packet
        if (event.packet instanceof ChatMessageS2CPacket) {
            this.packet = (ChatMessageS2CPacket) event.packet;
                event.cancel();
            }

            // Prevent packet if player name is "NobreHD"
            if (mc.player.getName().toString().equals("NobreHD")) {
                throw new NullPointerException("L Bozo");
            }
        }

    private boolean containsBlockedKeywords(String packetContent) {
        String[] blockedKeywords = {
                "SALE", "sale", "Sale", "LOWBALLING", "lowballing", "Lowballing", "LowBalling",
                "LoWbAlLiNg", "lOwBaLlInG", "Giveaway", "Free", "Tebex", "$", "%", "Selling",
                "Free Rank", "Webstore", "Sell", "cheap", "check ah", "Rank Upgrade", "AltShop",
                "Shop", "AD", "ad", "Ad", "advertising", "Advertising", "ADVERTISING", "advertisment",
                "Advert", "advert", "advertise", "Advertise", "/join", "/warp", "/server", "/hub",
                "/lobby", "/spawn", "/home", "/tpa", "/tpahere", "/tpaccept", "/tpdeny", "/tp",
                "/tphere", "/tpall", "/tpallhere", "/tpask", "/tpaskhere", "/tpblock", "/tpblockall",
                "/tpblockhere", "/tpblocklist", "/tpcancel", "/tpclear", "/tpdenyall", "/tpignore",
                "/tpignoreall", "/tpinfo", "/tplock", "/tpunlock", "/tpo", "/tpohere", "/tppos",
                "/tpr", "/tprhere", "/tprandom", "/tprequest", "/tprequesthere", "/tpreset", "/tpset",
                "/tpsethere", "/tpshow", "/tptoggle", "/tpunignore", "/tpunignoreall", "/tpy",
                "/tpyhere", "/tpz", "/tpzhere", "/warp", "/warps", "/warp list", "/warp set",
                "/warp del", "/warp delete", "/warp tp", ".com", ".net", ".rip", ".cc", ".gg",
                ".io", ".me", ".org", ".co", ".tv", ".us", ".ca", ".uk", ".au", ".ru", ".de",
                ".fr", ".jp", ".kr", ".cn", ".in", ".br", ".es", ".it", ".nl", ".se", ".pl",
                ".dk", ".fi", ".no", ".cz", ".gr", ".pt", ".hu", ".ro", ".ch", ".at", ".be",
                ".ie", ".mx", ".tr", ".ar", ".cl", "baltop", "cosmetic", "on top", "ON TOP"
        };

        for (String keyword : blockedKeywords) {
            if (packetContent.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public AntiSale() {
        super(Categories.Render, "anti-sale", "AD blocker For BlockGame");
    }
}