package net.arkadiyhimself.fantazia.data.talents;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCap;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapGetter;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AttributeTalent extends BasicTalent {
    private final String name;
    private final double amount;
    private final AttributeModifier.Operation operation;
    private final Attribute attribute;
    public AttributeTalent(ResourceLocation iconTexture, String title, int wisdom, ResourceLocation advancement, Attribute attribute, String name, double amount, AttributeModifier.Operation operation) {
        super(iconTexture, title, wisdom, advancement);
        this.attribute = attribute;
        this.name = name;
        this.amount = amount;
        this.operation = operation;
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
        return new AttributeModifier(name, amount, operation);
    }
    @Nullable
    public AttributeModifier getAttributeModifier(Level level) {
        LevelCap levelCap = LevelCapGetter.getLevelCap(level);
        if (levelCap == null) return null;
        return levelCap.getOrCreateModifier(this);
    }
    public Attribute getAttribute() {
        return attribute;
    }

    public void applyModifier(@NotNull Player player) {
        AttributeInstance instance = player.getAttribute(attribute);
        AttributeModifier modifier = getAttributeModifier(player.level());
        if (instance == null || modifier == null || instance.hasModifier(modifier)) return;
        instance.addPermanentModifier(modifier);
        if (Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable(String.valueOf(instance.getValue())));
    }
    public void removeModifier(@NotNull Player player) {
        AttributeInstance instance = player.getAttribute(attribute);
        AttributeModifier modifier = getAttributeModifier(player.level());
        if (instance == null || modifier == null || !instance.hasModifier(modifier)) return;
        instance.removeModifier(modifier);
        if (Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable(String.valueOf(instance.getValue())));
    }
    public String getName() {
        return name;
    }
    public double getAmount() {
        return amount;
    }
    public AttributeModifier.Operation getOperation() {
        return operation;
    }
    public static class Builder {
        private final ResourceLocation iconTexture;
        private final String title;
        private final int wisdom;
        private final ResourceLocation advancement;
        private final ResourceLocation attribute;
        private final String name;
        private final double amount;
        private final String operation;
        public Builder(ResourceLocation iconTexture, String title, int wisdom, ResourceLocation advancement, ResourceLocation attribute, String name, double amount, String operation) {
            this.iconTexture = iconTexture;
            this.title = title;
            this.wisdom = wisdom;
            this.advancement = advancement;
            this.attribute = attribute;
            this.name = name;
            this.amount = amount;
            this.operation = operation;
        }
        public AttributeTalent build() throws TalentDataException {
            Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(attribute);
            if (attr == null) throw new TalentDataException("Could not resolve attribute: " + attribute.toString());
            AttributeModifier.Operation operation1 = switch (operation) {
                case "addition" -> AttributeModifier.Operation.ADDITION;
                case "multiply_base" -> AttributeModifier.Operation.MULTIPLY_BASE;
                case "multiply_total" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
                default -> throw new TalentDataException("Could not resolve operation: " + operation);
            };
            return new AttributeTalent(iconTexture, title, wisdom, advancement, attr, name, amount, operation1);
        }
    }
}
