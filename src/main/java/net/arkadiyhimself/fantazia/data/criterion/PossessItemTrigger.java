package net.arkadiyhimself.fantazia.data.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.CustomCriteriaHolder;
import net.arkadiyhimself.fantazia.data.predicate.PossessedItemPredicate;
import net.arkadiyhimself.fantazia.data.predicate.PossessedRunePredicate;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PossessItemTrigger extends SimpleCriterionTrigger<PossessItemTrigger.TriggerInstance> {

    public static final PossessItemTrigger INSTANCE = new PossessItemTrigger();

    public void trigger(@NotNull ServerPlayer pPlayer, @NotNull CustomCriteriaHolder customCriteriaHolder) {
        this.trigger(pPlayer, triggerInstance -> triggerInstance.matches(customCriteriaHolder));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(PossessedItemPredicate predicate) implements SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PossessedItemPredicate.CODEC.optionalFieldOf("possessed", PossessedItemPredicate.builder().build()).forGetter(TriggerInstance::predicate)).apply(instance, TriggerInstance::new));

        public static Criterion<TriggerInstance> itemsOfTag(TagKey<Item> tagKey, int amount) {
            PossessedItemPredicate predicate = PossessedItemPredicate.builder().tag(tagKey).amount(amount).build();
            return INSTANCE.createCriterion(new TriggerInstance(predicate));
        }

        public static Criterion<TriggerInstance> specificItems(ItemLike... items) {
            PossessedItemPredicate predicate = PossessedItemPredicate.builder().addItems(items).build();
            return INSTANCE.createCriterion(new TriggerInstance(predicate));
        }

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }

        private boolean matches(@NotNull CustomCriteriaHolder holder) {
            return predicate().matches(holder);
        }
    }
}
