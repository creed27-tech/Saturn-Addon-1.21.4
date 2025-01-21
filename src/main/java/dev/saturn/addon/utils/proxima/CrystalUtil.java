package dev.saturn.addon.utils.proxima;

import dev.saturn.addon.modules.Combat.DeathCrystal.DeathCrystal;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
//import dev.saturn.addon.modules.Combat.CrystalBoomer;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CrystalUtil {
    public static float calculateDamage(EndCrystalEntity crystal, Entity entity, boolean predict, BlockPos obsidianPos, boolean ignoreTerrain) {
        if (crystal == null) return 0;
        return calculateDamage(crystal.getX(), crystal.getY(), crystal.getZ(), entity, predict, obsidianPos, ignoreTerrain);
    }

    public static float calculateDamage(BlockPos pos, Entity entity, boolean predict, BlockPos obsidianPos, boolean ignoreTerrain) {
        if (pos == null) return 0;
        return calculateDamage(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, entity, predict, pos, ignoreTerrain);
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity, boolean predict, BlockPos obsidianPos, boolean ignoreTerrain) {
        double finald = DamageUtils.crystalDamage((PlayerEntity) entity, new Vec3d(posX, posY, posZ), predict, obsidianPos);
        return (float) finald;
    }


    public static boolean terrainIgnore = false;

    public static float getExplosionDamage(EndCrystalEntity entity, LivingEntity target) {
        return getExplosionDamage(entity.getPos(), 6F, target);
    }

    //private static CrystalBoomer autoCrystal = new CrystalBoomer();

    public static float getExplosionDamage(Vec3d explosionPos, float power, LivingEntity target) {
        if (mc.world.getDifficulty() == Difficulty.PEACEFUL)
            return 0f;

        Explosion explosion = new Explosion() {
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

        double maxDist = power * 2;
        if (!mc.world.getOtherEntities(null, new Box(
                MathHelper.floor(explosionPos.x - maxDist - 1.0),
                MathHelper.floor(explosionPos.y - maxDist - 1.0),
                MathHelper.floor(explosionPos.z - maxDist - 1.0),
                MathHelper.floor(explosionPos.x + maxDist + 1.0),
                MathHelper.floor(explosionPos.y + maxDist + 1.0),
                MathHelper.floor(explosionPos.z + maxDist + 1.0))).contains(target)) {
            return 0f;
        }

        return 0f;
    }

    public static Vec3d getMotionVec(Entity entity, int ticks) {
        double dX = entity.getX() - entity.prevX;
        double dZ = entity.getZ() - entity.prevZ;
        double entityMotionPosX = 0;
        double entityMotionPosZ = 0;
        DeathCrystal autoCrystal = null;
        if (autoCrystal.collision.get()) {
            for (int i = 1; i <= ticks; i++) {
                if (mc.world.getBlockState(new BlockPos((int) (entity.getX() + dX * i), (int) entity.getY(), (int) (entity.getZ() + dZ * i))).getBlock() instanceof AirBlock) {
                    entityMotionPosX = dX * i;
                    entityMotionPosZ = dZ * i;
                } else {
                    break;
                }
            }
        } else {
            entityMotionPosX = dX * ticks;
            entityMotionPosZ = dZ * ticks;
        }

        return new Vec3d(entityMotionPosX, 0, entityMotionPosZ);
    }

    public static boolean isVisible(Vec3d vec3d) {
        Vec3d eyesPos = new Vec3d(mc.player.getX(), (mc.player.getBoundingBox()).minY + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
        return mc.world.raycastBlock(eyesPos, vec3d, new BlockPos((int) vec3d.x, (int) vec3d.y, (int) vec3d.z), VoxelShapes.fullCube(), mc.world.getBlockState(new BlockPos((int) vec3d.x, (int) vec3d.y, (int) vec3d.z))) == null;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float getBlastReduction(LivingEntity entity, float damageInput, Explosion explosion) {
        float damage = damageInput;
        if (entity instanceof PlayerEntity) {
            PlayerEntity ep = (PlayerEntity) entity;
            int k = 0;
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage = damage * (1.0F - f / 25.0F);

            if (entity.hasStatusEffect(StatusEffects.RESISTANCE)) {
                damage = damage - (damage / 4);
            }

            damage = Math.max(damage, 0.0F);
            return damage;

        }
        return damage;
    }

    public static int getCrystalSlot() {
        int crystalSlot = -1;

        if (mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL) {
            crystalSlot = mc.player.getInventory().selectedSlot;
        }


        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.getInventory().getStack(l).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }

        return crystalSlot;
    }

    public static int ping() {
        if (mc.getNetworkHandler() == null) {
            return 50;
        } else if (mc.player == null) {
            return 50;
        } else {
            try {
                return mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency();
            } catch (NullPointerException ignored) {
            }
            return 50;
        }
    }

    public static int getSwordSlot() {
        int swordSlot = -1;

        if (mc.player.getMainHandStack().getItem() == Items.DIAMOND_SWORD) {
            swordSlot = mc.player.getInventory().selectedSlot;
        }

        if (swordSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.getInventory().getStack(l).getItem() == Items.DIAMOND_SWORD) {
                    swordSlot = l;
                    break;
                }
            }
        }

        return swordSlot;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }

            if (!(mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR)) {
                return false;
            }

            for (Entity entity : mc.world.getEntitiesByClass(Entity.class, new Box(boost), new Predicate<Entity>() {
                @Override
                public boolean test(Entity entity) {
                    return false;
                }
            })) {
                if (!(entity instanceof EndCrystalEntity)) {
                    return false;
                }
            }

            for (Entity entity : mc.world.getEntitiesByClass(Entity.class, new Box(boost2), new Predicate<Entity>() {
                @Override
                public boolean test(Entity entity) {
                    return false;
                }
            })) {
                if (!(entity instanceof EndCrystalEntity)) {
                    return false;
                }
            }
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

    public static boolean rayTracePlace(BlockPos pos) {
        double increment = 0.45D;
        double start = 0.05D;
        double end = 0.95D;

        Vec3d eyesPos = new Vec3d(mc.player.getX(), (mc.player.getBoundingBox()).minY + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());

        for (double xS = start; xS <= end; xS += increment) {
            for (double yS = start; yS <= end; yS += increment) {
                for (double zS = start; zS <= end; zS += increment) {
                    Vec3d posVec = (new Vec3d(pos.getX(), pos.getY(), pos.getZ())).add(xS, yS, zS);

                    double distToPosVec = eyesPos.distanceTo(posVec);
                }
            }
        }
        return false;
    }

    public static boolean rayTraceBreak(double x, double y, double z) {
        if (mc.world.raycastBlock(new Vec3d(mc.player.getX(), mc.player.getY() + (double) mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ()), new Vec3d(x, y + 1.8, z), new BlockPos((int) x, (int) y, (int) z), VoxelShapes.fullCube(), mc.world.getBlockState(new BlockPos((int) x, (int) y, (int) z))) == null) {
            return true;
        }
        if (mc.world.raycastBlock(new Vec3d(mc.player.getX(), mc.player.getY() + (double) mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ()), new Vec3d(x, y + 1.5, z), new BlockPos((int) x, (int) y, (int) z),  VoxelShapes.fullCube(), mc.world.getBlockState(new BlockPos((int) x, (int) y, (int) z))) == null) {
            return true;
        }
        return mc.world.raycastBlock(new Vec3d(mc.player.getX(), mc.player.getY() + (double) mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ()), new Vec3d(x, y, z), new BlockPos((int) x, (int) y, (int) z),  VoxelShapes.fullCube(), mc.world.getBlockState(new BlockPos((int) x, (int) y, (int) z))) == null;
    }

}