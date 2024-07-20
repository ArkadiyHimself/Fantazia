package net.arkadiyhimself.fantazia.client.Models.Entity.NewEntitites.Hatchet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.Entities.HatchetEntity;
import net.arkadiyhimself.fantazia.Items.Weapons.Range.HatchetItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import org.jetbrains.annotations.NotNull;

public class HatchetRenderer extends EntityRenderer<HatchetEntity> {
    public static final ResourceLocation WOODEN = Fantazia.res("textures/entity/hatchet/wooden_hatchet.png");
    public static final ResourceLocation STONE = Fantazia.res("textures/entity/hatchet/stone_hatchet.png");
    public static final ResourceLocation GOLDEN = Fantazia.res("textures/entity/hatchet/gold_hatchet.png");
    public static final ResourceLocation IRON = Fantazia.res("textures/entity/hatchet/iron_hatchet.png");
    public static final ResourceLocation DIAMOND = Fantazia.res("textures/entity/hatchet/diamond_hatchet.png");
    public static final ResourceLocation NETHERITE = Fantazia.res("textures/entity/hatchet/netherite_hatchet.png");

    private final HatchetModel model;
    public HatchetRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new HatchetModel(pContext.bakeLayer(HatchetModel.LAYER_LOCATION));
    }
    @Override
    public void render(HatchetEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pMatrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.getEntityData().get(HatchetEntity.VISUAL_ROT0), pEntity.getEntityData().get(HatchetEntity.VISUAL_ROT1))));
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(pBuffer, this.model.renderType(this.getTextureLocation(pEntity)), false, pEntity.isFoil());
        this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(HatchetEntity pEntity) {
        if (pEntity.getEntityData().get(HatchetEntity.STACK).getItem() instanceof HatchetItem item) {
            Tier tier = item.getTier();
            ResourceLocation res;
            if (tier == Tiers.STONE) {
                res = STONE;
            } else if (tier == Tiers.IRON) {
                res = IRON;
            } else if (tier == Tiers.GOLD) {
                res = GOLDEN;
            } else if (tier == Tiers.DIAMOND) {
                res = DIAMOND;
            } else if (tier == Tiers.NETHERITE) {
                res = NETHERITE;
            } else {
                return WOODEN;
            }
            return res;
        }
        return WOODEN;
    }
}
