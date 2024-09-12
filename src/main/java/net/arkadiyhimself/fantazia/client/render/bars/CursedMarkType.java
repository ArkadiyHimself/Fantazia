package net.arkadiyhimself.fantazia.client.render.bars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class CursedMarkType extends RenderStateShard {
    public static final ResourceLocation CURSE_MARK = new ResourceLocation(Fantazia.MODID, "textures/render_above/curse_warning.png");
    public static final RenderType CURSE_WARNING_TYPE = curseWarningType();
    public CursedMarkType(String pName, Runnable pSetupState, Runnable pClearState) {
        super(pName, pSetupState, pClearState);
    }
    private static RenderType curseWarningType() {
        RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(CURSE_MARK, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false);
        return createCurseMark(renderTypeState);
    }
    private static RenderType createCurseMark(RenderType.CompositeState glState) {
        return RenderType.create("curse_mark", com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, glState);
    }
    public static void render(PoseStack poseStack, MultiBufferSource buffers, int iconHeight) {
        VertexConsumer disarmType = buffers.getBuffer(CURSE_WARNING_TYPE);
        final int light = 0xF000F0;
        disarmType.vertex(poseStack.last().pose(), -10.0F, iconHeight, 0).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();
        disarmType.vertex(poseStack.last().pose(), -10.0F, 20 + (float) iconHeight, 0).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
        disarmType.vertex(poseStack.last().pose(), 10.0F, 20 + (float) iconHeight, 0).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(light).endVertex();
        disarmType.vertex(poseStack.last().pose(), 10.0F, iconHeight, 0).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();
    }
}
