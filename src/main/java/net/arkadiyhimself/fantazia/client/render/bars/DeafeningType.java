package net.arkadiyhimself.fantazia.client.render.bars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.DeafenedEffect;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DeafeningType extends RenderStateShard {
    public static ResourceLocation getFrame(int number, String type) {
        String num = String.valueOf(number);
        return new ResourceLocation(Fantazia.MODID, "textures/render_above/deafening/" + type + "_circle/" + type + "_circle" + num + ".png");
    }
    public static RenderType SOUND_WAVE_TYPE(int number, String type) { return soundWaveType(number, type); }
    public DeafeningType(String pName, Runnable pSetupState, Runnable pClearState) {super(pName, pSetupState, pClearState);}
    private static RenderType soundWaveType(int number, String type) {
        RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(DeafeningType.getFrame(number, type), false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false);
        return DeafeningType.createDeafWaves(renderTypeState);
    }
    private static RenderType createDeafWaves(RenderType.CompositeState glState) {
        return RenderType.create("deaf_circle", com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, glState);
    }
    public static void render(@NotNull DeafenedEffect deafenedEffect, PoseStack poseStack, MultiBufferSource buffers, int iconHeight) {
        int tick = deafenedEffect.getAnimTick();
        int alpha = 55 + deafenedEffect.getAlphaTick();
        final int light = 0xF000F0;

        int innerFrames = tick % 14 + 1;
        VertexConsumer innerWave = buffers.getBuffer(DeafeningType.SOUND_WAVE_TYPE(innerFrames, "inner"));
        innerWave.vertex(poseStack.last().pose(), -4.0F, -4 + iconHeight, 0).color(255, 255, 255, alpha).uv(0.0F, 0.0F).uv2(light).endVertex();
        innerWave.vertex(poseStack.last().pose(), -4.0F, 4 + iconHeight, 0).color(255, 255, 255, alpha).uv(0.0F, 1.0F).uv2(light).endVertex();
        innerWave.vertex(poseStack.last().pose(), 4.0F, 4 + iconHeight, 0).color(255, 255, 255, alpha).uv(1.0F, 1.0F).uv2(light).endVertex();
        innerWave.vertex(poseStack.last().pose(), 4.0F, -4 + iconHeight, 0).color(255, 255, 255, alpha).uv(1.0F, 0.0F).uv2(light).endVertex();

        int middleFrames = tick % 21 + 1;
        VertexConsumer middleWave = buffers.getBuffer(DeafeningType.SOUND_WAVE_TYPE(middleFrames, "middle"));
        middleWave.vertex(poseStack.last().pose(), -8.0F, -8 + iconHeight, 0).color(255, 255, 255, alpha).uv(0.0F, 0.0F).uv2(light).endVertex();
        middleWave.vertex(poseStack.last().pose(), -8.0F, 8 + iconHeight, 0).color(255, 255, 255, alpha).uv(0.0F, 1.0F).uv2(light).endVertex();
        middleWave.vertex(poseStack.last().pose(), 8.0F, 8 + iconHeight, 0).color(255, 255, 255, alpha).uv(1.0F, 1.0F).uv2(light).endVertex();
        middleWave.vertex(poseStack.last().pose(), 8.0F, -8 + iconHeight, 0).color(255, 255, 255, alpha).uv(1.0F, 0.0F).uv2(light).endVertex();

        int outerFrames = tick % 28 + 1;
        VertexConsumer outerWave = buffers.getBuffer(DeafeningType.SOUND_WAVE_TYPE(outerFrames, "outer"));
        outerWave.vertex(poseStack.last().pose(), -12.0F, -12 + iconHeight, 0).color(255, 255, 255, alpha).uv(0.0F, 0.0F).uv2(light).endVertex();
        outerWave.vertex(poseStack.last().pose(), -12.0F, 12 + iconHeight, 0).color(255, 255, 255, alpha).uv(0.0F, 1.0F).uv2(light).endVertex();
        outerWave.vertex(poseStack.last().pose(), 12.0F, 12 + iconHeight, 0).color(255, 255, 255, alpha).uv(1.0F, 1.0F).uv2(light).endVertex();
        outerWave.vertex(poseStack.last().pose(), 12.0F, -12 + iconHeight, 0).color(255, 255, 255, alpha).uv(1.0F, 0.0F).uv2(light).endVertex();
    }
}
