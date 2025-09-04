package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.holders.PuppeteeredEffectHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetingConditions.class)
public class MixinTargetingConditions {
    @Inject(at = @At("HEAD"), method = "test", cancellable = true)
    private void changeTarget(LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        boolean disguised = target.hasEffect(FTZMobEffects.DISGUISED);
        PuppeteeredEffectHolder puppeteeredEffect = LivingEffectHelper.takeHolder(attacker, PuppeteeredEffectHolder.class);
        boolean owned = puppeteeredEffect != null && puppeteeredEffect.isPuppeteeredBy(target);
        if (disguised || owned) cir.setReturnValue(false);
    }
}
