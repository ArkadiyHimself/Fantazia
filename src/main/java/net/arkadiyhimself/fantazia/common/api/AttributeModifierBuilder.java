package net.arkadiyhimself.fantazia.common.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record AttributeModifierBuilder(double amount, AttributeModifier.Operation operation) {

    public static final Codec<AttributeModifierBuilder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("amount").forGetter(AttributeModifierBuilder::amount),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeModifierBuilder::operation)
    ).apply(instance, AttributeModifierBuilder::new));

    public AttributeModifier build(ResourceLocation id) {
        return new AttributeModifier(id, amount, operation);
    }
}
