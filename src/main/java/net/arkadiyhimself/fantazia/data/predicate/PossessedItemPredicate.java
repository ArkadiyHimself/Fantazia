package net.arkadiyhimself.fantazia.data.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.CustomCriteriaHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record PossessedItemPredicate(List<Item> itemList, Optional<TagKey<Item>> tag, int amount) {

    public static final Codec<PossessedItemPredicate> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(BuiltInRegistries.ITEM.byNameCodec().listOf().optionalFieldOf("items", Lists.newArrayList()).forGetter(PossessedItemPredicate::itemList),
                    TagKey.codec(Registries.ITEM).optionalFieldOf("tag").forGetter(PossessedItemPredicate::tag),
                    Codec.INT.optionalFieldOf("amount", 0).forGetter(PossessedItemPredicate::amount)
            ).apply(instance, PossessedItemPredicate::new));

    public boolean matches(@NotNull CustomCriteriaHolder holder) {
        List<Item> obtainedItems = new ArrayList<>(holder.getObtainedItems());
        List<Item> required = new ArrayList<>(List.copyOf(itemList));

        required.removeIf(obtainedItems::contains);
        boolean empty = required.isEmpty();

        if (tag.isEmpty()) return empty;

        List<Item> taggedItems = holder.getOrCreateTagList(tag.get());
        return empty && taggedItems.size() >= amount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<Item> itemList = Lists.newArrayList();
        private TagKey<Item> tag = null;
        private int amount = 0;

        public Builder addItems(ItemLike... items) {
            for (ItemLike itemLike : items) itemList.add(itemLike.asItem());
            return this;
        }

        public Builder tag(TagKey<Item> tagKey) {
            this.tag = tagKey;
            return this;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public PossessedItemPredicate build() {
            return new PossessedItemPredicate(itemList, Optional.ofNullable(tag), amount);
        }
    }
}
