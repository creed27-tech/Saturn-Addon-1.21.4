package dev.saturn.addon.utils.bed.advance;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import static dev.saturn.addon.utils.bed.advance.PredictionUtils.returnPredictBox;
import static dev.saturn.addon.utils.bed.advance.PredictionUtils.returnPredictVec;
import static dev.saturn.addon.utils.bed.basic.EntityInfo.*;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CrystalUtils {
    private static final Vec3d vec3d = new Vec3d(0, 0, 0);
    private static Explosion explosion;
    private static RaycastContext raycastContext;

    public static void init() {
        MeteorClient.EVENT_BUS.subscribe(CrystalUtils.class);
    }

    @EventHandler
    private static void onGameJoined(GameJoinedEvent event) {
        explosion = new Explosion() {
            @Override
            public ServerWorld getWorld() {
                return null;
            }

            @Override
            public DestructionType getDestructionType() {
                return null;
            }

            @Override
            public @Nullable LivingEntity getCausingEntity() {
                return null;
            }

            @Override
            public @Nullable Entity getEntity() {
                return null;
            }

            @Override
            public float getPower() {
                return 0;
            }

            @Override
            public Vec3d getPosition() {
                return null;
            }

            @Override
            public boolean canTriggerBlocks() {
                return false;
            }

            @Override
            public boolean preservesDecorativeEntities() {
                return false;
            }
        };
        raycastContext = new RaycastContext(null, null, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, mc.player);
    }

    public static double crystalDamage(PlayerEntity player, Vec3d crystal, boolean predictMovement, boolean collision, int i, BlockPos obsidianPos, boolean ignoreTerrain) {
        if (!notNull(player)) return 0;
        if (isCreative(player) && !(player instanceof FakePlayerEntity)) return 0;

        Vec3d pVec = returnPredictVec(player, collision, i);
        ((IVec3d) vec3d).meteor$set(player.getPos().x, player.getPos().y, player.getPos().z); // Replaced | Might not work
        if (predictMovement) ((IVec3d) vec3d).meteor$set(pVec.getX(), pVec.getY(), pVec.getZ()); // Replaced | Might not work

        double modDistance = Math.sqrt(vec3d.squaredDistanceTo(crystal));
        if (modDistance > 12) return 0;

        double impact = (1 - (modDistance / 12));
        double damage = ((impact * impact + impact) / 2 * 7 * (6 * 2) + 1);

        damage = getDamageForDifficulty(damage);

        return damage < 0 ? 0 : damage;
    }

    public static double crystalDamage(PlayerEntity player, Vec3d crystal) {
        return crystalDamage(player, crystal, false, false, 0, null, false);
    }

    // Utils

    private static double getDamageForDifficulty(double damage) {
        return switch (mc.world.getDifficulty()) {
            case PEACEFUL -> 0;
            case EASY -> Math.min(damage / 2 + 1, damage);
            case NORMAL -> 0.0;
            case HARD -> 0.0;
        };
    }
}