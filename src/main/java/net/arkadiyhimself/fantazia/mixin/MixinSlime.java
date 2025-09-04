package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slime.class)
public class MixinSlime {
    @SuppressWarnings("ConstantConditions")
    @Inject(at = @At("HEAD"), method = "isDealsDamage",cancellable = true)
    private void dealDamage(CallbackInfoReturnable<Boolean> cir) {
        Slime slime = (Slime) (Object) this;
        if (slime.hasEffect(FTZMobEffects.STUN) || slime.hasEffect(FTZMobEffects.DISARM)) cir.setReturnValue(false);
    }
    @SuppressWarnings("ConstantConditions")
    @Inject(at = @At("HEAD"), method = "jumpFromGround",cancellable = true)
    private void jumpCancel(CallbackInfo ci) {
        Slime slime = (Slime) (Object) this;
        if (slime.hasEffect(FTZMobEffects.STUN) || slime.hasEffect(FTZMobEffects.FROZEN)) ci.cancel();
    }
}
