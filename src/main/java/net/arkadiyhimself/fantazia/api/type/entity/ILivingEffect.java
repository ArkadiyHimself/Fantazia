package net.arkadiyhimself.fantazia.api.type.entity;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface ILivingEffect extends IBasicHolder {
    @NotNull LivingEntity getEntity();
    int initialDuration();
    int duration();
    @NotNull Holder<MobEffect> getEffect();
    void added(MobEffectInstance instance);
    void ended();
}
