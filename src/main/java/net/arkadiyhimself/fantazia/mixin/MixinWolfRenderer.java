package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.FuryEffect;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfRenderer.class)
public class MixinWolfRenderer {
    @Shadow @Final private static ResourceLocation WOLF_ANGRY_LOCATION;
    @Inject(at = @At("HEAD"), method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Wolf;)Lnet/minecraft/resources/ResourceLocation;", cancellable = true)
    private void angry(Wolf pEntity, CallbackInfoReturnable<ResourceLocation> cir) {
        FuryEffect furyEffect = EffectGetter.takeEffectHolder(pEntity, FuryEffect.class);
        if (furyEffect != null && furyEffect.isFurious()) cir.setReturnValue(WOLF_ANGRY_LOCATION);
    }

}
