package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.DisarmEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.StunEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public class MixinWarden {
    private final Warden warden = (Warden) (Object) this;
    @Inject(at = @At("HEAD"), method = "doHurtTarget", cancellable = true)
    private void cancelAttack(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        DisarmEffect disarmEffect = EffectGetter.takeEffectHolder(warden, DisarmEffect.class);
        StunEffect stunEffect = EffectGetter.takeEffectHolder(warden, StunEffect.class);
        if (disarmEffect != null && disarmEffect.renderDisarm() || stunEffect != null && stunEffect.stunned()) cir.setReturnValue(false);
    }
}
