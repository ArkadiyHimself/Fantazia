package net.arkadiyhimself.fantazia.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class FTZRenderTypes {

    private static final Function<ResourceLocation, RenderType> customGlint = Util.memoize(FTZRenderTypes::glint);
    public static ShaderInstance customGlintShader;

    public static final RenderStateShard.ShaderStateShard RENDERTYPE_CUSTOM_GLINT_SHADER = new RenderStateShard.ShaderStateShard(() -> customGlintShader);

    public static RenderType customGlint(ResourceLocation texture) {
        return customGlint.apply(texture);
    }

    public static RenderType glint(ResourceLocation texture) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_CUSTOM_GLINT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .setCullState(RenderStateShard.NO_CULL)
                .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
                .setTexturingState(RenderStateShard.GLINT_TEXTURING).createCompositeState(false);
        return RenderType.create(Fantazia.res("custom_glint").toString(), DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, true, false, compositeState);
    }
}
