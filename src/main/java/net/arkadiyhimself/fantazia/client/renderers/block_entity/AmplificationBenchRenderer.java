package net.arkadiyhimself.fantazia.client.renderers.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.entities.AmplificationBenchBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

public class AmplificationBenchRenderer implements BlockEntityRenderer<AmplificationBenchBlockEntity> {

    public AmplificationBenchRenderer(BlockEntityRendererProvider.Context context) {}

    public void render(@NotNull AmplificationBenchBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

    }
}
