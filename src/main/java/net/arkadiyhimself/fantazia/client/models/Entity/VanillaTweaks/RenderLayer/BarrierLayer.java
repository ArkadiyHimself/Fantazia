package net.arkadiyhimself.fantazia.client.models.Entity.VanillaTweaks.RenderLayer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects.BarrierEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;

public class BarrierLayer {
    public static final ResourceLocation BARRIER_LAYER = Fantazia.res("textures/entity_layers/barrier/barrier_armor.png");
    public static final ResourceLocation BARRIER_BG = Fantazia.res("textures/entity_layers/barrier/barrier_bg.png");
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
        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            EffectManager effectManager = EffectGetter.getUnwrap(pLivingEntity);
            if (effectManager == null) return;
            BarrierEffect barrierEffect = effectManager.takeEffect(BarrierEffect.class);
            if (barrierEffect == null || !barrierEffect.hasBarrier()) return;
            float f = (float)pLivingEntity.tickCount + pPartialTick;
            EntityModel<LivingEntity> entityModel = (EntityModel<LivingEntity>) this.renderer.getModel();
            entityModel.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
            this.getParentModel().copyPropertiesTo((EntityModel<T>) entityModel);
            float pU = xOffset(f) % 1.0F;
            float pV = f * 0.01F % 1.0F;
            VertexConsumer pBufferBuffer = pBuffer.getBuffer(RenderType.create("barrier", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                    false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(BARRIER_LAYER, false, false)).
                            setTexturingState(new RenderStateShard.OffsetTexturingStateShard(pU, pV)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false)));
            entityModel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            entityModel.renderToBuffer(pPoseStack, pBufferBuffer, pPackedLight, OverlayTexture.NO_OVERLAY, 0F, 0.6F, barrierEffect.getColor() < 1F ? 0.6F : 1F,0.1019999F + (1 - barrierEffect.getColor()) * 0.6F);
            VertexConsumer BGvertex = pBuffer.getBuffer(RenderType.entityTranslucent(BARRIER_BG));
            entityModel.renderToBuffer(pPoseStack, BGvertex, pPackedLight, OverlayTexture.NO_OVERLAY, barrierEffect.getColor(), 1F, 1F, 0.05F);
        }
        public static float xOffset(float pTickCount) {
            return pTickCount * 0.01F;
        }
    }
    public static void renderArmFirstPersonBarrier(PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, float equippedProgress, float swingProgress, float pPartialTick) {
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher renderManager = mc.getEntityRenderDispatcher();
        boolean flag = !(Minecraft.getInstance().options.mainHand().get() == HumanoidArm.LEFT);
        float f = flag ? 1.0F : -1.0F;
        float f1 = (float) Math.sqrt(swingProgress);
        float f2 = (float) (-0.3F * Math.sin(f1 * (float)Math.PI));
        float f3 = (float) (0.4F * Math.sin(f1 * ((float)Math.PI * 2F)));
        float f4 = (float) (-0.4F * Math.sin(swingProgress * (float)Math.PI));
        poseStack.translate(f * (f2 + 0.64000005F), f3 + -0.6F + equippedProgress * -0.6F, f4 + -0.71999997F);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 45.0F));
        float f5 = (float) Math.sin(swingProgress * swingProgress * (float)Math.PI);
        float f6 = (float) Math.sin(f1 * (float)Math.PI);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * f6 * 70.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(f * f5 * -20.0F));
        AbstractClientPlayer player = mc.player;
        mc.getTextureManager().bindForSetup(player.getSkinTextureLocation());
        poseStack.translate(f * -1.0F, 3.6F, 3.5D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(f * 120.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(200.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(f * -135.0F));
        poseStack.translate(f * 5.6F, 0.0D, 0.0D);
        PlayerRenderer playerrenderer = (PlayerRenderer) renderManager.getRenderer(player);
        if (flag) {
            playerrenderer.renderRightHand(poseStack, bufferIn, combinedLightIn, player);
            poseStack.scale(1.02f, 1.02f, 1.02f);
            renderRightArm(poseStack, bufferIn, combinedLightIn, player, playerrenderer.getModel(), pPartialTick);
        } else {
            playerrenderer.renderLeftHand(poseStack, bufferIn, combinedLightIn, player);
            poseStack.scale(1.02f, 1.02f, 1.02f);
            renderLeftArm(poseStack, bufferIn, combinedLightIn, player, playerrenderer.getModel(), pPartialTick);
        }
    }
    public static void renderRightArm(PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer playerIn, PlayerModel<AbstractClientPlayer> model, float pPartialTick) {
        renderItem(poseStack, bufferIn, combinedLightIn, playerIn, (model).rightSleeve, model, pPartialTick);
    }

    public static void renderLeftArm(PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer playerIn, PlayerModel<AbstractClientPlayer> model, float pPartialTick) {
        renderItem(poseStack, bufferIn, combinedLightIn, playerIn, (model).leftSleeve, model, pPartialTick);
    }

    private static void renderItem(PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer playerIn, ModelPart rendererArmwearIn, PlayerModel<AbstractClientPlayer> model, float pPartialTick) {
        setModelVisibilities(playerIn, model);
        model.attackTime = 0.0F;
        model.crouching = false;
        model.swimAmount = 0.0F;
        model.setupAnim(playerIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        rendererArmwearIn.xRot = 0.0F;
        float f = (float)playerIn.tickCount + pPartialTick;
        float pU = LayerBarrier.xOffset(f) % 1.0F;
        float pV = f * 0.01F % 1.0F;

        EffectManager effectManager = EffectGetter.getUnwrap(playerIn);
        if (effectManager == null) return;
        BarrierEffect barrierEffect = effectManager.takeEffect(BarrierEffect.class);
        if (barrierEffect == null) return;

        float color = barrierEffect.getColor();
        VertexConsumer pBufferBuffer = bufferIn.getBuffer(RenderType.create("barrier", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(BARRIER_LAYER, false, false)).
                        setTexturingState(new RenderStateShard.OffsetTexturingStateShard(pU, pV)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false)));
        //
        rendererArmwearIn.render(poseStack, pBufferBuffer, combinedLightIn, OverlayTexture.NO_OVERLAY,0, 0.6F, color < 1F ? 0.6F : 1F,0.1019999F + (1 - color) * 0.8F);
        VertexConsumer BGvertex = bufferIn.getBuffer(RenderType.entityTranslucent(BARRIER_BG));
        rendererArmwearIn.render(poseStack, BGvertex, combinedLightIn, OverlayTexture.NO_OVERLAY, color, 1F, 1F, 0.05F);
    }

    private static void setModelVisibilities(AbstractClientPlayer clientPlayer, PlayerModel<AbstractClientPlayer> playermodel) {
        if (clientPlayer.isSpectator()) {
            playermodel.setAllVisible(false);
            playermodel.head.visible = true;
            playermodel.hat.visible = true;
        } else {
            playermodel.setAllVisible(true);
            playermodel.hat.visible = clientPlayer.isModelPartShown(PlayerModelPart.HAT);
            playermodel.jacket.visible = clientPlayer.isModelPartShown(PlayerModelPart.JACKET);
            playermodel.leftSleeve.visible = clientPlayer.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playermodel.rightPants.visible = clientPlayer.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playermodel.leftSleeve.visible = clientPlayer.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playermodel.rightSleeve.visible = clientPlayer.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playermodel.crouching = clientPlayer.isCrouching();
        }
    }
}
