package net.arkadiyhimself.fantazia.data.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.CustomCriteriaHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record PossessedItemPredicate(HolderSet<Item> itemList, Optional<ResourceLocation> tag, int amount) {
    public static final Codec<PossessedItemPredicate> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("items", HolderSet.empty()).forGetter(PossessedItemPredicate::itemList),
                    ResourceLocation.CODEC.optionalFieldOf("tag").forGetter(PossessedItemPredicate::tag),
                    Codec.INT.optionalFieldOf("amount", 0).forGetter(PossessedItemPredicate::amount)
            ).apply(instance, PossessedItemPredicate::new));

    public boolean matches(@NotNull CustomCriteriaHolder holder) {
        List<Item> obtainedItems = new ArrayList<>(holder.getObtainedItems());
        List<Item> required = new ArrayList<>(itemList.stream().toList().stream().map(Holder::value).toList());

        required.removeIf(obtainedItems::contains);
        boolean empty = required.isEmpty();

        if (tag.isEmpty()) return empty;
        TagKey<Item> itemTagKey = TagKey.create(Registries.ITEM, tag.get());

        List<Item> taggedItems = holder.getOrCreateTagList(itemTagKey);
        return empty && taggedItems.size() >= amount;
    }
}
