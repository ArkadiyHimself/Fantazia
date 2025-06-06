package net.arkadiyhimself.fantazia.api;

import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nullable;

public record AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation, @Nullable Int2DoubleFunction curve) {

    public AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        this(id, amount, operation, (Int2DoubleFunction)null);
    }

    public AttributeModifier create(int level) {
        return this.curve != null ? new AttributeModifier(this.id, this.curve.apply(level), this.operation) : new AttributeModifier(this.id, this.amount * (double)(level + 1), this.operation);
    }
}
