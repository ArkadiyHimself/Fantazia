package net.arkadiyhimself.fantazia.data.talent.types;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.data.talent.TalentDataException;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record AttributeTalent(ITalent.BasicProperties properties, Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) implements ITalent {

    @Override
    public BasicProperties getProperties() {
        return properties;
    }

    @Override
    public void applyModifiers(@NotNull Player player) {
        AttributeInstance instance = player.getAttribute(attribute);
        AttributeModifier modifier = makeModifier();
        if (instance == null || instance.hasModifier(getID())) return;
        instance.addPermanentModifier(modifier);
        if (Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable(String.valueOf(instance.getValue())));
    }

    @Override
    public void removeModifiers(@NotNull Player player) {
        AttributeInstance instance = player.getAttribute(attribute);
        AttributeModifier modifier = makeModifier();
        if (instance == null || !instance.hasModifier(getID())) return;
        instance.removeModifier(modifier);
        if (Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable(String.valueOf(instance.getValue())));
    }

    @Override
    public List<Component> buildIconTooltip() {
        if (!Screen.hasShiftDown()) return ITalent.super.buildIconTooltip();
        List<Component> components = Lists.newArrayList();
        components.add(GuiHelper.attributeModifierComponent(attribute, makeModifier()));
        components.addAll(ITalent.super.buildIconTooltip());
        return components;
    }

    @Override
    public String toString() {
        return "AttributeTalent{" + getID() + "}";
    }

    // use it purely for render purposes
    private AttributeModifier makeModifier() {
        return new AttributeModifier(getID(), amount, operation);
    }

    public double getAmount() {
        return amount;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    public static class Builder extends ITalentBuilder.AbstractBuilder<AttributeTalent> {

        private final ResourceLocation attribute;
        private final double amount;
        private final String operation;

        public Builder(ResourceLocation iconTexture, String title, int wisdom, ResourceLocation advancement, ResourceLocation attribute, double amount, String operation) {
            super(iconTexture, title, wisdom, advancement);
            this.attribute = attribute;
            this.amount = amount;
            this.operation = operation;
        }

        @Override
        public AttributeTalent build(ResourceLocation identifier) throws TalentDataException {
            Optional<Holder.Reference<Attribute>> attr = BuiltInRegistries.ATTRIBUTE.getHolder(attribute);
            if (attr.isEmpty()) throw new TalentDataException("Could not resolve attribute: " + attribute);

            AttributeModifier.Operation operation1 = switch (operation) {
                case "addition" -> AttributeModifier.Operation.ADD_VALUE;
                case "multiply_base" -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
                case "multiply_total" -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
                default -> throw new TalentDataException("Could not resolve operation: " + operation);
            };

            return new AttributeTalent(buildProperties(identifier), attr.get(), amount, operation1);
        }
    }
}
