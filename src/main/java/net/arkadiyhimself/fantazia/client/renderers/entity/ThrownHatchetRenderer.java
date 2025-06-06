package net.arkadiyhimself.fantazia.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ThrownHatchetRenderer extends EntityRenderer<ThrownHatchet> {
    public static final ModelResourceLocation WOODEN = Fantazia.modelRes("item/wooden_hatchet");
    public static final ModelResourceLocation STONE = Fantazia.modelRes("item/stone_hatchet");
    public static final ModelResourceLocation GOLDEN = Fantazia.modelRes("item/golden_hatchet");
    public static final ModelResourceLocation IRON = Fantazia.modelRes("item/iron_hatchet");
    public static final ModelResourceLocation DIAMOND = Fantazia.modelRes("item/diamond_hatchet");
    public static final ModelResourceLocation NETHERITE = Fantazia.modelRes("item/netherite_hatchet");

    public ThrownHatchetRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(ThrownHatchet pEntity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.getEntityData().get(ThrownHatchet.VISUAL_ROT0), pEntity.getEntityData().get(ThrownHatchet.VISUAL_ROT1))));

        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(hatchetModel(pEntity));
        Minecraft.getInstance().getItemRenderer().render(pEntity.getPickupItem(), ItemDisplayContext.GUI, false, poseStack, pBuffer, pPackedLight, pPackedLight, model);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ThrownHatchet pEntity) {
        return Fantazia.res("textures/item/weapons/wooden_hatchet");
    }

    private static ModelResourceLocation hatchetModel(ThrownHatchet hatchet) {
        if (!(hatchet.getPickupItem().getItem() instanceof HatchetItem hatchetItem)) return WOODEN;
        Tier tier = hatchetItem.getTier();
        if (tier == Tiers.STONE) return STONE;
        else if (tier == Tiers.IRON) return IRON;
        else if (tier == Tiers.GOLD) return GOLDEN;
        else if (tier == Tiers.DIAMOND) return DIAMOND;
        else if (tier == Tiers.NETHERITE) return NETHERITE;
        else return WOODEN;
    }
}

