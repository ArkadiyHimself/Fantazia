package net.arkadiyhimself.fantazia.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.entities.magic_projectile.SimpleChasingProjectile;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SimpleChasingProjectileRenderer extends EntityRenderer<SimpleChasingProjectile> {

    public static final ModelLayerLocation SIMPLE_CHASING_PROJECTILE_LAYER = new ModelLayerLocation(Fantazia.res("simple_chasing_projectile"), "main");
    private final SimpleChasingProjectileModel model;

    public SimpleChasingProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SimpleChasingProjectileModel(context.bakeLayer(SIMPLE_CHASING_PROJECTILE_LAYER));
    }

    @Override
    public void render(@NotNull SimpleChasingProjectile pEntity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.0625, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, pEntity.getEntityData().get(SimpleChasingProjectile.ROT_X0), pEntity.getEntityData().get(SimpleChasingProjectile.ROT_X1))));
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, pEntity.getEntityData().get(SimpleChasingProjectile.ROT_Y0), pEntity.getEntityData().get(SimpleChasingProjectile.ROT_Y1))));

        VertexConsumer vertexconsumer = bufferSource.getBuffer(model.renderType(getTextureLocation(pEntity)));
        model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, pEntity.getColor());

        poseStack.popPose();
        super.render(pEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SimpleChasingProjectile simpleChasingProjectile) {
        return Fantazia.res("textures/entity/simple_chasing_projectile.png");
    }
}
