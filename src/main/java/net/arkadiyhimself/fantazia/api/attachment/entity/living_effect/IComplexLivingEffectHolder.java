package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

public interface IComplexLivingEffectHolder extends ILivingEffectHolder {

    @NotNull Holder<MobEffect> getEffect();
    int initialDuration();
    int duration();
}
