package net.arkadiyhimself.fantazia.client.render.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.LayeredBarrierEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
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

public class LayeredBarrierLayer {
    public static final ResourceLocation BARRIER_LAYER = Fantazia.res("textures/entity_layers/layered_barrier/barrier_armor.png");
    public static final ResourceLocation BARRIER_BG = Fantazia.res("textures/entity_layers/layered_barrier/barrier_bg.png");
    public static final ResourceLocation BARRIER_LAYER_FURY = Fantazia.res("textures/entity_layers/layered_barrier/barrier_armor_fury.png");
    public static final ResourceLocation BARRIER_BG_FURY = Fantazia.res("textures/entity_layers/layered_barrier/barrier_bg_fury.png");
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
            if (pLivingEntity.isInvisible()) return;

            EvasionHolder evasionHolder = LivingDataHelper.takeHolder(pLivingEntity, EvasionHolder.class);
            if (evasionHolder != null && evasionHolder.getIFrames() > 0) return;

            LayeredBarrierEffectHolder layeredBarrierEffect = LivingEffectHelper.takeHolder(pLivingEntity, LayeredBarrierEffectHolder.class);
            if (layeredBarrierEffect == null || !layeredBarrierEffect.hasBarrier()) return;

            if (pLivingEntity instanceof Player player) {
                DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
                if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() > 2) return;
            }

            boolean furious = LivingEffectHelper.hasEffect(pLivingEntity, FTZMobEffects.FURY.value());

            float f = (float)pLivingEntity.tickCount + pPartialTick;
            M entityModel = this.renderer.getModel();
            entityModel.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
            this.getParentModel().copyPropertiesTo(entityModel);
            float pU = xOffset(f) % 1.0F;
            float pV = f * 0.01F % 1.0F;

            VertexConsumer pBufferBuffer = pBuffer.getBuffer(RenderType.energySwirl(furious ? BARRIER_LAYER_FURY : BARRIER_LAYER, pU, pV));
            entityModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

            int color = FastColor.ARGB32.colorFromFloat(0.125f, 1f, furious ? 0.6f : 1f, furious ? 0.6f : 1f);
            entityModel.renderToBuffer(pPoseStack, pBufferBuffer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
            VertexConsumer BGvertex = pBuffer.getBuffer(RenderType.entityTranslucent(furious ? BARRIER_BG_FURY : BARRIER_BG));
            entityModel.renderToBuffer(pPoseStack, BGvertex, pPackedLight, OverlayTexture.NO_OVERLAY, color);
        }
        public static float xOffset(float pTickCount) {
            return pTickCount * 0.01F;
        }
    }

}
