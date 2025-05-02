package net.arkadiyhimself.fantazia.client.render.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.basis_attachments.ReflectLayerRenderHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class MysticMirror {
    public static final ResourceLocation MIRROR_LAYER = Fantazia.res("textures/entity_layers/mystic_mirror/mystic_mirror.png");
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENERGY_SWIRL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEnergySwirlShader);
    protected static final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);
    protected static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    protected static final RenderStateShard.OverlayStateShard OVERLAY = new RenderStateShard.OverlayStateShard(true);
    public static class LayerMirror<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T,M> {
        private final RenderLayerParent<T, M> renderer;
        public LayerMirror(LivingEntityRenderer<T, M> renderer) {
            super(renderer);
            this.renderer = renderer;
        }
        @Override
        public void render(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            if (pLivingEntity.isInvisible()) return;

            if (!(pLivingEntity instanceof Player player)) return;

            DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() > 2) return;

            EvasionHolder evasionHolder = LivingDataHelper.takeHolder(pLivingEntity, EvasionHolder.class);
            if (evasionHolder != null && evasionHolder.getIFrames() > 0) return;

            ReflectLayerRenderHolder values = pLivingEntity.getData(FTZAttachmentTypes.REFLECT_RENDER_VALUES);
            if (!values.showLayer) return;

            pPoseStack.pushPose();
            float scale = values.layerSize;
            pPoseStack.scale(scale, scale, scale);
            float f = (float)pLivingEntity.tickCount + pPartialTick;
            M entityModel = renderer.getModel();
            entityModel.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
            this.getParentModel().copyPropertiesTo(entityModel);
            float pU = xOffset(f) % 1.0F;
            float pV = f * 0.01F % 1.0F;
            VertexConsumer pBufferBuffer = pBuffer.getBuffer(RenderType.create("mystic_mirror", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(MIRROR_LAYER, false, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(pU, pV)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false)));
            VertexConsumer buffer = pBuffer.getBuffer(RenderType.entityTranslucent(MIRROR_LAYER));
            buffer.setUv1(0, 0);
            buffer.setUv2(64, 64);
            entityModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

            int color = FastColor.ARGB32.colorFromFloat(values.layerTransparency, values.layerTransparency, values.layerTransparency,0.575f * values.layerTransparency);
            entityModel.renderToBuffer(pPoseStack, buffer, pPackedLight, OverlayTexture.NO_OVERLAY, color);

            pPoseStack.popPose();
        }
        public static float xOffset(float pTickCount) {
            return pTickCount * 0.01F;
        }
    }

}
