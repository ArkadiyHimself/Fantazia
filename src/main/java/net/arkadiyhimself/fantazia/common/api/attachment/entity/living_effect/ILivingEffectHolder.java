package net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect;

import net.arkadiyhimself.fantazia.common.api.attachment.IBasicHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface ILivingEffectHolder extends IBasicHolder {

    @NotNull LivingEntity getEntity();
    void added(MobEffectInstance instance);
    void ended(MobEffect effect);
}
