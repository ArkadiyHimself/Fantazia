package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.registry.MobEffectRegistry;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slime.class)
public class MixinSlime {
    @Inject(at = @At("HEAD"), method = "isDealsDamage",cancellable = true)
    private void dealDamage(CallbackInfoReturnable<Boolean> cir) {
        Slime slime = (Slime) (Object) this;
        if (slime.hasEffect(MobEffectRegistry.STUN.get()) || slime.hasEffect(MobEffectRegistry.DISARM.get())) cir.setReturnValue(false);
    }

    @Inject(at = @At("HEAD"), method = "jumpFromGround",cancellable = true)
    private void jumpCancel(CallbackInfo ci) {
        Slime slime = (Slime) (Object) this;
        if (slime.hasEffect(MobEffectRegistry.STUN.get()) || slime.hasEffect(MobEffectRegistry.FROZEN.get())) ci.cancel();
    }
}
