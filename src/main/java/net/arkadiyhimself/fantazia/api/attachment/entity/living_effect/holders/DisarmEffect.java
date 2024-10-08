package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.monster.warden.Warden;

public class DisarmEffect extends LivingEffectHolder {
    public DisarmEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("disarm_effect"), FTZMobEffects.DISARM);
    }
    public boolean renderDisarm() {
        return duration() > 0;
    }
    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        if (instance.getEffect() == getEffect() && getEntity() instanceof Warden warden) {
            SonicBoom.setCooldown(getEntity(), 0);
            warden.attackAnimationState.stop();
        }
    }
}
