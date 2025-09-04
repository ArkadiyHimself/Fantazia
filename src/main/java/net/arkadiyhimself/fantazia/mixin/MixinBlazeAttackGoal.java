package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.world.entity.monster.Blaze;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.Blaze.BlazeAttackGoal")
public class MixinBlazeAttackGoal {

    @Shadow @Final private Blaze blaze;

    @Inject(at = @At("HEAD"), method = "canUse", cancellable = true)
    private void canUse(CallbackInfoReturnable<Boolean> cir) {
        if (blaze.hasEffect(FTZMobEffects.DISARM)) cir.setReturnValue(false);
    }
}
