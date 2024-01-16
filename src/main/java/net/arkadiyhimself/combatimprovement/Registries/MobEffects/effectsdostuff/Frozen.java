package net.arkadiyhimself.combatimprovement.Registries.MobEffects.effectsdostuff;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.List;

public class Frozen extends MobEffect {
    public static DamageSource FROZEN = new DamageSource("frozen").bypassArmor().bypassMagic();
    public Frozen(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) { pLivingEntity.hurt(FROZEN, 1f); }
        else if (pLivingEntity.fireImmune()) { pLivingEntity.hurt(FROZEN, 2.25f); }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}
