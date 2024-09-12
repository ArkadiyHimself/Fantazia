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
    public static final ResourceLocation BROKEN_SWORD = new ResourceLocation(Fantazia.MODID, "textures/render_above/broken_sword.png");
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
    public static void render(PoseStack poseStack, MultiBufferSource buffers, int iconHeight) {
        VertexConsumer disarmType = buffers.getBuffer(DisarmedSwordType.BROKEN_SWORD_TYPE);
        final int light = 0xF000F0;
        disarmType.vertex(poseStack.last().pose(), -10.0F, iconHeight, 0).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();
        disarmType.vertex(poseStack.last().pose(), -10.0F, 20 + (float) iconHeight, 0).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
        disarmType.vertex(poseStack.last().pose(), 10.0F, 20 + (float) iconHeight, 0).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(light).endVertex();
        disarmType.vertex(poseStack.last().pose(), 10.0F, iconHeight, 0).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();
    }
}