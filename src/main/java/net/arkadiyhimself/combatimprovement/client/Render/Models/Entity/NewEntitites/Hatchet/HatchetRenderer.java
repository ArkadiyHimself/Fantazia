package net.arkadiyhimself.combatimprovement.client.Render.Models.Entity.NewEntitites.Hatchet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.Entities.HatchetEntity;
import net.arkadiyhimself.combatimprovement.Items.Weapons.Mixed.Hatchet;
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
    public static final ResourceLocation WOODEN = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/wooden_hatchet.png");
    public static final ResourceLocation STONE = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/stone_hatchet.png");
    public static final ResourceLocation GOLDEN = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/gold_hatchet.png");
    public static final ResourceLocation IRON = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/iron_hatchet.png");
    public static final ResourceLocation DIAMOND = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/diamond_hatchet.png");
    public static final ResourceLocation NETHERITE = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/netherite_hatchet.png");

    private final HatchetModel model;
    public HatchetRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new HatchetModel(pContext.bakeLayer(HatchetModel.LAYER_LOCATION));
    }
    @Override
    public void render(HatchetEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pMatrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot()) + 90.0F));
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(pBuffer, this.model.renderType(this.getTextureLocation(pEntity)), false, false);
        this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(HatchetEntity pEntity) {
        if (pEntity.getEntityData().get(HatchetEntity.STACK).getItem() instanceof Hatchet item) {
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
