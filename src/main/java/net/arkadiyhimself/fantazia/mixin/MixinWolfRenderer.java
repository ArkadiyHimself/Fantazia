package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.AttachCommonData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.CommonData;
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
        CommonData data = AttachCommonData.getUnwrap(pEntity);
        if (data != null && data.isFurious()) {
            cir.setReturnValue(WOLF_ANGRY_LOCATION);
        }
    }

}
