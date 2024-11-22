package net.arkadiyhimself.fantazia.client.render.bars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.StunEffect;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

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
        return StunBarType.createStunBar(renderTypeState);
    }
    private static RenderType createStunBar(RenderType.CompositeState glState) {
        return RenderType.create("stun_bar", com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, glState);
    }
    public static void render(@NotNull StunEffect stunEffect, PoseStack poseStack, MultiBufferSource buffers) {
        float stunPercent;
        VertexConsumer stunBar = buffers.getBuffer(BAR_TEXTURE_TYPE);
        final int light = 0xF000F0;

        if (stunEffect.stunned()) {
            stunPercent = (float) stunEffect.duration() / (float) stunEffect.initialDuration();
            int redColor = 160 + (int) (FantazicMath.intoSin(stunEffect.getEntity().tickCount, 12) * 45);

            // empty bar
            stunBar.addVertex(poseStack.last().pose(), -1, -0.25f, 0.003F).setColor(redColor, 255, 255, 255).setUv(0.0F, 0.5F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), -1, 0, 0.003F).setColor(redColor, 255, 255, 255).setUv(0.0F, 0.75F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), 1, 0, 0.003F).setColor(redColor, 255, 255, 255).setUv(1.0F, 0.75F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), 1, -0.25f, 0.003F).setColor(redColor, 255, 255, 255).setUv(1.0F, 0.5F).setLight(light);

            // filling
            stunBar.addVertex(poseStack.last().pose(), -0.875f, -0.25f, 0.004F).setColor(255, 255, 255, 255).setUv(0.0F, 0.75F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), -0.875f, 0, 0.004F).setColor(255, 255, 255, 255).setUv(0.0F, 1.0F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), -0.875f + 1.75f * stunPercent, 0, 0.004F).setColor(255, 255, 255, 255).setUv(stunPercent, 1.0F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), -0.875f + 1.75f * stunPercent, -0.25f, 0.004F).setColor(255, 255, 255, 255).setUv(stunPercent, 0.75F).setLight(light);
        } else if (stunEffect.hasPoints()) {
             stunPercent = (float) stunEffect.getPoints() / (float) stunEffect.getMaxPoints();

            // empty bar
            stunBar.addVertex(poseStack.last().pose(), -1, -0.25f, 0.003F).setColor(255, 255, 255, 255).setUv(0.0F, 0.0F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), -1, 0, 0.003F).setColor(255, 255, 255, 255).setUv(0.0F, 0.25F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), 1, 0, 0.003F).setColor(255, 255, 255, 255).setUv(1.0F, 0.25F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), 1, -0.25f, 0.003F).setColor(255, 255, 255, 255).setUv(1.0F, 0.0F).setLight(light);

            // filling
            stunBar.addVertex(poseStack.last().pose(), -0.875f, -0.25f, 0.004F).setColor(255, 255, 255, 255).setUv(0.0F, 0.25F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), -0.875f, 0, 0.004F).setColor(255, 255, 255, 255).setUv(0.0F, 0.5F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), -0.875f + 1.75f * stunPercent, 0, 0.004F).setColor(255, 255, 255, 255).setUv(stunPercent, 0.5F).setLight(light);
            stunBar.addVertex(poseStack.last().pose(), -0.875f + 1.75f * stunPercent, -0.25f, 0.004F).setColor(255, 255, 255, 255).setUv(stunPercent, 0.25F).setLight(light);
        }
    }
}
