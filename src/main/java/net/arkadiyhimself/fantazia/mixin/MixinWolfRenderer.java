package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.FuryEffect;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfRenderer.class)
public class MixinWolfRenderer {
    @Inject(at = @At("HEAD"), method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Wolf;)Lnet/minecraft/resources/ResourceLocation;", cancellable = true)
    private void angry(Wolf pEntity, CallbackInfoReturnable<ResourceLocation> cir) {
        FuryEffect furyEffect = LivingEffectGetter.takeHolder(pEntity, FuryEffect.class);
        if (furyEffect != null && furyEffect.isFurious()) cir.setReturnValue(pEntity.getVariant().value().angryTexture());
    }

}
