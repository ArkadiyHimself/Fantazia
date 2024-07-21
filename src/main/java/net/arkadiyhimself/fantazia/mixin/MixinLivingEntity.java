package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects.BarrierEffect;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects.HaemorrhageEffect;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.events.custom.NewEvents;
import net.arkadiyhimself.fantazia.registry.DamageTypeRegistry;
import net.arkadiyhimself.fantazia.registry.SoundRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Shadow public abstract void baseTick();

    private LivingEntity entity = (LivingEntity) (Object) this;
    @Inject(at = @At(value = "HEAD"), method = "playHurtSound", cancellable = true)
    protected void cancelSound(DamageSource pSource, CallbackInfo ci) {
        if (pSource != null) {
            if (pSource.is(DamageTypeRegistry.BLEEDING)) {
                ci.cancel();
                EffectManager effectManager = EffectGetter.getUnwrap(entity);
                if (effectManager == null) return;
                HaemorrhageEffect haemorrhageEffect = effectManager.takeEffect(HaemorrhageEffect.class);

                if (haemorrhageEffect != null && haemorrhageEffect.makeSound()) {
                    entity.playSound(SoundRegistry.BLOODLOSS.get());
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
        boolean pickup = NewEvents.ForgeExtenstion.onLivingPickUpItem(entity, pItemEntity);
        if (!pickup) ci.cancel();
    }
}
