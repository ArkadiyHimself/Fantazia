package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectManager;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.HaemorrhageEffect;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    private final LivingEntity entity = (LivingEntity) (Object) this;
    @Inject(at = @At(value = "HEAD"), method = "playHurtSound", cancellable = true)
    protected void cancelSound(DamageSource pSource, CallbackInfo ci) {
        if (pSource == null) return;
        if (pSource.is(FTZDamageTypeTags.NO_HURT_SOUND)) ci.cancel();

        EffectManager effectManager = EffectGetter.getUnwrap(entity);
        if (effectManager == null) return;
        if (pSource.is(FTZDamageTypes.BLEEDING)) effectManager.getEffect(HaemorrhageEffect.class).ifPresent(HaemorrhageEffect::tryMakeSound);

        Collection<MobEffectInstance> instanceCollection = entity.getActiveEffects();
        for (MobEffectInstance instance : instanceCollection) if (FTZMobEffectTags.hasTag(instance.getEffect(), FTZMobEffectTags.BARRIER)) ci.cancel();
    }
    @Inject(at = @At(value = "HEAD"), method = "onItemPickup", cancellable = true)
    protected void pickUp(ItemEntity pItemEntity, CallbackInfo ci) {
        boolean pickup = FTZEvents.ForgeExtension.onLivingPickUpItem(entity, pItemEntity);
        if (!pickup) ci.cancel();
    }
    @Inject(at = @At(value = "HEAD"), method = "curePotionEffects", cancellable = true, remap = false)
    private void milkBucket(ItemStack curativeItem, CallbackInfoReturnable<Boolean> cir) {
        EffectCleansing.tryCleanseAll(entity, Cleanse.BASIC);
        cir.cancel();
    }
}
