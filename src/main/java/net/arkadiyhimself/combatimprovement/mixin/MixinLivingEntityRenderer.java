package net.arkadiyhimself.combatimprovement.mixin;

import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect.BarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrierEffect;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {
    @Inject(at = @At("HEAD"), method = "getOverlayCoords", cancellable = true)
    private static void preventTurningRed(LivingEntity pLivingEntity, float pU, CallbackInfoReturnable<Integer> cir) {
        if (BarrierEffect.getUnwrap(pLivingEntity).hasBarrier() || LayeredBarrierEffect.getUnwrap(pLivingEntity).hasBarrier()) {
            cir.setReturnValue(655360);
        }
    }
}
