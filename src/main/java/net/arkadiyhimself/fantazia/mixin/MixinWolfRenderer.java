package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
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
        if (LivingEffectHelper.hasEffect(pEntity, FTZMobEffects.FURY.value())) cir.setReturnValue(pEntity.getVariant().value().angryTexture());
    }

}
