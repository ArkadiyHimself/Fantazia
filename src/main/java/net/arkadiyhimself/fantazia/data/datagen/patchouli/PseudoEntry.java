package net.arkadiyhimself.fantazia.data.datagen.patchouli;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record PseudoEntry(String name, PseudoIcon pseudoIcon, ResourceLocation category, Optional<ResourceLocation> advancement, List<PseudoPage> pseudoPages) {

    public static final Codec<PseudoEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("ident").forGetter(PseudoEntry::name),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(entry -> entry.pseudoIcon.getId()),
            ResourceLocation.CODEC.fieldOf("category").forGetter(PseudoEntry::category),
            ResourceLocation.CODEC.optionalFieldOf("advancement").forGetter(PseudoEntry::advancement),
            PseudoPage.CODEC.listOf().fieldOf("pages").forGetter(PseudoEntry::pseudoPages)
    ).apply(instance, PseudoEntry::decode));

    private static PseudoEntry decode(String name, ResourceLocation icon, ResourceLocation category, Optional<ResourceLocation> advancement, List<PseudoPage> pseudoPages) {
        throw new IllegalStateException("This object is for Data Gen exclusively!");
    }

    public void save(Consumer<PseudoEntryHolder> consumer, ResourceLocation id) {
        consumer.accept(new PseudoEntryHolder(id,this));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name = null;
        private PseudoIcon pseudoIcon = null;
        private ResourceLocation category = null;
        private ResourceLocation advancement = null;
        private final List<PseudoPage> pseudoPages = Lists.newArrayList();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder icon(ResourceLocation pseudoIcon) {
            this.pseudoIcon = PseudoIcon.fromId(pseudoIcon);
            return this;
        }

        public Builder icon(Item item) {
            this.pseudoIcon = PseudoIcon.fromItem(item);
            return this;
        }

        public Builder category(ResourceLocation category) {
            this.category = category;
            return this;
        }

        public Builder advancement(ResourceLocation advancement) {
            this.advancement = advancement;
            return this;
        }

        public Builder addPseudoPage(PseudoPage pseudoPage) {
            this.pseudoPages.add(pseudoPage);
            return this;
        }

        public PseudoEntry build() {
            if (name == null) throw new IllegalStateException("Could not build entry: the ident is null");
            if (pseudoIcon == null) throw new IllegalStateException("Could not build entry: the icon is null");
            if (category == null) throw new IllegalStateException("Could not build entry: the category is null");
            if (pseudoPages.isEmpty()) throw new IllegalStateException("Could not build entry: the pages are empty");

            return new PseudoEntry(name, pseudoIcon, category, Optional.ofNullable(advancement), pseudoPages);
        }
    }
}
