
package net.arkadiyhimself.fantazia.advanced.runes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.items.ITooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

public final class Rune implements ITooltipBuilder {

    private final ModelResourceLocation icon;
    private final ImmutableMap<Holder<Attribute>, AttributeModifier> attributeModifiers;
    private String descriptionId;
    private final ChatFormatting[] name;
    private final ChatFormatting[] desc;
    private final int fortune;
    private final int looting;

    private Rune(ResourceLocation icon, Map<Holder<Attribute>, AttributeModifier> attributeModifiers, ChatFormatting[] name, ChatFormatting[] desc, int fortune, int looting) {
        this.icon = Fantazia.modelRes(icon.withPrefix("rune/"));
        this.attributeModifiers = ImmutableMap.copyOf(attributeModifiers);
        this.name = name;
        this.desc = desc;
        this.fortune = fortune;
        this.looting = looting;
    }

    @Override
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();

        String desc = getDescriptionId();

        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.rune", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, getName(), Component.translatable(desc).getString()));

        int lines = 0;
        try {
            String amo = Component.translatable(desc + ".lines").getString();
            lines = Integer.parseInt(amo);
        } catch (NumberFormatException ignored) {}

        if (lines > 0) {
            components.add(Component.literal(" "));
            for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(desc + "." + i, getDesc(), null));
        }

        lines = 0;
        try {
            String amo = Component.translatable(desc + ".extra.lines").getString();
            lines = Integer.parseInt(amo);
        } catch (NumberFormatException ignored) {}

        if (lines > 0) {
            components.add(Component.literal(" "));
            for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(desc + ".extra." + i, getDesc(), null));
        }

        return components;
    }

    public ModelResourceLocation getIcon() {
        return icon;
    }

    public ImmutableMap<Holder<Attribute>, AttributeModifier> getAttributeModifiers() {
        return attributeModifiers;
    }

    public String getDescriptionId() {
        if (descriptionId == null) descriptionId = Util.makeDescriptionId("rune", FantazicRegistries.RUNES.getKey(this));
        return descriptionId;
    }

    public ChatFormatting[] getName() {
        return name;
    }

    public ChatFormatting[] getDesc() {
        return desc;
    }

    public int fortune() {
        return fortune;
    }

    public int looting() {
        return looting;
    }

    public static Builder builder(ResourceLocation icon) {
        return new Builder(icon);
    }

    public static class Builder {

        private final ResourceLocation icon;
        private final Map<Holder<Attribute>, AttributeModifier> attributeModifiers = Maps.newHashMap();
        private ChatFormatting[] name = new ChatFormatting[]{};
        private ChatFormatting[] desc = new ChatFormatting[]{};
        private int fortune = 0;
        private int looting = 0;

        private Builder(ResourceLocation icon) {
            this.icon = icon;
        }

        public Builder addAttributeModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
            attributeModifiers.put(attribute, modifier);
            return this;
        }

        public Builder addAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
            return addAttributeModifier(attribute, new AttributeModifier(id, amount, operation));
        }

        public Builder nameFormatting(ChatFormatting... name) {
            this.name = name;
            return this;
        }

        public Builder descFormatting(ChatFormatting... desc) {
            this.desc = desc;
            return this;
        }

        public Builder fortune(int fortune) {
            this.fortune = fortune;
            return this;
        }

        public Builder looting(int looting) {
            this.looting = looting;
            return this;
        }

        public Rune build() {
            return new Rune(icon, attributeModifiers, name, desc, fortune, looting);
        }
    }
}
