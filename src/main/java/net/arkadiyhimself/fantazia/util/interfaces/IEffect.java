package net.arkadiyhimself.fantazia.util.interfaces;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public interface IEffect extends INBTsaver, ITicking {
    int getInitDur();
    int getDur();
    MobEffect getEffect();
    LivingEntity getOwner();
    void respawn();
    void added(MobEffectInstance instance);
    void ended();
}
