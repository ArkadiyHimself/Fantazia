package net.arkadiyhimself.fantazia.data.datagen.patchouli;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

public record PseudoCategory(String name, String description, PseudoIcon icon) {

    public static final Codec<PseudoCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("ident").forGetter(PseudoCategory::name),
            Codec.STRING.fieldOf("description").forGetter(PseudoCategory::description),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(category -> category.icon.getId())
    ).apply(instance, PseudoCategory::decode));

    private static PseudoCategory decode(String name, String description, ResourceLocation icon) {
        throw new IllegalStateException("This object is for Data Gen exclusively!");
    }

    public static Builder builder() {
        return new Builder();
    }

    public void save(Consumer<PseudoCategoryHolder> consumer, ResourceLocation id) {
        consumer.accept(new PseudoCategoryHolder(id,this));
    }

    public static class Builder {

        private String name = null;
        private String description = null;
        private PseudoIcon pseudoIcon = null;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder icon(Item icon) {
            this.pseudoIcon = PseudoIcon.fromItem(icon);
            return this;
        }

        public Builder icon(ResourceLocation icon) {
            this.pseudoIcon = PseudoIcon.fromId(icon);
            return this;
        }

        public PseudoCategory build() {
            if (name == null) throw new IllegalStateException("Could not build category: the ident is null");
            if (description == null) throw new IllegalStateException("Could not build category: the description is null");
            if (pseudoIcon == null) throw new IllegalStateException("Could not build category: the icon is null");

            return new PseudoCategory(name, description, pseudoIcon);
        }
    }
}
