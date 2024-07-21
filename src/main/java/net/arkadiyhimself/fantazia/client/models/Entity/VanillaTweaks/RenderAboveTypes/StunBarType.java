package net.arkadiyhimself.fantazia.client.models.Entity.VanillaTweaks.RenderAboveTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects.StunEffect;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP;

public class StunBarType extends RenderStateShard {
    private static final ResourceLocation STUN_BAR = Fantazia.res("textures/render_above/stun_bar.png");
    public static final RenderType BAR_TEXTURE_TYPE = emptyStunBarType();
    public StunBarType(String pName, Runnable pSetupState, Runnable pClearState) {super(pName, pSetupState, pClearState);}
    private static RenderType emptyStunBarType() {
        RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new TextureStateShard(StunBarType.STUN_BAR, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false);
        return StunBarType.createStunBar("stun_bar", POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, renderTypeState);
    }
    private static RenderType createStunBar(String name, VertexFormat format, VertexFormat.Mode mode, int bufSize, boolean affectsCrumbling, boolean sortOnUpload, RenderType.CompositeState glState) {
        return RenderType.create(name, format, mode, bufSize, affectsCrumbling, sortOnUpload, glState);
    }
    public static void render(@NotNull StunEffect stunEffect, PoseStack poseStack, MultiBufferSource buffers) {
        float stunPercent;
        VertexConsumer stunBar = buffers.getBuffer(StunBarType.BAR_TEXTURE_TYPE);
        final int light = 0xF000F0;

        if (stunEffect.stunned()) {
            stunPercent = (float) stunEffect.getDur() / (float) stunEffect.getInitDur();
            int changingRed = stunEffect.getColor();



            // empty bar
            stunBar.vertex(poseStack.last().pose(), -16, -4, 0.003F).color(changingRed, 255, 255, 255).uv(0.0F, 0.5F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), -16, 0, 0.003F).color(changingRed, 255, 255, 255).uv(0.0F, 0.75F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), 16, 0, 0.003F).color(changingRed, 255, 255, 255).uv(1.0F, 0.75F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), 16, -4, 0.003F).color(changingRed, 255, 255, 255).uv(1.0F, 0.5F).uv2(light).endVertex();

            // filling
            stunBar.vertex(poseStack.last().pose(), -14, -4, 0.004F).color(255, 255, 255, 255).uv(0.0F, 0.75F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), -14, 0, 0.004F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, 0, 0.004F).color(255, 255, 255, 255).uv(stunPercent, 1.0F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, -4, 0.004F).color(255, 255, 255, 255).uv(stunPercent, 0.75F).uv2(light).endVertex();
        } else if (stunEffect.hasPoints()) {
            stunPercent = (float) stunEffect.getPoints() / (float) stunEffect.getMaxPoints();

            // empty bar
            stunBar.vertex(poseStack.last().pose(), -16, 0, 0.003F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), -16, 4, 0.003F).color(255, 255, 255, 255).uv(0.0F, 0.25F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), 16, 4, 0.003F).color(255, 255, 255, 255).uv(1.0F, 0.25F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), 16, 0, 0.003F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();

            // filling
            stunBar.vertex(poseStack.last().pose(), -14, 0, 0.004F).color(255, 255, 255, 255).uv(0.0F, 0.25F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), -14, 4, 0.004F).color(255, 255, 255, 255).uv(0.0F, 0.5F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, 4, 0.004F).color(255, 255, 255, 255).uv(stunPercent, 0.5F).uv2(light).endVertex();
            stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, 0, 0.004F).color(255, 255, 255, 255).uv(stunPercent, 0.25F).uv2(light).endVertex();
        }
    }
}
