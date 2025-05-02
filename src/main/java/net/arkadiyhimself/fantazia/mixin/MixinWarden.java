package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.StunEffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public class MixinWarden {
    @Unique
    private final Warden fantazia$warden = (Warden) (Object) this;
    @Inject(at = @At("HEAD"), method = "doHurtTarget", cancellable = true)
    private void cancelAttack(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        StunEffectHolder stunEffect = LivingEffectHelper.takeHolder(fantazia$warden, StunEffectHolder.class);
        if (LivingEffectHelper.hasEffectSimple(fantazia$warden, FTZMobEffects.DISARM.value()) || stunEffect != null && stunEffect.stunned()) cir.setReturnValue(false);
    }
}
