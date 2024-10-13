package net.arkadiyhimself.fantazia.data.talent.types;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesGetter;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.TalentAttributeModifiersHolder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.data.talent.ITalentBuilder;
import net.arkadiyhimself.fantazia.data.talent.TalentDataException;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AttributeTalent extends BasicTalent {

    private final double amount;
    private final AttributeModifier.Operation operation;
    private final Holder<Attribute> attribute;

    public AttributeTalent(ResourceLocation iconTexture, String title, int wisdom, ResourceLocation advancement, Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
        super(iconTexture, title, wisdom, advancement);
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public void applyModifiers(@NotNull Player player) {
        AttributeInstance instance = player.getAttribute(attribute);
        AttributeModifier modifier = getAttributeModifier(player.level());
        if (instance == null || modifier == null || instance.hasModifier(getID())) return;
        instance.addPermanentModifier(modifier);
        if (Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable(String.valueOf(instance.getValue())));
    }

    @Override
    public void removeModifiers(@NotNull Player player) {
        AttributeInstance instance = player.getAttribute(attribute);
        AttributeModifier modifier = getAttributeModifier(player.level());
        if (instance == null || modifier == null || !instance.hasModifier(getID())) return;
        instance.removeModifier(modifier);
        if (Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable(String.valueOf(instance.getValue())));
    }

    @Override
    public List<Component> buildIconTooltip() {
        if (!Screen.hasShiftDown()) return super.buildIconTooltip();
        List<Component> components = Lists.newArrayList();
        components.add(GuiHelper.attributeModifierComponent(attribute, insecureModifier()));
        components.addAll(super.buildIconTooltip());
        return components;
    }

    // use it purely for render purposes
    private AttributeModifier insecureModifier() {
        return new AttributeModifier(getID(), amount, operation);
    }

    @Nullable
    public AttributeModifier getAttributeModifier(Level level) {
        TalentAttributeModifiersHolder holder = LevelAttributesGetter.takeHolder(level, TalentAttributeModifiersHolder.class);
        if (holder == null) return null;
        return holder.getOrCreateModifier(this);
    }

    public Holder<Attribute> getAttribute() {
        return attribute;
    }

    public double getAmount() {
        return amount;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    public static class Builder implements ITalentBuilder<AttributeTalent> {

        private final ResourceLocation iconTexture;
        private final String title;
        private final int wisdom;
        private final ResourceLocation advancement;
        private final ResourceLocation attribute;
        private final double amount;
        private final String operation;

        public Builder(ResourceLocation iconTexture, String title, int wisdom, ResourceLocation advancement, ResourceLocation attribute, double amount, String operation) {
            this.iconTexture = iconTexture;
            this.title = title;
            this.wisdom = wisdom;
            this.advancement = advancement;
            this.attribute = attribute;
            this.amount = amount;
            this.operation = operation;
        }

        @Override
        public AttributeTalent build() throws TalentDataException {
            Optional<Holder.Reference<Attribute>> attr = BuiltInRegistries.ATTRIBUTE.getHolder(attribute);
            if (attr.isEmpty()) throw new TalentDataException("Could not resolve attribute: " + attribute);

            AttributeModifier.Operation operation1 = switch (operation) {
                case "addition" -> AttributeModifier.Operation.ADD_VALUE;
                case "multiply_base" -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
                case "multiply_total" -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
                default -> throw new TalentDataException("Could not resolve operation: " + operation);
            };

            return new AttributeTalent(iconTexture, title, wisdom, advancement, attr.get(), amount, operation1);
        }
    }
}
