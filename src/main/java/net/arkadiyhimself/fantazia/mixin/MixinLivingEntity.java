package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects.BarrierEffect;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects.HaemorrhageEffect;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    private final LivingEntity entity = (LivingEntity) (Object) this;
    @Inject(at = @At(value = "HEAD"), method = "playHurtSound", cancellable = true)
    protected void cancelSound(DamageSource pSource, CallbackInfo ci) {
        if (pSource != null) {
            if (pSource.is(FTZDamageTypes.BLEEDING)) {
                ci.cancel();
                EffectManager effectManager = EffectGetter.getUnwrap(entity);
                if (effectManager == null) return;
                HaemorrhageEffect haemorrhageEffect = effectManager.takeEffect(HaemorrhageEffect.class);

                if (haemorrhageEffect != null && haemorrhageEffect.makeSound()) {
                    entity.playSound(FTZSoundEvents.BLOODLOSS);
                    haemorrhageEffect.madeSound();
                }
            }
        }

        EffectManager effectManager = EffectGetter.getUnwrap(entity);
        if (effectManager == null) return;
        BarrierEffect barrierEffect = effectManager.takeEffect(BarrierEffect.class);
        boolean bar1 = barrierEffect != null && barrierEffect.hasBarrier();
        LayeredBarrierEffect layeredBarrierEffect = effectManager.takeEffect(LayeredBarrierEffect.class);
        boolean bar2 = layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier();

        if (bar1 || bar2) ci.cancel();
    }
    @Inject(at = @At(value = "HEAD"), method = "onItemPickup", cancellable = true)
    protected void pickUp(ItemEntity pItemEntity, CallbackInfo ci) {
        boolean pickup = FTZEvents.ForgeExtenstion.onLivingPickUpItem(entity, pItemEntity);
        if (!pickup) ci.cancel();
    }
}
