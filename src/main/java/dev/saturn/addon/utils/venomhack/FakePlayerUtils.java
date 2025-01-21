package dev.saturn.addon.utils.venomhack;

import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Iterator;

public class FakePlayerUtils {
    public static void onStatusEffectRemoved(FakePlayerEntity entity, StatusEffectInstance effect) {
        effect.getEffectType();
    }

    public static void clearStatusEffects(FakePlayerEntity entity) {
        Iterator<StatusEffectInstance> iterator = entity.getActiveStatusEffects().values().iterator();

        while (iterator.hasNext()) {
            onStatusEffectRemoved(entity, iterator.next());
            iterator.remove();
        }
    }

    public static void updatePose(FakePlayerEntity entity) {
        EntityPose entityPose = EntityPose.STANDING;
        if (entity.isLiving()) {
            entityPose = EntityPose.GLIDING;
        } else if (entity.isSleeping()) {
            entityPose = EntityPose.SLEEPING;
        } else if (entity.isSwimming()) {
            entityPose = EntityPose.SWIMMING;
        } else if (entity.isUsingRiptide()) {
            entityPose = EntityPose.SPIN_ATTACK;
        } else if (entity.isSneaking() && !entity.getAbilities().flying) {
            entityPose = EntityPose.CROUCHING;
        } else {
            entityPose = EntityPose.STANDING;
        }

        entity.setPose(entityPose);
    }
}