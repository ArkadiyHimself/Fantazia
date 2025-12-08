package net.arkadiyhimself.fantazia.datagen.advancement.the_worldliness;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class EntryAdvancementHolder {

    private final String category;
    private final Advancement.Builder builder = new Advancement.Builder();

    public EntryAdvancementHolder(String category) {
        this.category = category;
    }

    public EntryAdvancementHolder addCriterion(String key, Criterion<?> criterion) {
        builder.addCriterion(key, criterion);
        return this;
    }

    public EntryAdvancementHolder requirements(AdvancementRequirements.Strategy strategy) {
        builder.requirements(strategy);
        return this;
    }

    public EntryAdvancementHolder requirements(AdvancementRequirements requirements) {
        builder.requirements(requirements);
        return this;
    }

    public void save(Consumer<AdvancementHolder> consumer, ItemLike item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem()).withPrefix("the_worldliness/" + category + "/");
        builder.save(consumer, id.toString());
    }

    public void save(Consumer<AdvancementHolder> consumer, ResourceLocation id) {
        builder.save(consumer, id.getNamespace() + ":the_worldliness/" + category + "/" + id.getPath());
    }
}
