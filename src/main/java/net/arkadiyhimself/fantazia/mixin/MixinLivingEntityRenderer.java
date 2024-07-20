package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {
    @Inject(at = @At("HEAD"), method = "getOverlayCoords", cancellable = true)
    private static void preventTurningRed(LivingEntity pLivingEntity, float pU, CallbackInfoReturnable<Integer> cir) {
        if (!WhereMagicHappens.Abilities.hurtRedColor(pLivingEntity)) {
            cir.setReturnValue(655360);
        }
    }
}
