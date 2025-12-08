package net.arkadiyhimself.fantazia.datagen.patchouli;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record PseudoPage(ResourceLocation type, String text, Optional<String> title, Optional<String> item, Optional<ResourceLocation> recipe, List<ResourceLocation> images, boolean border) {

    public static final Codec<PseudoPage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("type").forGetter(PseudoPage::type),
            Codec.STRING.fieldOf("text").forGetter(PseudoPage::text),
            Codec.STRING.optionalFieldOf("title").forGetter(PseudoPage::title),
            Codec.STRING.optionalFieldOf("item").forGetter(PseudoPage::item),
            ResourceLocation.CODEC.optionalFieldOf("recipe").forGetter(PseudoPage::recipe),
            ResourceLocation.CODEC.listOf().optionalFieldOf("images", Lists.newArrayList()).forGetter(PseudoPage::images),
            Codec.BOOL.optionalFieldOf("border",false).forGetter(PseudoPage::border)
    ).apply(instance, PseudoPage::new));

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ResourceLocation type = null;
        private String text = null;
        private String title = null;
        private String item = "";
        private ResourceLocation recipe = null;

        private final List<ResourceLocation> images = Lists.newArrayList();
        private boolean border = false;

        public Builder type(ResourceLocation type) {
            this.type = type;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder item(Item item) {
            if (!this.item.isEmpty()) this.item += ",";
            this.item += BuiltInRegistries.ITEM.getKey(item).toString();
            return this;
        }

        public Builder item(TagKey<Item> tagKey) {
            if (!this.item.isEmpty()) this.item += ",";
            this.item  += "tag:" + tagKey.location().toString();
            return this;
        }

        public Builder recipe(ResourceLocation recipe) {
            this.recipe = recipe;
            return this;
        }

        public Builder recipe(Item recipe) {
            return recipe(BuiltInRegistries.ITEM.getKey(recipe));
        }

        public Builder images(ResourceLocation... images) {
            this.images.addAll(Arrays.stream(images).toList());
            return this;
        }

        public Builder border() {
            this.border = true;
            return this;
        }

        public PseudoPage build() {
            if (type == null) throw new IllegalStateException("Could not build page: the type is null");
            if (text == null) throw new IllegalStateException("Could not build page: the text is null");

            return new PseudoPage(type, text, Optional.ofNullable(title), Optional.ofNullable(item.isEmpty() ? null : item), Optional.ofNullable(recipe), images, border);
        }
    }
}
