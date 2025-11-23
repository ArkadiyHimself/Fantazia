package net.arkadiyhimself.fantazia.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.entity.skong.Pimpillo;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PimpilloRenderer extends EntityRenderer<Pimpillo> {

    private final PimpilloModel model;

    public static final ModelLayerLocation PIMPILLO_LAYER =
            new ModelLayerLocation(Fantazia.location("pimpillo"), "main");

    public PimpilloRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PimpilloModel(context.bakeLayer(PIMPILLO_LAYER));
    }

    @Override
    public void render(@NotNull Pimpillo pimpillo, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        VertexConsumer vertexconsumer = bufferSource.getBuffer(model.renderType(getTextureLocation(pimpillo)));
        poseStack.mulPose(Axis.ZN.rotationDegrees(180f));
        int life = pimpillo.getLife();
        if (life < 15) {
            int i = 15 - life;
            float expand = 1f + 0.075f * i + partialTick * 0.075f;
            poseStack.scale(expand, expand, expand);
        }
        model.renderToBuffer(poseStack, vertexconsumer, packedLight, whiteOverlay(pimpillo), 12785985);

        poseStack.popPose();
        super.render(pimpillo, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    public int whiteOverlay(Pimpillo pimpillo) {
        int life = pimpillo.getLife();
        if (life >= 15) return OverlayTexture.NO_OVERLAY;
        float progress = 1f - ((float) life / 15f);
        int u = OverlayTexture.u(progress);
        return OverlayTexture.pack(u, 10);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Pimpillo pimpillo) {
        return Fantazia.location("textures/entity/pimpillo.png");
    }
}
