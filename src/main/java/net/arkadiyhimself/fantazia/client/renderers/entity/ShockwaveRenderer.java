package net.arkadiyhimself.fantazia.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.entities.ShockwaveEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ShockwaveRenderer extends EntityRenderer<ShockwaveEntity> {

    private static final ResourceLocation REGULAR1 = Fantazia.res("textures/entity/shockwave/regular1.png");
    private static final ResourceLocation REGULAR2 = Fantazia.res("textures/entity/shockwave/regular2.png");
    private static final ResourceLocation REGULAR3 = Fantazia.res("textures/entity/shockwave/regular3.png");
    private static final ResourceLocation REGULAR4 = Fantazia.res("textures/entity/shockwave/regular4.png");
    private static final ResourceLocation REGULAR5 = Fantazia.res("textures/entity/shockwave/regular5.png");

    private static final ResourceLocation FURY1 = Fantazia.res("textures/entity/shockwave/fury1.png");
    private static final ResourceLocation FURY2 = Fantazia.res("textures/entity/shockwave/fury2.png");
    private static final ResourceLocation FURY3 = Fantazia.res("textures/entity/shockwave/fury3.png");
    private static final ResourceLocation FURY4 = Fantazia.res("textures/entity/shockwave/fury4.png");
    private static final ResourceLocation FURY5 = Fantazia.res("textures/entity/shockwave/fury5.png");

    private static RenderType createRenderType(RenderType.CompositeState glState) {
        return RenderType.create("stun_bar", com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, glState);
    }

    private RenderType renderType(ShockwaveEntity entity) {
        RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(getTextureLocation(entity), false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(false);
        return createRenderType(renderTypeState);
    }

    public ShockwaveRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull ShockwaveEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        final int light = 0xF000F0;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType(entity));

        poseStack.mulPose(Axis.YN.rotationDegrees(entity.getYRot() + 90));

        int alpha = (int) (255 * entity.lifePercentage());
        vertexConsumer.addVertex(poseStack.last().pose(),-1,0,-1).setColor(255,255,255, alpha).setUv(0,0).setLight(light);
        vertexConsumer.addVertex(poseStack.last().pose(),-1,0,1).setColor(255,255,255, alpha).setUv(0,1).setLight(light);
        vertexConsumer.addVertex(poseStack.last().pose(),1,0,1).setColor(255,255,255, alpha).setUv(1,1).setLight(light);
        vertexConsumer.addVertex(poseStack.last().pose(),1,0,-1).setColor(255,255,255, alpha).setUv(1,0).setLight(light);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ShockwaveEntity entity) {
        float lifePer = entity.lifePercentage();
        boolean fury = entity.furious();
        if (lifePer >= 0.8f) return fury ? FURY5 : REGULAR5;
        else if (lifePer >= 0.6f) return fury ? FURY4 : REGULAR4;
        else if (lifePer >= 0.4f) return fury ? FURY3 : REGULAR3;
        else if (lifePer >= 0.2f) return fury ? FURY2 : REGULAR2;
        else return fury ? FURY1 : REGULAR1;
    }
}
