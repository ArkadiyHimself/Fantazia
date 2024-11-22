package net.arkadiyhimself.fantazia.client.render.bars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.DeafenedEffect;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DeafeningType extends RenderStateShard {
    public static ResourceLocation getFrame(int number, String type) {
        String num = String.valueOf(number);
        return Fantazia.res( "textures/render_above/deafening/" + type + "_circle/" + type + "_circle" + num + ".png");
    }
    public static RenderType SOUND_WAVE_TYPE(int number, String type) { return soundWaveType(number, type); }
    public DeafeningType(String pName, Runnable pSetupState, Runnable pClearState) {super(pName, pSetupState, pClearState);}
    private static RenderType soundWaveType(int number, String type) {
        RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new TextureStateShard(DeafeningType.getFrame(number, type), false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false);
        return DeafeningType.createDeafWaves(renderTypeState);
    }
    private static RenderType createDeafWaves(RenderType.CompositeState glState) {
        return RenderType.create("deaf_circle", com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, glState);
    }
    public static void render(@NotNull DeafenedEffect deafenedEffect, PoseStack poseStack, MultiBufferSource buffers, float iconHeight) {
        poseStack.pushPose();
        poseStack.translate(0,iconHeight,0);
        int tick = deafenedEffect.getEntity().tickCount;
        int alpha = 125 + (int) (FantazicMath.intoSin(tick, 28) * 65);
        final int light = 0xF000F0;

        float halfSize = 0.25f;

        int innerFrames = tick % 14 + 1;
        VertexConsumer innerWave = buffers.getBuffer(DeafeningType.SOUND_WAVE_TYPE(innerFrames, "inner"));
        innerWave.addVertex(poseStack.last().pose(), -halfSize, -halfSize, 0).setColor(255, 255, 255, alpha).setUv(0.0F, 0.0F).setLight(light);
        innerWave.addVertex(poseStack.last().pose(), -halfSize, halfSize, 0).setColor(255, 255, 255, alpha).setUv(0.0F, 1.0F).setLight(light);
        innerWave.addVertex(poseStack.last().pose(), halfSize, halfSize, 0).setColor(255, 255, 255, alpha).setUv(1.0F, 1.0F).setLight(light);
        innerWave.addVertex(poseStack.last().pose(), halfSize, -halfSize, 0).setColor(255, 255, 255, alpha).setUv(1.0F, 0.0F).setLight(light);

        int middleFrames = tick % 21 + 1;
        VertexConsumer middleWave = buffers.getBuffer(DeafeningType.SOUND_WAVE_TYPE(middleFrames, "middle"));
        middleWave.addVertex(poseStack.last().pose(), -halfSize * 2, -halfSize * 2, 0).setColor(255, 255, 255, alpha).setUv(0.0F, 0.0F).setLight(light);
        middleWave.addVertex(poseStack.last().pose(), -halfSize * 2, halfSize * 2, 0).setColor(255, 255, 255, alpha).setUv(0.0F, 1.0F).setLight(light);
        middleWave.addVertex(poseStack.last().pose(), halfSize * 2, halfSize * 2, 0).setColor(255, 255, 255, alpha).setUv(1.0F, 1.0F).setLight(light);
        middleWave.addVertex(poseStack.last().pose(), halfSize * 2, -halfSize * 2, 0).setColor(255, 255, 255, alpha).setUv(1.0F, 0.0F).setLight(light);

        int outerFrames = tick % 28 + 1;
        VertexConsumer outerWave = buffers.getBuffer(DeafeningType.SOUND_WAVE_TYPE(outerFrames, "outer"));
        outerWave.addVertex(poseStack.last().pose(), -halfSize * 3, -halfSize * 3, 0).setColor(255, 255, 255, alpha).setUv(0.0F, 0.0F).setLight(light);
        outerWave.addVertex(poseStack.last().pose(), -halfSize * 3, halfSize * 3, 0).setColor(255, 255, 255, alpha).setUv(0.0F, 1.0F).setLight(light);
        outerWave.addVertex(poseStack.last().pose(), halfSize * 3, halfSize * 3, 0).setColor(255, 255, 255, alpha).setUv(1.0F, 1.0F).setLight(light);
        outerWave.addVertex(poseStack.last().pose(), halfSize * 3, -halfSize * 3, 0).setColor(255, 255, 255, alpha).setUv(1.0F, 0.0F).setLight(light);
        poseStack.popPose();
    }
}
