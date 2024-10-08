package net.arkadiyhimself.fantazia.client.render.bars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class DisarmedSwordType extends RenderStateShard {
    public static final ResourceLocation BROKEN_SWORD = Fantazia.res("textures/render_above/broken_sword.png");
    public static final RenderType BROKEN_SWORD_TYPE = brokenSwordType();
    public DisarmedSwordType(String pName, Runnable pSetupState, Runnable pClearState) {super(pName, pSetupState, pClearState);}
    private static RenderType brokenSwordType() {
        RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new TextureStateShard(DisarmedSwordType.BROKEN_SWORD, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false);
        return DisarmedSwordType.createBrokenSword(renderTypeState);
    }
    private static RenderType createBrokenSword(RenderType.CompositeState glState) {
        return RenderType.create("broken_sword", com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, glState);
    }
    public static void render(PoseStack poseStack, MultiBufferSource buffers, float iconHeight) {
        VertexConsumer disarmType = buffers.getBuffer(DisarmedSwordType.BROKEN_SWORD_TYPE);
        final int light = 0xF000F0;
        poseStack.pushPose();
        poseStack.translate(0,iconHeight,0);
        disarmType.addVertex(poseStack.last().pose(), -0.8F, -0.8f, 0).setColor(255, 255, 255, 255).setUv(0.0F, 0.0F).setLight(light);
        disarmType.addVertex(poseStack.last().pose(), -0.8F, 0.8f, 0).setColor(255, 255, 255, 255).setUv(0.0F, 1.0F).setLight(light);
        disarmType.addVertex(poseStack.last().pose(), 0.8F, 0.8f, 0).setColor(255, 255, 255, 255).setUv(1.0F, 1.0F).setLight(light);
        disarmType.addVertex(poseStack.last().pose(), 0.8F, -0.8f, 0).setColor(255, 255, 255, 255).setUv(1.0F, 0.0F).setLight(light);
        poseStack.popPose();
    }
}