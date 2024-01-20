package net.arkadiyhimself.combatimprovement.MobEffects.effectsdostuff;

import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.StunEffect.StunEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class Stun extends MobEffect {
    public Stun(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }
    private int duration;
    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        StunEffect.get(pLivingEntity).ifPresent(stun -> stun.setDuration(duration));
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        this.duration = pDuration;
        return true;
    }
}
