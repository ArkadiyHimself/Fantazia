package net.arkadiyhimself.combatimprovement.mixin;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents.NewEvents;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect.BarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrierEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    private LivingEntity entity = (LivingEntity) (Object) this;
    @Inject(at = @At(value = "HEAD"), method = "playHurtSound", cancellable = true)
    protected void cancelSound(DamageSource pSource, CallbackInfo ci) {
        if (BarrierEffect.getUnwrap(entity).hasBarrier() || LayeredBarrierEffect.getUnwrap(entity).hasBarrier()) { ci.cancel(); }
    }
}
