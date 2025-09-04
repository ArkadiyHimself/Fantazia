package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public abstract class MixinIronGolem extends LivingEntity {
    public MixinIronGolem(EntityType<? extends LivingEntity> pEntityType, Level pLevel, int attackAnimationTick) {
        super(pEntityType, pLevel);
        this.attackAnimationTick = attackAnimationTick;
    }
    @Shadow private int attackAnimationTick;
    @Inject(at = @At("HEAD"), method = "doHurtTarget", cancellable = true)
    private void cancelAnim(CallbackInfoReturnable<Boolean> cir) {
        if (LivingEffectHelper.hasEffectSimple(this, FTZMobEffects.DISARM.value())) cir.setReturnValue(false);
    }
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/IronGolem;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), method = "doHurtTarget")
    private void cancelSound(IronGolem instance, SoundEvent soundEvent, float v, float p) {
        if (!LivingEffectHelper.hasEffectSimple(this, FTZMobEffects.DISARM.value())) instance.playSound(soundEvent,v,p);
    }

}
