
package net.arkadiyhimself.fantazia.common.advanced.rune;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.common.api.AttributeModifierBuilder;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.common.item.ITooltipBuilder;
import net.arkadiyhimself.fantazia.common.registries.custom.Runes;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class Rune implements ITooltipBuilder {

    private ModelResourceLocation icon = null;
    private final ImmutableMap<Holder<Attribute>, AttributeModifierBuilder> attributeModifiers;
    private String descriptionId;
    private final ChatFormatting[] nameFormatting;
    private final ChatFormatting[] descFormatting;
    private final int fortune;
    private final int looting;
    private final Consumer<LivingEntity> onTick;

    private Rune(Map<Holder<Attribute>, AttributeModifierBuilder> attributeModifiers, ChatFormatting[] nameFormatting, ChatFormatting[] descFormatting, int fortune, int looting, Consumer<LivingEntity> onTick) {
        this.attributeModifiers = ImmutableMap.copyOf(attributeModifiers);
        this.nameFormatting = nameFormatting;
        this.descFormatting = descFormatting;
        this.fortune = fortune;
        this.looting = looting;
        this.onTick = onTick;
    }

    @Override
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();

        String desc = getDescriptionId();

        int lines = 0;
        try {
            String amo = Component.translatable(desc + ".lines").getString();
            lines = Integer.parseInt(amo);
        } catch (NumberFormatException ignored) {}

        if (lines > 0) {
            for (int i = 1; i <= lines; i++) components.add(Component.translatable(desc + "." + i).withStyle(getDescFormatting()));
        }

        lines = 0;
        try {
            String amo = Component.translatable(desc + ".extra.lines").getString();
            lines = Integer.parseInt(amo);
        } catch (NumberFormatException ignored) {}

        if (lines > 0) {
            components.add(Component.literal(" "));
            for (int i = 1; i <= lines; i++) components.add(Component.translatable(desc + ".extra." + i).withStyle(getDescFormatting()));
        }

        return components;
    }

    public Component getNameComponent() {
        return Component.translatable(getDescriptionId()).withStyle(getNameFormatting());
    }

    public void onTick(LivingEntity livingEntity) {
        this.onTick.accept(livingEntity);
    }

    public ModelResourceLocation getIcon() {
        if (icon == null) {
            ResourceLocation id = FantazicRegistries.RUNES.getKey(this);
            this.icon = Fantazia.modelLocation(id.withPrefix("rune/"));
        }
        return icon;
    }

    public ImmutableMap<Holder<Attribute>, AttributeModifier> getAttributeModifiers() {
        ImmutableMap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMap.builder();
        for (Map.Entry<Holder<Attribute>, AttributeModifierBuilder> entry : attributeModifiers.entrySet())
            builder.put(entry.getKey(), entry.getValue().build(FantazicRegistries.RUNES.getKey(this)));
        return builder.build();
    }

    public String getDescriptionId() {
        if (descriptionId == null) descriptionId = Util.makeDescriptionId("rune", FantazicRegistries.RUNES.getKey(this));
        return descriptionId;
    }

    public ChatFormatting[] getNameFormatting() {
        return nameFormatting;
    }

    public ChatFormatting[] getDescFormatting() {
        return descFormatting;
    }

    public int fortune() {
        return fortune;
    }

    public int looting() {
        return looting;
    }

    public boolean isEmpty() {
        return this == Runes.EMPTY.value();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Map<Holder<Attribute>, AttributeModifierBuilder> attributeModifiers = Maps.newHashMap();
        private ChatFormatting[] name = new ChatFormatting[]{};
        private ChatFormatting[] desc = new ChatFormatting[]{};
        private int fortune = 0;
        private int looting = 0;
        private Consumer<LivingEntity> onTick = livingEntity -> {};

        private Builder() {}

        public Builder addAttributeModifier(Holder<Attribute> attribute, AttributeModifierBuilder modifier) {
            attributeModifiers.put(attribute, modifier);
            return this;
        }

        public Builder addAttributeModifier(Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
            return addAttributeModifier(attribute, new AttributeModifierBuilder(amount, operation));
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

        public Builder onTick(Consumer<LivingEntity> onTick) {
            this.onTick = onTick;
            return this;
        }

        public Rune build() {
            return new Rune(attributeModifiers, name, desc, fortune, looting, onTick);
        }
    }
}
