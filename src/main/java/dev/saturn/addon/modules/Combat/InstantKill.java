
package dev.saturn.addon.modules.Combat;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class InstantKill extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<Boolean> auto = sgGeneral.add(new BoolSetting.Builder()
            .name("AutoDraw")
            .description("Automatically draws your bow.")
            .defaultValue(false)
            .build()
    );

    public static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean shouldAddVelocity = true;
    public static boolean shouldAddVelocity1 = false;
    public static boolean shouldAddVelocity2 = false;
    public static boolean shouldAddVelocity3 = false;
    public static boolean shouldAddVelocity0 = false;

    public InstantKill() {
        super(Categories.Combat, "insta-kill", "Enable/Disable instakill");
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (auto.get() && mc.player.getMainHandStack().getItem() == Items.BOW){
            if (!mc.player.isUsingItem()) {
                mc.options.useKey.setPressed(true);
            }
        }
    }
    @Override
    public void onDeactivate() {
        mc.options.useKey.setPressed(false);
    }

    public static void addVelocityToPlayer(){
        if(shouldAddVelocity){
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            for (int i = 0; i < 100; i++) {
                sendmovementpackets();
            }
        }
        if(shouldAddVelocity1){
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            for (int i = 0; i < 150; i++) {
                sendmovementpackets();
            }
        }
        if(shouldAddVelocity2){
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            for (int i = 0; i < 200; i++) {
                sendmovementpackets();
            }
        }
        if(shouldAddVelocity3){
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            for (int i = 0; i < 300; i++) {
                sendmovementpackets();
            }
        }
        if(shouldAddVelocity0){
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            for (int i = 0; i < 50; i++) {
                sendmovementpackets();
            }
        }
    }
    private static void sendmovementpackets(){
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                mc.player.getX(), mc.player.getY() - 0.000000001, mc.player.getZ(), true, false));

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                mc.player.getX(), mc.player.getY() + 0.000000001, mc.player.getZ(), false, false));
    }
}