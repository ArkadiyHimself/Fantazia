package net.arkadiyhimself.fantazia.client.renderers.block_entity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.client.ClientEvents;
import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.arkadiyhimself.fantazia.common.entity.EngineeringTableBlockEntity;
import net.arkadiyhimself.fantazia.common.item.EngineeringTableBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class EngineeringTableRenderer implements BlockEntityRenderer<EngineeringTableBlockEntity> {

    private final EntityRenderDispatcher entityRenderDispatcher;
    private final Font font;

    public EngineeringTableRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getEntityRenderer();
        this.font = context.getFont();
    }

    @Override
    public void render(
            @NotNull EngineeringTableBlockEntity blockEntity,
            float v,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        if (blockEntity.getBlockState().getValue(EngineeringTableBlock.HALF) != DoubleBlockHalf.UPPER) return;
        Holder<Blueprint> blueprint = blockEntity.getBlueprint();
        if (blueprint == null) return;
        Direction direction = blockEntity.getBlockState().getValue(EngineeringTableBlock.FACING);
        if (blockEntity == ClientEvents.crossHairBlockEntity) {
            Vec3 initial = new Vec3( 0.5, 1.15, 0.5);
            Vec3i delta = direction.getOpposite().getNormal();
            initial = initial.add(delta.getX() * 0.8, 0, delta.getZ() * 0.8);
            renderBlueprintName(initial, blueprint.value().getNameComponent(), poseStack, bufferSource, packedLight);
        }
        poseStack.pushPose();
        poseStack.translate(0.5,0,0.5);

        if (direction == Direction.WEST) poseStack.mulPose(Axis.YN.rotationDegrees(90));
        else if (direction == Direction.NORTH) poseStack.mulPose(Axis.YN.rotationDegrees(180));
        else if (direction == Direction.EAST) poseStack.mulPose(Axis.YN.rotationDegrees(270));
        poseStack.translate(-0.5,0,-0.5);

        RenderType renderType = makeRenderType(blueprint.value().getBlueprintTexture());
        Matrix4f matrix4f = poseStack.last().pose();

        VertexConsumer consumer = bufferSource.getBuffer(renderType);
        float dY = 7f;
        float dZ = dY * (float) Math.cos(Math.PI / 8 * 3);
        float dX = 12f;
        float offset = 2.35f;
        float y0 = 3.5f / 16;
        float x0 = 2f / 16;
        float y1 = y0 + dY / 16;
        float x1 = x0 + dX / 16;
        float z1 = (offset) / 16;
        float z0 = (dZ + offset) / 16;

        consumer.addVertex(matrix4f, x0, y0, z0).setUv(0,1).setLight(packedLight).setColor(255, 255, 255, 255);
        consumer.addVertex(matrix4f, x1, y0, z0).setUv(1,1).setLight(packedLight).setColor(255, 255, 255, 255);
        consumer.addVertex(matrix4f, x1, y1, z1).setUv(1,0).setLight(packedLight).setColor(255, 255, 255, 255);
        consumer.addVertex(matrix4f, x0, y1, z1).setUv(0,0).setLight(packedLight).setColor(255, 255, 255, 255);
        poseStack.popPose();
    }

    private static RenderType makeRenderType(ResourceLocation texture) {
        RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .createCompositeState(false);
        return createRenderType(renderTypeState);
    }

    private static RenderType createRenderType(RenderType.CompositeState glState) {
        return RenderType.create("fantazia_engineering_table_blueprint",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, glState);
    }

    protected void renderBlueprintName(
            Vec3 vec3,
            Component displayName,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        poseStack.pushPose();
        poseStack.translate(vec3.x, vec3.y, vec3.z);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = poseStack.last().pose();
        float f = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int j = (int)(f * 255.0F) << 24;
        float f1 = (float)(-this.font.width(displayName) / 2);
        this.font.drawInBatch(
                displayName, f1,0,553648127, false, matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH, j, packedLight
        );
        font.drawInBatch(displayName, f1, 0, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();
    }

}
