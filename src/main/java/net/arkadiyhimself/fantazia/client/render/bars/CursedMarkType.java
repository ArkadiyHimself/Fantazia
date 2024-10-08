package net.arkadiyhimself.fantazia.client.render.bars;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class CursedMarkType extends RenderStateShard {
    public static final ResourceLocation CURSE_MARK = Fantazia.res("textures/render_above/curse_warning.png");
    public static final RenderType CURSE_WARNING_TYPE = curseWarningType();
    public CursedMarkType(String pName, Runnable pSetupState, Runnable pClearState) {
        super(pName, pSetupState, pClearState);
    }
    private static RenderType curseWarningType() {
        RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new TextureStateShard(CURSE_MARK, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false);
        return createCurseMark(renderTypeState);
    }
    private static RenderType createCurseMark(RenderType.CompositeState glState) {
        return RenderType.create("curse_mark", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, glState);
    }
    public static void render(PoseStack poseStack, MultiBufferSource source, float iconHeight) {
        RenderSystem.setShaderTexture(0, CURSE_MARK);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        VertexConsumer consumer = source.getBuffer(CURSE_WARNING_TYPE);

        final int light = 0xF000F0;

        poseStack.pushPose();
        poseStack.translate(0, iconHeight, 0);
        Matrix4f matrix4f = poseStack.last().pose();

        consumer.addVertex(matrix4f, -1.0F, -1, 0).setColor(255, 255, 255, 255).setUv(0.0F, 0.0F).setLight(light).setNormal(poseStack.last(), 0.0F, -1.0F, 0.0F);
        consumer.addVertex(matrix4f, -1.0F, 1, 0).setColor(255, 255, 255, 255).setUv(0.0F, 1.0F).setLight(light).setNormal(poseStack.last(), 0.0F, -1.0F, 0.0F);
        consumer.addVertex(matrix4f, 1.0F, 1, 0).setColor(255, 255, 255, 255).setUv(1.0F, 1.0F).setLight(light).setNormal(poseStack.last(), 0.0F, -1.0F, 0.0F);
        consumer.addVertex(matrix4f, 1.0F, -1, 0).setColor(255, 255, 255, 255).setUv(1.0F, 0.0F).setLight(light).setNormal(poseStack.last(), 0.0F, -1.0F, 0.0F);

        MeshData meshData = builder.build();
        if (meshData != null) BufferUploader.drawWithShader(meshData);
        poseStack.popPose();
    }
}
