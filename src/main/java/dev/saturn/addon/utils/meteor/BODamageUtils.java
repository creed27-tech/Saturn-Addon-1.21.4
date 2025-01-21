
package dev.saturn.addon.utils.meteor;

import dev.saturn.addon.utils.SettingUtils;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.explosion.Explosion;

import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BODamageUtils {
    private static final Vec3d vec3d = new Vec3d(0, 0, 0);
    private static Explosion explosion;
    public static RaycastContext raycastContext;
    public static RaycastContext bedRaycast;

    @PreInit
    public static void init() {
        MeteorClient.EVENT_BUS.subscribe(BODamageUtils.class);
    }

    public static double anchorDamage(AbstractClientPlayerEntity target, Box box, BlockPos pos) {
        return 0;
    }

    public static double crystal(ClientPlayerEntity player, Box box, Vec3d vec, BlockPos blockPos, Boolean aBoolean) {
        return 0;
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        raycastContext = new RaycastContext(null, null, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, mc.player);
        bedRaycast = new RaycastContext(null, null, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, mc.player);
    }

    // Calculate Crystal Damage
    public double calculateCrystalDamage(LivingEntity entity, Box boundingBox, Vec3d crystalPosition, BlockPos obsidianPos, boolean ignoreTerrain) {
        if (entity == null || entity instanceof PlayerEntity && ((PlayerEntity) entity).getAbilities().creativeMode) return 0;

        Vec3d center = new Vec3d((boundingBox.minX + boundingBox.maxX) / 2, boundingBox.minY, (boundingBox.minZ + boundingBox.maxZ) / 2);
        double distance = center.squaredDistanceTo(crystalPosition);

        if (distance > 144) return 0; // Maximum distance (12 blocks squared)

        return distance;
    }

    private double calculateImpactDamage(double impact, int explosionPower) {
        double baseDamage = ((impact * impact + impact) / 2 * 7 * (explosionPower * 2) + 1);
        return adjustForDifficulty(baseDamage);
    }

    private double applyDamageReductions(double damage, LivingEntity entity, Explosion explosion, boolean isBlastProtection) {
        damage = applyArmorReduction(damage, entity);
        damage = applyResistanceReduction(damage, entity);

        if (isBlastProtection) {
            damage = applyEnchantmentReduction(damage, entity, explosion, true);
        } else {
            damage = applyEnchantmentReduction(damage, entity, explosion, false);
        }

        return damage;
    }

    private double applyArmorReduction(double damage, LivingEntity entity) {
        return damage;
    }

    private double applyResistanceReduction(double damage, LivingEntity entity) {
        if (entity.hasStatusEffect(StatusEffects.RESISTANCE)) {
            int level = (entity.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
            damage *= (25 - level) / 25.0;
        }
        return damage;
    }

    private double applyEnchantmentReduction(double damage, Entity entity, Explosion explosion, boolean isBlast) {
        return damage;
    }

    // Sword Damage Calculation
    public double calculateSwordDamage(ItemStack weapon, PlayerEntity attacker, LivingEntity target, boolean fullyCharged) {
        double baseDamage = getWeaponBaseDamage(weapon);
        if (fullyCharged) baseDamage *= 1.5;

        baseDamage += calculateSharpnessBonus(weapon);
        baseDamage += calculateStrengthBonus(attacker);

        baseDamage = applyResistanceReduction(baseDamage, target);
        baseDamage = applyArmorReduction(baseDamage, target);
        baseDamage = applyEnchantmentReduction(baseDamage, target, null, false);

        return Math.max(baseDamage, 0);
    }

    private double getWeaponBaseDamage(ItemStack weapon) {
        if (weapon == null || weapon.isEmpty()) return 0;
        return 0;
    }


    private double calculateSharpnessBonus(ItemStack weapon) {
        return 0;
    }

    private double calculateStrengthBonus(PlayerEntity attacker) {
        if (attacker.hasStatusEffect(StatusEffects.STRENGTH)) {
            int amplifier = attacker.getStatusEffect(StatusEffects.STRENGTH).getAmplifier() + 1;
            return 3 * amplifier;
        }
        return 0;
    }

    // Damage Adjustments for Difficulty
    private double adjustForDifficulty(double damage) {
        return switch (mc.world.getDifficulty()) {
            case EASY -> Math.min(damage / 2 + 1, damage);
            case HARD -> damage * 1.5;
            default -> damage;
        };
    }

    private static double resistanceReduction(LivingEntity player, double damage) {
        if (player.hasStatusEffect(StatusEffects.RESISTANCE)) {
            int lvl = (player.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1);
            damage *= (1 - (lvl * 0.2));
        }

        return damage < 0 ? 0 : damage;
    }

    public static double getExposure(Vec3d source, Entity entity, Box box, RaycastContext raycastContext, BlockPos ignore, boolean ignoreTerrain) {
        double d = 1 / ((box.maxX - box.minX) * 2 + 1);
        double e = 1 / ((box.maxY - box.minY) * 2 + 1);
        double f = 1 / ((box.maxZ - box.minZ) * 2 + 1);
        double g = (1 - Math.floor(1 / d) * d) / 2;
        double h = (1 - Math.floor(1 / f) * f) / 2;

        if (!(d < 0) && !(e < 0) && !(f < 0)) {
            int i = 0;
            int j = 0;

            for (double k = 0; k <= 1; k += d) {
                for (double l = 0; l <= 1; l += e) {
                    for (double m = 0; m <= 1; m += f) {
                        double n = MathHelper.lerp(k, box.minX, box.maxX);
                        double o = MathHelper.lerp(l, box.minY, box.maxY);
                        double p = MathHelper.lerp(m, box.minZ, box.maxZ);

                        ((IVec3d) vec3d).meteor$set(n + g, o, p + h); // Replaced | Might not work
                        ((IRaycastContext) raycastContext).meteor$set(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity); // Replaced | Might not work

                        if (raycast(raycastContext, ignore, ignoreTerrain).getType() == HitResult.Type.MISS) i++;

                        j++;
                    }
                }
            }

            return (double) i / j;
        }

        return 0;
    }

    public static BlockHitResult raycast(RaycastContext context) {
        return BlockView.raycast(context.getStart(), context.getEnd(), context, (raycastContext, blockPos) -> {
            BlockState blockState;
            blockState = mc.world.getBlockState(blockPos);

            Vec3d vec3d = raycastContext.getStart();
            Vec3d vec3d2 = raycastContext.getEnd();

            VoxelShape voxelShape = raycastContext.getBlockShape(blockState, mc.world, blockPos);
            BlockHitResult blockHitResult = mc.world.raycastBlock(vec3d, vec3d2, blockPos, voxelShape, blockState);
            VoxelShape voxelShape2 = VoxelShapes.empty();
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, blockPos);

            double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult2.getPos());

            return d <= e ? blockHitResult : blockHitResult2;
        }, (raycastContext) -> {
            Vec3d vec3d = raycastContext.getStart().subtract(raycastContext.getEnd());
            return BlockHitResult.createMissed(raycastContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(raycastContext.getEnd()));
        });
    }

    private static BlockHitResult raycast(RaycastContext context, BlockPos ignore, boolean ignoreTerrain) {
        return BlockView.raycast(context.getStart(), context.getEnd(), context, (raycastContext, blockPos) -> {
            BlockState blockState;
            if (blockPos.equals(ignore)) blockState = Blocks.AIR.getDefaultState();
            else {
                blockState = mc.world.getBlockState(blockPos);
                if (blockState.getBlock().getBlastResistance() < 600 && ignoreTerrain)
                    blockState = Blocks.AIR.getDefaultState();
            }

            Vec3d vec3d = raycastContext.getStart();
            Vec3d vec3d2 = raycastContext.getEnd();

            VoxelShape voxelShape = raycastContext.getBlockShape(blockState, mc.world, blockPos);
            BlockHitResult blockHitResult = mc.world.raycastBlock(vec3d, vec3d2, blockPos, voxelShape, blockState);
            VoxelShape voxelShape2 = VoxelShapes.empty();
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, blockPos);

            double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult2.getPos());

            return d <= e ? blockHitResult : blockHitResult2;
        }, (raycastContext) -> {
            Vec3d vec3d = raycastContext.getStart().subtract(raycastContext.getEnd());
            return BlockHitResult.createMissed(raycastContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(raycastContext.getEnd()));
        });
    }
}