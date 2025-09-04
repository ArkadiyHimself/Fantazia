package net.arkadiyhimself.fantazia.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class WitherBarrierLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation WITHER_ARMOR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_armor.png");

    private final M entityModel;

    public WitherBarrierLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
        this.entityModel = renderer.getModel();
    }

    @Override
    public void render(
            @NotNull PoseStack pPoseStack,
            @NotNull MultiBufferSource pBuffer,
            int pPackedLight, T pLivingEntity,
            float pLimbSwing,
            float pLimbSwingAmount,
            float pPartialTick,
            float pAgeInTicks,
            float pNetHeadYaw,
            float pHeadPitch
    ) {
        if (pLivingEntity.isInvisible()) return;

        EvasionHolder evasionHolder = LivingDataHelper.takeHolder(pLivingEntity, EvasionHolder.class);
        if (evasionHolder != null && evasionHolder.getIFrames() > 0) return;

        if (!LivingEffectHelper.hasEffectSimple(pLivingEntity, FTZMobEffects.WITHERS_BARRIER.value())) return;

        if (pLivingEntity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() > 2) return;
        }

        float f = (float)pLivingEntity.tickCount + pPartialTick;
        entityModel.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
        this.getParentModel().copyPropertiesTo(entityModel);
        float pU = VisualHelper.layerOffset(f) % 1.0F;
        float pV = f * 0.01F % 1.0F;

        VertexConsumer pBufferBuffer = pBuffer.getBuffer(RenderType.energySwirl(WITHER_ARMOR_LOCATION, pU, pV));
        entityModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

        entityModel.renderToBuffer(pPoseStack, pBufferBuffer, pPackedLight, OverlayTexture.NO_OVERLAY, -8355712);
    }
}
