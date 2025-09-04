package net.arkadiyhimself.fantazia.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.holders.StuckHatchetHolder;
import net.arkadiyhimself.fantazia.common.entity.ThrownHatchet;
import net.arkadiyhimself.fantazia.common.item.weapons.Range.HatchetItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HatchetLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private final EntityRenderDispatcher dispatcher;

    public HatchetLayer(EntityRendererProvider.Context context, LivingEntityRenderer<T, M> renderer) {
        super(renderer);
        this.dispatcher = context.getEntityRenderDispatcher();
    }

    protected void renderStuckItem(ThrownHatchet hatchet, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float x, float y, float z, float partialTick) {
        float f = Mth.sqrt(x * x + z * z);
        hatchet.setYRot((float)(Math.atan2(x, z) * 180.0 / 3.1415927410125732));
        hatchet.setXRot((float)(Math.atan2(y, f) * 180.0 / 3.1415927410125732));
        hatchet.yRotO = hatchet.getYRot();
        hatchet.xRotO = hatchet.getXRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(0));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        this.dispatcher.render(hatchet, 0.0, 0.0, 0.0, 0.0F, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public void render(
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight, @NotNull T entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        RandomSource randomsource = RandomSource.create(entity.getId());
        M model = getParentModel();
        if (!(model instanceof HumanoidModel<?> humanoidModel)) return;
        StuckHatchetHolder hatchetHolder = LivingDataHelper.takeHolder(entity, StuckHatchetHolder.class);
        if (hatchetHolder == null) return;
        ItemStack stack = hatchetHolder.getStack();
        if (!(stack.getItem() instanceof HatchetItem hatchetItem)) return;
        ThrownHatchet hatchet = hatchetItem.makeThrownHatchet(entity.level(), entity.position());
        poseStack.pushPose();
        ModelPart modelpart = humanoidModel.head;
        ModelPart.Cube modelpart$cube = modelpart.getRandomCube(randomsource);
        modelpart.translateAndRotate(poseStack);
        float f = 0.5f;
        float f1 = 0.5f;
        float f2 = 0.5f;
        float f3 = Mth.lerp(f, modelpart$cube.minX, modelpart$cube.maxX) / 16.0F;
        float f4 = Mth.lerp(f1, modelpart$cube.minY, modelpart$cube.maxY) / 16.0F;
        float f5 = Mth.lerp(f2, modelpart$cube.minZ, modelpart$cube.maxZ) / 16.0F;
        poseStack.translate(f3, f4 - 0.425, f5);
        f = -1.0F * (f * 2.0F - 1.0F);
        f1 = -1.0F * (f1 * 2.0F - 1.0F);
        f2 = -1.0F * (f2 * 2.0F - 1.0F);
        this.renderStuckItem(hatchet, poseStack, buffer, packedLight, f, f1, f2, partialTicks);
        poseStack.popPose();
    }
}
