package net.arkadiyhimself.fantazia.client.Models.Entity.VanillaTweaks.RenderAboveTypes;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP;

public class DeafeningType extends RenderStateShard {
    public static ResourceLocation getFrame(int number, String type) {
        String num = String.valueOf(number);
        return new ResourceLocation(Fantazia.MODID, "textures/render_above/deafening/" + type + "_circle/" + type + "_circle" + num + ".png");
    }
    public static final RenderType SOUND_WAVE_TYPE(int number, String type) { return soundWaveType(number, type); }
    public DeafeningType(String pName, Runnable pSetupState, Runnable pClearState) {super(pName, pSetupState, pClearState);}
    private static RenderType soundWaveType(int number, String type) {
        RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(DeafeningType.getFrame(number, type), false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false);
        return DeafeningType.createDeafWaves("inner_circle", POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, renderTypeState);
    }
    private static RenderType createDeafWaves(String name, VertexFormat format, VertexFormat.Mode mode, int bufSize, boolean affectsCrumbling, boolean sortOnUpload, RenderType.CompositeState glState) {
        return RenderType.create(name, format, mode, bufSize, affectsCrumbling, sortOnUpload, glState);
    }
}
