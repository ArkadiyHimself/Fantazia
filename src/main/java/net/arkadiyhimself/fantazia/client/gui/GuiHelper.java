package net.arkadiyhimself.fantazia.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GuiHelper {
    public static void wholeScreen(ResourceLocation resourceLocation, float red, float green, float blue, float alpha) {
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        float[] previousSC = RenderSystem.getShaderColor();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(red, green, blue, alpha);
        RenderSystem.setShaderTexture(0, resourceLocation);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(0.0D, height, -90.0D).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex(width, height, -90.0D).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(width, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(previousSC[0], previousSC[1], previousSC[2], previousSC[3]);
    }
    @SuppressWarnings("all")
    public static void addComponent(List<Component> list, String str, @Nullable ChatFormatting[] strFormat, @Nullable ChatFormatting[] varFormat, Object... objs) {
        Component[] stringValues = new Component[objs.length];
        int counter = 0;
        for (Object obj : objs) {
            MutableComponent comp;

            if (obj instanceof MutableComponent mut) comp = mut;
            else comp = Component.literal(obj.toString());

            if (varFormat != null) comp = comp.withStyle(varFormat);
            stringValues[counter] = comp;
            counter++;
        }
        if (strFormat == null) list.add(Component.translatable(str, stringValues));
        else list.add(Component.translatable(str, stringValues).withStyle(strFormat));
    }
    public static Component attributeModifierComponent(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
        double value = modifier.getAmount();
        if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) return Component.translatable("attribute.modifier.plus." + modifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value), Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.BLUE);
        else return Component.translatable("attribute.modifier.plus." + modifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value), Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.RED);
    }
}
