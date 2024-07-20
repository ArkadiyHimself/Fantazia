package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.HandlersAndHelpers.CustomEvents.NewEvents;
import net.arkadiyhimself.fantazia.api.DamageTypeRegistry;
import net.arkadiyhimself.fantazia.api.SoundRegistry;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.AttachCommonData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.CommonData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.EffectGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.EffectManager;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects.BarrierEffect;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects.LayeredBarrierEffect;
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
                CommonData data = AttachCommonData.getUnwrap(entity);
                if (data != null && data.makeBleedSound()) {
                    entity.playSound(SoundRegistry.BLOODLOSS.get());
                    data.onBleedSound();
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
