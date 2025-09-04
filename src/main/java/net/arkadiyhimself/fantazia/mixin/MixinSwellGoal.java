package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwellGoal.class)
public class MixinSwellGoal {

    @Shadow @Final private Creeper creeper;

    @Inject(at = @At("HEAD"), method = "canUse", cancellable = true)
    private void canUse(CallbackInfoReturnable<Boolean> cir) {
        if (creeper.hasEffect(FTZMobEffects.DISARM)) cir.setReturnValue(false);
    }
}
