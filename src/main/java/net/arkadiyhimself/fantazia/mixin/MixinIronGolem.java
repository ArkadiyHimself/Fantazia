package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.DisarmEffect;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
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
    @Inject(at = @At("TAIL"), method = "doHurtTarget")
    private void cancelAnim(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
    }
    @Inject(at = @At("HEAD"), method = "doHurtTarget", cancellable = true)
    private void cancelAnim(CallbackInfoReturnable<Boolean> cir) {
        DisarmEffect disarmEffect = EffectGetter.takeEffectHolder(this, DisarmEffect.class);
        if (disarmEffect != null && disarmEffect.renderDisarm()) cir.setReturnValue(false);
    }
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/IronGolem;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), method = "doHurtTarget")
    private void cancelSound(IronGolem instance, SoundEvent soundEvent, float v, float p) {
        DisarmEffect disarmEffect = EffectGetter.takeEffectHolder(this, DisarmEffect.class);
        if (disarmEffect == null || !disarmEffect.renderDisarm()) instance.playSound(soundEvent,v,p);
    }

}
