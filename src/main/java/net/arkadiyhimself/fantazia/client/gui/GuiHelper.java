package net.arkadiyhimself.fantazia.client.gui;

import net.arkadiyhimself.fantazia.common.advanced.dynamic_attribute_modifier.DynamicAttributeModifier;
import net.arkadiyhimself.fantazia.util.simpleobjects.PercentageAttribute;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class GuiHelper {

    public static final DecimalFormat PERCENTAGE_ATTRIBUTE_MODIFIER = Util.make(new DecimalFormat("#"), (p_41704_) -> {
        p_41704_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    });

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

    public static Component bakeLevelComponent(ResourceKey<Level> key) {
        ResourceLocation location = key.location();
        return Component.translatable("dimension." + location.getNamespace() + "." + location.getPath());
    }

    public static Component attributeModifierComponent(@NotNull Holder<Attribute> attribute, @NotNull AttributeModifier modifier) {
        double amount = modifier.amount();
        boolean pos = amount >= 0;
        double delta = Math.abs(amount);
        String str = pos ? ".plus." : ".take.";

        if (attribute.value() instanceof PercentageAttribute percentageAttribute) return Component.translatable("fantazia.percentage_attribute" + str + modifier.operation().id(), PERCENTAGE_ATTRIBUTE_MODIFIER.format(delta), Component.translatable(percentageAttribute.getDescriptionId())).withStyle(pos ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED);

        if (modifier.operation() == AttributeModifier.Operation.ADD_VALUE) return Component.translatable("attribute.modifier" + str + modifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(delta), Component.translatable(attribute.value().getDescriptionId())).withStyle(ChatFormatting.DARK_PURPLE);
        else return Component.translatable("attribute.modifier" + str + modifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(delta), Component.translatable(attribute.value().getDescriptionId())).withStyle(ChatFormatting.RED);
    }

    public static Component attributeModifierComponent(@NotNull Holder<Attribute> attribute, @NotNull DynamicAttributeModifier modifier) {
        return attributeModifierComponent(attribute, modifier.fullModifier());
    }

}
