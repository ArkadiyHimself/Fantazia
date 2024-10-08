package net.arkadiyhimself.fantazia.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arkadiyhimself.fantazia.advanced.dynamicattributemodifying.DynamicAttributeModifier;
import net.arkadiyhimself.fantazia.simpleobjects.PercentageAttribute;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class GuiHelper {
    public static final DecimalFormat PERCENTAGE_ATTRIBUTE_MODIFIER = Util.make(new DecimalFormat("#"), (p_41704_) -> {
        p_41704_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    });
    public static void wholeScreen(GuiGraphics guiGraphics, ResourceLocation resourceLocation, float red, float green, float blue, float alpha) {
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        float[] previousSC = RenderSystem.getShaderColor();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        RenderSystem.setShaderColor(red, green, blue, alpha);
        guiGraphics.blit(resourceLocation,0,0, -90, 0,0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), guiGraphics.guiWidth(), guiGraphics.guiHeight());

      //  RenderSystem.setShaderTexture(0, resourceLocation);
       // Tesselator tesselator = Tesselator.getInstance();
       // BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
       // bufferbuilder.addVertex(0.0f, height, -90.0f).setUv(0.0F, 1.0F);
       // bufferbuilder.addVertex(width, height, -90.0f).setUv(1.0F, 1.0F);
       // bufferbuilder.addVertex(width, 0.0f, -90.0f).setUv(1.0F, 0.0F);
       // bufferbuilder.addVertex(0.0f, 0.0f, -90.0f).setUv(0.0F, 0.0F);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(previousSC[0], previousSC[1], previousSC[2], previousSC[3]);
    }
    @SuppressWarnings("all")
    public static Component bakeComponent(String str, @Nullable ChatFormatting[] strFormat, @Nullable ChatFormatting[] varFormat, Object... objs) {
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
        if (strFormat == null) return Component.translatable(str, stringValues);
        else return Component.translatable(str, stringValues).withStyle(strFormat);
    }
    public static Component attributeModifierComponent(@NotNull Holder<Attribute> attribute, @NotNull AttributeModifier modifier) {
        double value = modifier.amount();
        if (attribute.value() instanceof PercentageAttribute percentageAttribute) {
            if (value >= 0) return Component.translatable("fantazia.percentage_attribute." + modifier.operation().id(), PERCENTAGE_ATTRIBUTE_MODIFIER.format(value), Component.translatable(percentageAttribute.getDescriptionId())).withStyle(ChatFormatting.DARK_GREEN);
            else return Component.translatable("fantazia.percentage_attribute." + modifier.operation().id(), PERCENTAGE_ATTRIBUTE_MODIFIER.format(value), Component.translatable(percentageAttribute.getDescriptionId())).withStyle(ChatFormatting.DARK_RED);
        }
        if (modifier.operation() == AttributeModifier.Operation.ADD_VALUE) return Component.translatable("attribute.modifier.plus." + modifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(value), Component.translatable(attribute.value().getDescriptionId())).withStyle(ChatFormatting.DARK_PURPLE);
        else return Component.translatable("attribute.modifier.plus." + modifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(value), Component.translatable(attribute.value().getDescriptionId())).withStyle(ChatFormatting.RED);
    }
    public static Component attributeModifierComponent(@NotNull Holder<Attribute> attribute, @NotNull DynamicAttributeModifier modifier) {
        return attributeModifierComponent(attribute, modifier.maximumModifier());
    }
}
