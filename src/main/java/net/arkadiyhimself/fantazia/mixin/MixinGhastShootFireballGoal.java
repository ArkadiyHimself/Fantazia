package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.Ghast.GhastShootFireballGoal")
public class MixinGhastShootFireballGoal {

    @Shadow @Final private Ghast ghast;

    @Shadow public int chargeTime;

    @Inject(at = @At("HEAD"), method = "canUse", cancellable = true)
    private void canUse(CallbackInfoReturnable<Boolean> cir) {
        if (ghast.hasEffect(FTZMobEffects.DISARM)) cir.setReturnValue(true);
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void tick(CallbackInfo ci) {
        if (ghast.hasEffect(FTZMobEffects.DISARM)) {
            chargeTime = 0;
            ci.cancel();
        }
    }
}
