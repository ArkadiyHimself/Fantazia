package net.arkadiyhimself.fantazia.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.common.entity.BlockFly;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    @Shadow @Final protected List<RenderLayer<T, M>> layers;

    protected MixinLivingEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }
    @Unique
    @Nullable
    private T fantazia$current = null;
    @Inject(at = @At("HEAD"), method = "getOverlayCoords", cancellable = true)
    private static void preventTurningRed(LivingEntity pLivingEntity, float pU, CallbackInfoReturnable<Integer> cir) {
        if (pLivingEntity instanceof BlockFly) cir.setReturnValue(655360);
        if (pLivingEntity.deathTime > 0) return;
        if (!LivingEffectHelper.hurtRedColor(pLivingEntity)) cir.setReturnValue(655360);
    }
    @Inject(at = @At("HEAD"), method = "isBodyVisible", cancellable = true)
    private void invisible(T pLivingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (!(pLivingEntity instanceof Player player)) return;
        DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
        if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() == 2) cir.setReturnValue(false);
    }
    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    private void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        fantazia$current = pEntity;
    }
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"), method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    private void cancelRender(M instance, PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
        boolean render = true;
        if (fantazia$current != null) {
            EvasionHolder evasionHolder = LivingDataHelper.takeHolder(fantazia$current, EvasionHolder.class);
            if (evasionHolder != null && evasionHolder.getIFrames() > 0) render = false;
        }

        if (fantazia$current instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.getLevel() > 2 && dashHolder.isDashing()) render = false;
        }

        if (render) instance.renderToBuffer(var1,var2,var3,var4,var5);
    }
}
