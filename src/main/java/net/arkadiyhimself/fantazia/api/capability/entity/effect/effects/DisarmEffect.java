package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.monster.warden.Warden;

public class DisarmEffect extends EffectHolder {
    public DisarmEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.DISARM.get());
    }
    public boolean renderDisarm() {
        return getDur() > 0;
    }
    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        if (instance.getEffect() == getEffect() && getOwner() instanceof Warden warden) {
            SonicBoom.setCooldown(getOwner(), 0);
            warden.attackAnimationState.stop();
        }
    }
}
