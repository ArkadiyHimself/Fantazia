package net.arkadiyhimself.fantazia.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.entity.skong.ThrownPin;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ThrownPinRenderer extends EntityRenderer<ThrownPin> {

    public static final ModelLayerLocation THROWN_PIN_LAYER =
            new ModelLayerLocation(Fantazia.location("thrown_pin"), "main");

    private final ThrownPinModel model;

    public ThrownPinRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ThrownPinModel(context.bakeLayer(THROWN_PIN_LAYER));
    }

    @Override
    public void render(@NotNull ThrownPin thrownPin, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        VertexConsumer vertexconsumer = bufferSource.getBuffer(model.renderType(getTextureLocation(thrownPin)));
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, thrownPin.yRotO, thrownPin.getYRot()) - 0.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(Mth.lerp(partialTick, thrownPin.xRotO, thrownPin.getXRot())));

        model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 654311423);

        poseStack.popPose();
        super.render(thrownPin, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ThrownPin thrownPin) {
        return Fantazia.location("textures/entity/thrown_pin.png");
    }
}
