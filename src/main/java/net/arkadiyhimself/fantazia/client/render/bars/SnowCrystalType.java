package net.arkadiyhimself.fantazia.client.render.bars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.DurationHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SnowCrystalType extends RenderStateShard {
    public static final ResourceLocation SNOW_CRYSTAL = Fantazia.res("textures/render_above/snow_crystal.png");
    public static final RenderType SNOW_CRYSTAL_TYPE = snowCrystalType();
    public SnowCrystalType(String pName, Runnable pSetupState, Runnable pClearState) {super(pName, pSetupState, pClearState);}
    private static RenderType snowCrystalType() {
        RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new TextureStateShard(SnowCrystalType.SNOW_CRYSTAL, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false);
        return SnowCrystalType.createSnowCrystal(renderTypeState);
    }
    private static RenderType createSnowCrystal(RenderType.CompositeState glState) {
        return RenderType.create("snow_crystal", com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, glState);
    }
    public static void render(LivingEntity entity, @NotNull DurationHolder holder, PoseStack poseStack, MultiBufferSource buffers, float iconHeight) {
        VertexConsumer freezeType = buffers.getBuffer(SnowCrystalType.SNOW_CRYSTAL_TYPE);
        poseStack.pushPose();
        poseStack.translate(0,iconHeight,0);
        final int light = 0xF000F0;
        float percent = holder.percent();
        if (percent > 0) {
            int effectPercent = (int) Math.ceil(percent * 255);
            freezeType.addVertex(poseStack.last().pose(), -0.8f, -0.8f, 0).setColor(255, 255, 255, effectPercent).setUv(0.0F, 0.0F).setLight(light);
            freezeType.addVertex(poseStack.last().pose(), -0.8f, 0.8f, 0).setColor(255, 255, 255, effectPercent).setUv(0.0F, 1.0F).setLight(light);
            freezeType.addVertex(poseStack.last().pose(), 0.8f, 0.8f, 0).setColor(255, 255, 255, effectPercent).setUv(1.0F, 1.0F).setLight(light);
            freezeType.addVertex(poseStack.last().pose(), 0.8f, -0.8f, 0).setColor(255, 255, 255, effectPercent).setUv(1.0F, 0.0F).setLight(light);
        } else {
            int freezePercent = (int) (entity.getPercentFrozen() * 235);
            freezeType.addVertex(poseStack.last().pose(), -0.8f, -0.8f, 0).setColor(255, 255, 255, 20 + freezePercent).setUv(0.0F, 0.0F).setLight(light);
            freezeType.addVertex(poseStack.last().pose(), -0.8f, 0.8f, 0).setColor(255, 255, 255, 20 + freezePercent).setUv(0.0F, 1.0F).setLight(light);
            freezeType.addVertex(poseStack.last().pose(), 0.8f, 0.8f, 0).setColor(255, 255, 255, 20 + freezePercent).setUv(1.0F, 1.0F).setLight(light);
            freezeType.addVertex(poseStack.last().pose(), 0.8f, -0.8f, 0).setColor(255, 255, 255, 20 + freezePercent).setUv(1.0F, 0.0F).setLight(light);
        }
        poseStack.popPose();
    }
}
