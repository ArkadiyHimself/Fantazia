package net.arkadiyhimself.fantazia.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataManager;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.EvasionData;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    protected MixinLivingEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }
    @Nullable
    private T current = null;
    @Inject(at = @At("HEAD"), method = "getOverlayCoords", cancellable = true)
    private static void preventTurningRed(LivingEntity pLivingEntity, float pU, CallbackInfoReturnable<Integer> cir) {
        if (!EffectHelper.hurtRedColor(pLivingEntity)) cir.setReturnValue(655360);
    }
    @Inject(at = @At("HEAD"), method = "isBodyVisible", cancellable = true)
    private <T extends LivingEntity> void invisible(T pLivingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (!(pLivingEntity instanceof Player player)) return;
        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null) return;
        Dash dash = abilityManager.takeAbility(Dash.class);
        if (dash != null && dash.isDashing() && dash.getLevel() == 2) cir.setReturnValue(false);
    }
    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    private void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        current = pEntity;
    }
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    private void cancelRender(M instance, PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, float r, float g, float b, float a) {
        boolean render = true;
        DataManager dataManager = DataGetter.getUnwrap(current);
        if (dataManager != null) {
            EvasionData evasionData = dataManager.takeData(EvasionData.class);
            if (evasionData != null && evasionData.getIFrames() > 0) render = false;
        }
        AbilityManager abilityManager = current instanceof Player player ? AbilityGetter.getUnwrap(player) : null;
        if (abilityManager != null) {
            Dash dash = abilityManager.takeAbility(Dash.class);
            if (dash != null && dash.getLevel() > 2) render = false;
        }
        if (render) instance.renderToBuffer(poseStack,vertexConsumer,i,j,r,g,b,a);
    }
}
