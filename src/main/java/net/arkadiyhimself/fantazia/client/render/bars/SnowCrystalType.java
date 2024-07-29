package net.arkadiyhimself.fantazia.client.render.bars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects.FrozenEffect;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SnowCrystalType extends RenderStateShard {
    public static final ResourceLocation SNOW_CRYSTAL = new ResourceLocation(Fantazia.MODID, "textures/render_above/snow_crystal.png");
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
    public static void render(@NotNull FrozenEffect frozenEffect, PoseStack poseStack, MultiBufferSource buffers, int iconHeight) {
        VertexConsumer freezeType = buffers.getBuffer(SnowCrystalType.SNOW_CRYSTAL_TYPE);
        final int light = 0xF000F0;
        if (frozenEffect.effectPercent() > 0) {
            int effectPercent = (int) Math.ceil(frozenEffect.effectPercent() * 255);
            freezeType.vertex(poseStack.last().pose(), -8, iconHeight, 0).color(255, 255, 255, effectPercent).uv(0.0F, 0.0F).uv2(light).endVertex();
            freezeType.vertex(poseStack.last().pose(), -8, 16 + iconHeight, 0).color(255, 255, 255, effectPercent).uv(0.0F, 1.0F).uv2(light).endVertex();
            freezeType.vertex(poseStack.last().pose(), 8, 16 + iconHeight, 0).color(255, 255, 255, effectPercent).uv(1.0F, 1.0F).uv2(light).endVertex();
            freezeType.vertex(poseStack.last().pose(), 8, iconHeight, 0).color(255, 255, 255, effectPercent).uv(1.0F, 0.0F).uv2(light).endVertex();
        } else {
            int freezePercent = (int) (frozenEffect.freezePercent() * 235);
            freezeType.vertex(poseStack.last().pose(), -8, iconHeight, 0).color(255, 255, 255, 20 + freezePercent).uv(0.0F, 0.0F).uv2(light).endVertex();
            freezeType.vertex(poseStack.last().pose(), -8, 16 + iconHeight, 0).color(255, 255, 255, 20 + freezePercent).uv(0.0F, 1.0F).uv2(light).endVertex();
            freezeType.vertex(poseStack.last().pose(), 8, 16 + iconHeight, 0).color(255, 255, 255, 20 + freezePercent).uv(1.0F, 1.0F).uv2(light).endVertex();
            freezeType.vertex(poseStack.last().pose(), 8, iconHeight, 0).color(255, 255, 255, 20 + freezePercent).uv(1.0F, 0.0F).uv2(light).endVertex();
        }
    }
}
