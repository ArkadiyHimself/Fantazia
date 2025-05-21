package net.arkadiyhimself.fantazia.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.render.FTZRenderTypes;
import net.arkadiyhimself.fantazia.client.render.layers.AbsoluteBarrier;
import net.arkadiyhimself.fantazia.client.renderers.item.FantazicItemRenderer;
import net.arkadiyhimself.fantazia.entities.DashStone;
import net.arkadiyhimself.fantazia.registries.FTZDataComponentTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.renderer.entity.ItemRenderer.getFoilBuffer;

@OnlyIn(Dist.CLIENT)
public class DashStoneRenderer extends EntityRenderer<DashStone> {

    private final ItemRenderer renderer;

    public DashStoneRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.renderer = pContext.getItemRenderer();
    }

    @Override
    public void render(@NotNull DashStone pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.getEntityData().get(DashStone.OWNER) == -1) return;
        pPoseStack.pushPose();

        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.yRot0, pEntity.yRot1)));
        pPoseStack.scale(0.5f,0.5f,0.5f);
        pPoseStack.translate(-0.5F, -0.5F, -0.5F);

        ItemStack stack = pEntity.getDashstone();
        boolean unpickable = pEntity.getEntityData().get(DashStone.UNPICKABLE);

        BakedModel model = renderer.getItemModelShaper().getModelManager().getModel(getModel(pEntity));

        for (BakedModel model1 : model.getRenderPasses(stack, false)) {
            for (RenderType renderType : model1.getRenderTypes(stack, false)) {

                PoseStack.Pose pose = pPoseStack.last().copy();
                VertexConsumer consumer;
                if (false) consumer = getBarrierVertex(pBuffer, renderType, pose);
                else consumer = getFoilBuffer(pBuffer, renderType, true, true);;

                renderer.renderModelLists(model1, stack, pPackedLight, pPackedLight, pPoseStack, consumer);
            }
        }

        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DashStone pEntity) {
        return Fantazia.res("");
    }

    private static ModelResourceLocation getModel(DashStone entity) {
        ItemStack item = entity.getDashstone();
        Integer dashLevel = item.get(FTZDataComponentTypes.DASH_LEVEL);
        if (dashLevel == null || dashLevel <= 2) return FantazicItemRenderer.DASHSTONE2;
        else return FantazicItemRenderer.DASHSTONE3;
    }

    public static VertexConsumer getBarrierVertex(MultiBufferSource bufferSource, RenderType renderType, PoseStack.Pose pose) {
        return VertexMultiConsumer.create(
                new SheetedDecalTextureGenerator(bufferSource.getBuffer(FTZRenderTypes.customGlint(AbsoluteBarrier.BARRIER_LAYER)), pose, 0.0078125F), bufferSource.getBuffer(renderType)
        );
    }
}
