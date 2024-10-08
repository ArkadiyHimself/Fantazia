package net.arkadiyhimself.fantazia.client.models.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.entities.DashStoneEntity;
import net.arkadiyhimself.fantazia.items.casters.DashStoneItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DashStoneRenderer extends EntityRenderer<DashStoneEntity> {
    public static final ModelResourceLocation DASHSTONE2 = Fantazia.modelRes("item/dashstone2");
    public static final ModelResourceLocation DASHSTONE3 = Fantazia.modelRes("item/dashstone3");
    public DashStoneRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull DashStoneEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();

        pPoseStack.scale(0.5f,0.5f,0.5f);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.getEntityData().get(DashStoneEntity.VISUAL_ROT0), pEntity.getEntityData().get(DashStoneEntity.VISUAL_ROT1))));

        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(getModel(pEntity));
        Minecraft.getInstance().getItemRenderer().render(pEntity.getDashstone(), ItemDisplayContext.GUI, false, pPoseStack, pBuffer, pPackedLight, pPackedLight, model);

        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DashStoneEntity pEntity) {
        return Fantazia.res("");
    }

    private static ModelResourceLocation getModel(DashStoneEntity entity) {
        Item item = entity.getDashstone().getItem();
        if (!(item instanceof DashStoneItem dashStoneItem)) return DASHSTONE2;
        int level = dashStoneItem.level;
        if (level <= 2) return DASHSTONE2;
        else return DASHSTONE3;
    }
}
