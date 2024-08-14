package net.arkadiyhimself.fantazia.data.talents;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AttributeTalent extends BasicTalent {
    private final Attribute attribute;
    private final AttributeModifier modifier;
    public AttributeTalent(ResourceLocation iconTexture, String title, int wisdomCost, Attribute attribute, String modifierID, double amount, AttributeModifier.Operation operation) {
        super(iconTexture, title, wisdomCost);
        this.attribute = attribute;
        this.modifier = new AttributeModifier(modifierID, amount, operation);
    }
    @Override
    public List<Component> buildIconTooltip() {
        List<Component> components = super.buildIconTooltip();
        if (!Screen.hasShiftDown()) components.add(GuiHelper.attributeModifierComponent(attribute, modifier));
        return components;
    }

    public Attribute getAttribute() {
        return attribute;
    }
    public AttributeModifier getModifier() {
        return modifier;
    }
    public void applyModifier(@NotNull Player player) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null || instance.hasModifier(modifier)) return;
        instance.addPermanentModifier(modifier);
        if (Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable(String.valueOf(instance.getValue())));
    }
    public void removeModifier(@NotNull Player player) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null || !instance.hasModifier(modifier)) return;
        instance.removeModifier(modifier);
        if (Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable(String.valueOf(instance.getValue())));
    }
    public static class Builder {
        private final ResourceLocation iconTexture;
        private final String title;
        private final int wisdomCost;
        private final ResourceLocation attributeID;
        private final String modifierID;
        private final double amount;
        private final String operation;
        public Builder(ResourceLocation iconTexture, String title, int wisdomCost, ResourceLocation attributeID, String modifierID, double amount, String operation) {
            this.iconTexture = iconTexture;
            this.title = title;
            this.wisdomCost = wisdomCost;
            this.attributeID = attributeID;
            this.modifierID = modifierID;
            this.amount = amount;
            this.operation = operation;
        }
        public AttributeTalent build() throws TalentDataException {
            Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(attributeID);
            if (attr == null) throw new TalentDataException("Could not resolve attribute: " + attributeID.toString());
            AttributeModifier.Operation operation1 = switch (operation) {
                default -> throw new TalentDataException("Could not resolve operation: " + operation);
                case "addition" -> AttributeModifier.Operation.ADDITION;
                case "multiply_base" -> AttributeModifier.Operation.MULTIPLY_BASE;
                case "multiply_total" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
            };
            return new AttributeTalent(iconTexture, title, wisdomCost, attr, modifierID, amount, operation1);
        }
    }
}
