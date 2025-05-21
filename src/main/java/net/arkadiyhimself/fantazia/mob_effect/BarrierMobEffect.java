package net.arkadiyhimself.fantazia.mob_effect;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

public class BarrierMobEffect extends MobEffect implements IPatchouliEntry {

    public BarrierMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 8780799);
        addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, Fantazia.res("effect.barrier"), 0.5, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public @NotNull BarrierMobEffect addAttributeModifier(@NotNull Holder<Attribute> attribute, @NotNull ResourceLocation id, double amount, AttributeModifier.@NotNull Operation operation) {
        super.addAttributeModifier(attribute, id, amount, operation);
        return this;
    }
}
