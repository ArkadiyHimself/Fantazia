package net.arkadiyhimself.combatimprovement.MobEffects.effectsdostuff;

import net.arkadiyhimself.combatimprovement.api.DamageTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;

import java.util.ArrayList;
import java.util.List;

public class Frozen extends MobEffect {
    public Frozen(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        DamageSource FROZEN = new DamageSource(pLivingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.FROZEN));
        if (pLivingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) { pLivingEntity.hurt(FROZEN, 1f); }
        else if (pLivingEntity.fireImmune()) { pLivingEntity.hurt(FROZEN, 2.25f); }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}
