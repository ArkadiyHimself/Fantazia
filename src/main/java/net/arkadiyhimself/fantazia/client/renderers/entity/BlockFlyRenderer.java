package net.arkadiyhimself.fantazia.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.entity.BlockFly;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BlockFlyRenderer extends MobRenderer<BlockFly, BlockFlyModel> {

    public static final ModelLayerLocation BLOCK_FLY_LAYER = new ModelLayerLocation(Fantazia.location("block_fly"), "main");

    private static final ResourceLocation DYING = Fantazia.location("textures/entity/block_fly/dying.png");
    private static final ResourceLocation BODY_FINE = Fantazia.location("textures/entity/block_fly/body_fine.png");
    private static final ResourceLocation BODY_DAMAGED = Fantazia.location("textures/entity/block_fly/body_damaged.png");
    private static final ResourceLocation BODY_MORIBUND = Fantazia.location("textures/entity/block_fly/body_moribund.png");
    private static final ResourceLocation EYES_REGULAR = Fantazia.location("textures/entity/block_fly/eyes_regular.png");
    private static final ResourceLocation EYES_ANGRY = Fantazia.location("textures/entity/block_fly/eyes_angry.png");

    public BlockFlyRenderer(EntityRendererProvider.Context context) {
        super(context, new BlockFlyModel(context.bakeLayer(BLOCK_FLY_LAYER)), 0.3F);
        this.addLayer(new Eyes(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BlockFly blockFly) {
        if (blockFly.isDeadOrDying()) return DYING;
        return switch (blockFly.getState()) {
            case FINE -> BODY_FINE;
            case DAMAGED -> BODY_DAMAGED;
            case MORIBUND -> BODY_MORIBUND;
        };
    }

    @Override
    protected float getFlipDegrees(@NotNull BlockFly livingEntity) {
        return 0;
    }

    @Override
    public void render(@NotNull BlockFly blockFly, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(blockFly, entityYaw, partialTicks, poseStack, buffer, packedLight);
        blockFly.visualY = (this.model.root().getChild("body").y - 13d) / 16;
    }

    public static class Eyes extends RenderLayer<BlockFly, BlockFlyModel> {

        private static final RenderType REGULAR =
                RenderType.entityTranslucent(EYES_REGULAR);
        private static final RenderType ANGRY =
                RenderType.entityTranslucent(EYES_ANGRY);

        public Eyes(RenderLayerParent<BlockFly, BlockFlyModel> renderer) {
            super(renderer);
        }

        @Override
        public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull BlockFly blockFly, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (blockFly.isDeadOrDying()) return;
            BlockFly.WellBeing state = blockFly.getState();
            if (state == BlockFly.WellBeing.DAMAGED && RandomUtil.nextDouble() <= 0.15) return;
            if (state == BlockFly.WellBeing.MORIBUND && RandomUtil.nextDouble() <= 0.35) return;

            VertexConsumer vertexconsumer = buffer.getBuffer(blockFly.isAngry() ? ANGRY : REGULAR);
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}
