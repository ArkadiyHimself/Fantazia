package net.arkadiyhimself.fantazia.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.mixin.TextureAtlasHolderAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class FantazicPaintingRenderer extends PaintingRenderer {

    private static final ResourceLocation BACK_TEXTURE_ATLAS_LOCATION = Fantazia.res("back");

    public FantazicPaintingRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Painting entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

        PaintingVariant paintingType = entityIn.getVariant().value();
        VertexConsumer iVertexBuilder = bufferIn.getBuffer(RenderType.entitySolid(this.getTextureLocation(entityIn)));
        PaintingTextureManager paintingSpriteUploader = Minecraft.getInstance().getPaintingTextures();
        renderPainting(matrixStackIn, iVertexBuilder, entityIn, paintingType.width(), paintingType.height(), paintingSpriteUploader.get(paintingType), ((TextureAtlasHolderAccessor) paintingSpriteUploader).textureAtlas().getSprite(BACK_TEXTURE_ATLAS_LOCATION));

        matrixStackIn.popPose();
    }

    private void renderPainting(PoseStack poseStack, VertexConsumer consumer, Painting painting, int width, int height, TextureAtlasSprite paintingSprite, TextureAtlasSprite backSprite) {
        PoseStack.Pose posestack$pose = poseStack.last();
        float f = (float)(-width) / 2.0F;
        float f1 = (float)(-height) / 2.0F;
        float f2 = 0.03125F;
        float f3 = backSprite.getU0();
        float f4 = backSprite.getU1();
        float f5 = backSprite.getV0();
        float f6 = backSprite.getV1();
        float f7 = backSprite.getU0();
        float f8 = backSprite.getU1();
        float f9 = backSprite.getV0();
        float f10 = backSprite.getV(0.0625F);
        float f11 = backSprite.getU0();
        float f12 = backSprite.getU(0.0625F);
        float f13 = backSprite.getV0();
        float f14 = backSprite.getV1();
        double d0 = (double)1.0F / (double)width;
        double d1 = (double)1.0F / (double)height;

        for(int i = 0; i < width; ++i) {
            for(int j = 0; j < height; ++j) {
                float f15 = f + (float)(i + 1);
                float f16 = f + (float)i;
                float f17 = f1 + (float)(j + 1);
                float f18 = f1 + (float)j;
                int k = painting.getBlockX();
                int l = Mth.floor(painting.getY() + (double)((f17 + f18) / 2.0F));
                int i1 = painting.getBlockZ();
                Direction direction = painting.getDirection();
                if (direction == Direction.NORTH) {
                    k = Mth.floor(painting.getX() + (double)((f15 + f16) / 2.0F));
                }

                if (direction == Direction.WEST) {
                    i1 = Mth.floor(painting.getZ() - (double)((f15 + f16) / 2.0F));
                }

                if (direction == Direction.SOUTH) {
                    k = Mth.floor(painting.getX() - (double)((f15 + f16) / 2.0F));
                }

                if (direction == Direction.EAST) {
                    i1 = Mth.floor(painting.getZ() + (double)((f15 + f16) / 2.0F));
                }

                int j1 = LevelRenderer.getLightColor(painting.level(), new BlockPos(k, l, i1));
                float f19 = paintingSprite.getU((float)(d0 * (double)(width - i)));
                float f20 = paintingSprite.getU((float)(d0 * (double)(width - (i + 1))));
                float f21 = paintingSprite.getV((float)(d1 * (double)(height - j)));
                float f22 = paintingSprite.getV((float)(d1 * (double)(height - (j + 1))));
                this.vertex(posestack$pose, consumer, f15, f18, f20, f21, -0.03125F, 0, 0, -1, j1);
                this.vertex(posestack$pose, consumer, f16, f18, f19, f21, -0.03125F, 0, 0, -1, j1);
                this.vertex(posestack$pose, consumer, f16, f17, f19, f22, -0.03125F, 0, 0, -1, j1);
                this.vertex(posestack$pose, consumer, f15, f17, f20, f22, -0.03125F, 0, 0, -1, j1);
                this.vertex(posestack$pose, consumer, f15, f17, f4, f5, 0.03125F, 0, 0, 1, j1);
                this.vertex(posestack$pose, consumer, f16, f17, f3, f5, 0.03125F, 0, 0, 1, j1);
                this.vertex(posestack$pose, consumer, f16, f18, f3, f6, 0.03125F, 0, 0, 1, j1);
                this.vertex(posestack$pose, consumer, f15, f18, f4, f6, 0.03125F, 0, 0, 1, j1);
                this.vertex(posestack$pose, consumer, f15, f17, f7, f9, -0.03125F, 0, 1, 0, j1);
                this.vertex(posestack$pose, consumer, f16, f17, f8, f9, -0.03125F, 0, 1, 0, j1);
                this.vertex(posestack$pose, consumer, f16, f17, f8, f10, 0.03125F, 0, 1, 0, j1);
                this.vertex(posestack$pose, consumer, f15, f17, f7, f10, 0.03125F, 0, 1, 0, j1);
                this.vertex(posestack$pose, consumer, f15, f18, f7, f9, 0.03125F, 0, -1, 0, j1);
                this.vertex(posestack$pose, consumer, f16, f18, f8, f9, 0.03125F, 0, -1, 0, j1);
                this.vertex(posestack$pose, consumer, f16, f18, f8, f10, -0.03125F, 0, -1, 0, j1);
                this.vertex(posestack$pose, consumer, f15, f18, f7, f10, -0.03125F, 0, -1, 0, j1);
                this.vertex(posestack$pose, consumer, f15, f17, f12, f13, 0.03125F, -1, 0, 0, j1);
                this.vertex(posestack$pose, consumer, f15, f18, f12, f14, 0.03125F, -1, 0, 0, j1);
                this.vertex(posestack$pose, consumer, f15, f18, f11, f14, -0.03125F, -1, 0, 0, j1);
                this.vertex(posestack$pose, consumer, f15, f17, f11, f13, -0.03125F, -1, 0, 0, j1);
                this.vertex(posestack$pose, consumer, f16, f17, f12, f13, -0.03125F, 1, 0, 0, j1);
                this.vertex(posestack$pose, consumer, f16, f18, f12, f14, -0.03125F, 1, 0, 0, j1);
                this.vertex(posestack$pose, consumer, f16, f18, f11, f14, 0.03125F, 1, 0, 0, j1);
                this.vertex(posestack$pose, consumer, f16, f17, f11, f13, 0.03125F, 1, 0, 0, j1);
            }
        }

    }

    private void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int packedLight) {
        consumer.addVertex(pose, x, y, z).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, (float)normalX, (float)normalY, (float)normalZ);
    }
}
