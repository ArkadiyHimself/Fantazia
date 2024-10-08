package net.arkadiyhimself.fantazia.client.render.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.BarrierEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.FuryEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
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

public class BarrierLayer {
    public static final ResourceLocation BARRIER_LAYER = Fantazia.res("textures/entity_layers/barrier/barrier_armor.png");
    public static final ResourceLocation BARRIER_BG = Fantazia.res("textures/entity_layers/barrier/barrier_bg.png");
    public static final ResourceLocation BARRIER_LAYER_FURY = Fantazia.res("textures/entity_layers/barrier/barrier_armor_fury.png");
    public static final ResourceLocation BARRIER_BG_FURY = Fantazia.res("textures/entity_layers/barrier/barrier_bg_fury.png");
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
    public static class LayerBarrier<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T,M> {
        private final RenderLayerParent<T, M> renderer;
        public LayerBarrier(LivingEntityRenderer<T, M> renderer) {
            super(renderer);
            this.renderer = renderer;
        }
        @Override
        public void render(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            EvasionHolder evasionHolder = LivingDataGetter.takeHolder(pLivingEntity, EvasionHolder.class);
            if (evasionHolder != null && evasionHolder.getIFrames() > 0) return;

            BarrierEffect barrierEffect = LivingEffectGetter.takeHolder(pLivingEntity, BarrierEffect.class);
            if (barrierEffect == null || !barrierEffect.hasBarrier()) return;

            if (pLivingEntity instanceof Player player) {
                DashHolder dashHolder = PlayerAbilityGetter.takeHolder(player, DashHolder.class);
                if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() > 2) return;
            }

            FuryEffect furyEffect = LivingEffectGetter.takeHolder(pLivingEntity, FuryEffect.class);
            boolean furious = furyEffect != null && furyEffect.isFurious();

            float f = (float)pLivingEntity.tickCount + pPartialTick;
            M entityModel = this.renderer.getModel();
            entityModel.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
            this.getParentModel().copyPropertiesTo(entityModel);
            float pU = xOffset(f) % 1.0F;
            float pV = f * 0.01F % 1.0F;

            VertexConsumer pBufferBuffer = pBuffer.getBuffer(RenderType.energySwirl(furious ? BARRIER_LAYER_FURY : BARRIER_LAYER, pU, pV));
            entityModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

            int color = FastColor.ARGB32.colorFromFloat(0.1019999F + (1 - barrierEffect.getColor()) * 0.6F, furious ? 1f : 0f, 0.6F, barrierEffect.getColor() < 1F ? 0.6F : 1F);
            entityModel.renderToBuffer(pPoseStack, pBufferBuffer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
            VertexConsumer BGvertex = pBuffer.getBuffer(RenderType.entityTranslucent(furious ? BARRIER_BG_FURY : BARRIER_BG));
            int color1 = FastColor.ARGB32.colorFromFloat(0.05f, furious ? 1f : 0f, 1F, 1F);
            entityModel.renderToBuffer(pPoseStack, BGvertex, pPackedLight, OverlayTexture.NO_OVERLAY, color1);
        }
        public static float xOffset(float pTickCount) {
            return pTickCount * 0.01F;
        }
    }

}
