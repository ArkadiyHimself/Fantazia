package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.DisarmEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.StunEffect;
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
        DisarmEffect disarmEffect = LivingEffectGetter.takeHolder(fantazia$warden, DisarmEffect.class);
        StunEffect stunEffect = LivingEffectGetter.takeHolder(fantazia$warden, StunEffect.class);
        if (disarmEffect != null && disarmEffect.renderDisarm() || stunEffect != null && stunEffect.stunned()) cir.setReturnValue(false);
    }
}
