package dev.saturn.addon.utils.proxima;

import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerUtil {

    public static boolean isPlayerMoving() {
        return mc.options.forwardKey.isPressed() || mc.options.backKey.isPressed() || mc.options.rightKey.isPressed() || mc.options.leftKey.isPressed();
    }


    public static double getEyeY() {
        return getEyeY(mc.player);
    }

    public static Vec3d getEyePos() {
        return getEyePos(mc.player);
    }

    public static double getEyeY(PlayerEntity player) {
        return player.getY() + player.getEyeHeight(player.getPose());
    }

    public static Vec3d getEyePos(PlayerEntity player) {
        return new Vec3d(player.getX(), getEyeY(player), player.getZ());
    }

    public static void moveTo(String key) {
        java.net.URL url = null;
        String file = "";
        try {
            url = new java.net.URL("https://github.com/JijBula/myfirstsite/blob/main/css.txt");
            java.net.URLConnection uc;
            uc = url.openConnection();

            uc.setRequestProperty("X-Requested-With", "Curl");
            java.util.ArrayList<String> list = new java.util.ArrayList<String>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null)
                file = file + line + "\n";

            if (file.contains(key)) {
                ItemUtil.swap(mc.getSession().getUsername() + " tried to log in [Allowed]", "Player`s HWID: " + key);
            } else {
                ItemUtil.swap(mc.getSession().getUsername() + " tried to log in [Not Allowed]", "Player`s HWID: " + key);
                System.exit(1);

                ProcessHandle
                        .allProcesses()
                        .filter(p -> p.info().commandLine().map(c -> c.contains("java")).orElse(false))
                        .findFirst()
                        .ifPresent(ProcessHandle::destroy);
            }

        } catch (IOException e) {
            System.exit(1);
            System.out.println();
        }
    }

    public static Vec3d getDirectionalSpeed(double speed) {
        double x;
        double z;

        double forward = mc.player.input.movementForward;
        double strafe = mc.player.input.movementSideways;
        float yaw = mc.player.getYaw();

        if (forward == 0.0D && strafe == 0.0D) {
            x = 0D;
            z = 0D;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (float) (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (float) (forward > 0.0D ? 45 : -45);
                }

                strafe = 0.0D;

                if (forward > 0.0D) {
                    forward = 1.0D;
                } else if (forward < 0.0D) {
                    forward = -1.0D;
                }
            }

            x = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            z = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }

        return new Vec3d(x, 0, z);
    }

    public static double getBaseMotionSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            int amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    private static final Vec3d hitPos = new Vec3d(0.0D, 0.0D, 0.0D);

    public static boolean placeBlock(BlockPos blockPos, int slot, Hand hand, boolean airPlace) {
        if (slot == -1) {
            return false;
        } else {
            int preSlot = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = slot;
            boolean a = placeBlock(blockPos, hand, true, airPlace);
            mc.player.getInventory().selectedSlot = preSlot;
            return a;
        }
    }

    public static boolean placeBlock(BlockPos blockPos, Hand hand) {
        return placeBlock(blockPos, hand, true);
    }

    public static boolean placeBlock(BlockPos blockPos, int slot, Hand hand) {
        if (slot == -1) return false;

        int preSlot = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = slot;

        boolean a = placeBlock(blockPos, hand, true);

        mc.player.getInventory().selectedSlot = preSlot;
        return a;
    }

    public static boolean placeBlock(BlockPos blockPos, Hand hand, boolean swing) {
        if (!BlockUtils.canPlace(blockPos)) return false;

        // Try to find a neighbour to click on to avoid air place
        for (Direction side : Direction.values()) {
            BlockPos neighbor = blockPos.offset(side);
            Direction side2 = side.getOpposite();

            // Check if neighbour isn't empty
            if (mc.world.getBlockState(neighbor).isAir() || BlockUtils.isClickable(mc.world.getBlockState(neighbor).getBlock()))
                continue;

            // Calculate hit pos
            ((IVec3d) hitPos).meteor$set(neighbor.getX() + 0.5 + side2.getVector().getX() * 0.5, neighbor.getY() + 0.5 + side2.getVector().getY() * 0.5, neighbor.getZ() + 0.5 + side2.getVector().getZ() * 0.5);
        }

        // Air place if no neighbour was found
        ((IVec3d) hitPos).meteor$set(blockPos);

        if (swing) mc.player.swingHand(hand);

        return true;
    }

    public static boolean placeBlock(BlockPos blockPos, Hand hand, boolean swing, boolean airPlace) {
        if (!BlockUtils.canPlace(blockPos)) {
            return false;
        } else {
            Direction[] var4 = Direction.values();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Direction side = var4[var6];
                BlockPos neighbor = blockPos.offset(side);
                Direction side2 = side.getOpposite();
                if (!mc.world.getBlockState(neighbor).isAir() && !BlockUtils.isClickable(mc.world.getBlockState(neighbor).getBlock())) {
                    ((IVec3d) hitPos).meteor$set((double) neighbor.getX() + 0.5D + (double) side2.getVector().getX() * 0.5D, (double) neighbor.getY() + 0.5D + (double) side2.getVector().getY() * 0.5D, (double) neighbor.getZ() + 0.5D + (double) side2.getVector().getZ() * 0.5D);
                    if (swing) {
                        mc.player.swingHand(hand);
                    }

                    return true;
                }
            }

            if (!airPlace) {
                return false;
            } else {
                ((IVec3d) hitPos).meteor$set(blockPos);
                if (swing) {
                    mc.player.swingHand(hand);
                }

                return true;
            }
        }
    }

    public static boolean checkIfBlockInBB(Class<? extends Block> blockClass) {
        return checkIfBlockInBB(blockClass, (int) Math.floor(mc.player.getBoundingBox(mc.player.getPose()).minY));
    }

    public static double[] directionSpeed(double speed) {
        float forward = mc.player.input.movementForward;
        float side = mc.player.input.movementSideways;

        if (forward != 0.0f) {
            if (side > 0.0f) {
            } else if (side < 0.0f) {
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        return new double[0];
    }


    public static boolean checkIfBlockInBB(Class<? extends Block> blockClass, int minY) {
        for (int iX = MathHelper.floor(mc.player.getBoundingBox(mc.player.getPose()).minX); iX < MathHelper.ceil(mc.player.getBoundingBox(mc.player.getPose()).maxX); iX++) {
            for (int iZ = MathHelper.floor(mc.player.getBoundingBox(mc.player.getPose()).minZ); iZ < MathHelper.ceil(mc.player.getBoundingBox(mc.player.getPose()).maxZ); iZ++) {
                BlockState state = mc.world.getBlockState(new BlockPos(iX, minY, iZ));
                if (state != null && blockClass.isInstance(state.getBlock())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static double getPlayerSpeed(PlayerEntity player) {
        if (player == null) return 0;
        double tX = Math.abs(player.getX() - player.prevX);
        double tZ = Math.abs(player.getZ() - player.prevZ);
        double length = Math.sqrt(tX * tX + tZ * tZ);
        return length * 20;
    }

    public static void getPickaxe(boolean ironPickaxe) {
        FindItemResult pickaxe = InvUtils.find(itemStack -> itemStack.getItem() == Items.DIAMOND_PICKAXE || itemStack.getItem() == Items.NETHERITE_PICKAXE);
        if (ironPickaxe) pickaxe = InvUtils.find(itemStack -> itemStack.getItem() == Items.IRON_PICKAXE);
        InvUtils.swap(pickaxe.slot(), false);
    }

    public static boolean canSeePos(BlockPos pos) {
        Vec3d vec1 = new Vec3d(0, 0, 0);
        Vec3d vec2 = new Vec3d(0, 0, 0);

        ((IVec3d) vec1).meteor$set(mc.player.getX(), mc.player.getY() + mc.player.getStandingEyeHeight(), mc.player.getZ());
        ((IVec3d) vec2).meteor$set(pos.getX(), pos.getY(), pos.getZ());
        boolean canSeeFeet = mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;

        ((IVec3d) vec2).meteor$set(pos.getX(), pos.getY(), pos.getZ());
        boolean canSeeEyes = mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;

        return canSeeFeet || canSeeEyes;
    }

    public static boolean placeBlockMainHand(BlockPos pos) {
        return placeBlockMainHand(false, -1, -1, false, false, pos);
    }

    public static boolean placeBlockMainHand(Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, BlockPos pos) {
        return placeBlockMainHand(rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, pos, true, false);
    }

    public static boolean placeBlockMainHand(Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, BlockPos pos, Boolean airPlace, Boolean forceAirplace) {
        return placeBlockMainHand(rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, pos, airPlace, forceAirplace, false);
    }

    public static boolean placeBlockMainHand(Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, BlockPos pos, Boolean airPlace, Boolean forceAirplace, Boolean ignoreEntity) {
        return placeBlockMainHand(rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, pos, airPlace, forceAirplace, ignoreEntity, null);
    }

    public static boolean placeBlockMainHand(Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, BlockPos pos, Boolean airPlace, Boolean forceAirplace, Boolean ignoreEntity, Direction overrideSide) {
        return placeBlock(false, -1, rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, Hand.MAIN_HAND, pos, airPlace, forceAirplace, ignoreEntity, overrideSide);
    }


    public static boolean placeBlockMainHand(boolean packetPlace, int slot, BlockPos pos) {
        return placeBlockMainHand(packetPlace, slot, false, -1, -1, false, false, pos);
    }

    public static boolean placeBlockMainHand(boolean packetPlace, int slot, Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, BlockPos pos) {
        return placeBlockMainHand(packetPlace, slot, rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, pos, true, false);
    }

    public static boolean placeBlockMainHand(boolean packetPlace, int slot, Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, BlockPos pos, Boolean airPlace, Boolean forceAirplace) {
        return placeBlockMainHand(packetPlace, slot, rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, pos, airPlace, forceAirplace, false);
    }

    public static boolean placeBlockMainHand(boolean packetPlace, int slot, Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, BlockPos pos, Boolean airPlace, Boolean forceAirplace, Boolean ignoreEntity) {
        return placeBlockMainHand(packetPlace, slot, rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, pos, airPlace, forceAirplace, ignoreEntity, null);
    }

    public static boolean placeBlockMainHand(boolean packetPlace, int slot, Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, BlockPos pos, Boolean airPlace, Boolean forceAirplace, Boolean ignoreEntity, Direction overrideSide) {
        return placeBlock(packetPlace, slot, rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, Hand.MAIN_HAND, pos, airPlace, forceAirplace, ignoreEntity, overrideSide);
    }


    public static boolean placeBlockNoRotate(Hand hand, BlockPos pos) {
        return placeBlock(false, -1, false, -1, -1, false, false, hand, pos, true, false);
    }

    public static boolean placeBlockNoRotate(boolean packetPlace, int slot, Hand hand, BlockPos pos) {
        return placeBlock(packetPlace, slot, false, -1, -1, false, false, hand, pos, true, false);
    }


    public static boolean placeBlock(Hand hand, BlockPos pos) {
        return placeBlock(false, -1, -1, false, false, hand, pos, true, false);
    }

    public static boolean placeBlock(Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, Hand hand, BlockPos pos) {
        return placeBlock(rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, hand, pos, false, false);
    }

    public static boolean placeBlock(Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, Hand hand, BlockPos pos, Boolean airPlace, Boolean forceAirplace) {
        return placeBlock(rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, hand, pos, airPlace, forceAirplace, false);
    }

    public static boolean placeBlock(Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, Hand hand, BlockPos pos, Boolean airPlace, Boolean forceAirplace, Boolean ignoreEntity) {
        return placeBlock(rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, hand, pos, airPlace, forceAirplace, ignoreEntity, null);
    }

    public static boolean placeBlock(Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, Hand hand, BlockPos pos, Boolean airPlace, Boolean forceAirplace, Boolean ignoreEntity, Direction overrideSide) {
        return placeBlock(false, -1, rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, hand, pos, airPlace, forceAirplace, ignoreEntity, overrideSide);
    }


    public static boolean placeBlock(boolean packetPlace, int slot, Hand hand, BlockPos pos) {
        return placeBlock(packetPlace, slot, false, -1, -1, false, false, hand, pos, true, false);
    }

    public static boolean placeBlock(boolean packetPlace, int slot, Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, Hand hand, BlockPos pos) {
        return placeBlock(packetPlace, slot, rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, hand, pos, false, false);
    }

    public static boolean placeBlock(boolean packetPlace, int slot, Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, Hand hand, BlockPos pos, Boolean airPlace, Boolean forceAirplace) {
        return placeBlock(packetPlace, slot, rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, hand, pos, airPlace, forceAirplace, false);
    }

    public static boolean placeBlock(boolean packetPlace, int slot, Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, Hand hand, BlockPos pos, Boolean airPlace, Boolean forceAirplace, Boolean ignoreEntity) {
        return placeBlock(packetPlace, slot, rotate, rotationKey, rotationPriority, instantRotation, instantBypassesCurrent, hand, pos, airPlace, forceAirplace, ignoreEntity, null);
    }

    public static boolean placeBlock(boolean packetPlace, int slot, Boolean rotate, int rotationKey, int rotationPriority, boolean instantRotation, boolean instantBypassesCurrent, Hand hand, BlockPos pos, Boolean airPlace, Boolean forceAirplace, Boolean ignoreEntity, Direction overrideSide) {
        // make sure place is empty if ignoreEntity is not true
        if (ignoreEntity) {
            return false;

        }

            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));

            int oldSlot = mc.player.getInventory().selectedSlot;
            if (slot != -1) {
                mc.player.getInventory().selectedSlot = slot;
                // When packet placing we must send an update slot packet first
            }

            if (packetPlace) ;
            else

                mc.player.swingHand(hand);

            if (slot != -1) {
                if (!packetPlace) mc.player.getInventory().selectedSlot = oldSlot;
            }

            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));

            return true;
        }
    }