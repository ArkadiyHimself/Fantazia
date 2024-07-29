package net.arkadiyhimself.fantazia.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(at = @At("HEAD"), method = "bobHurt", cancellable = true)
    private void bobHurt(PoseStack pPoseStack, float pPartialTicks, CallbackInfo ci) {
        Entity entity = Minecraft.getInstance().getCameraEntity();
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.getLastDamageSource() != null && livingEntity.getLastDamageSource().is(FTZDamageTypes.BLEEDING)) ci.cancel();
        }
    }
}
