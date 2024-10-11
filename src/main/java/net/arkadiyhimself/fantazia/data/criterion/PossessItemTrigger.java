package net.arkadiyhimself.fantazia.data.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.CustomCriteriaHolder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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

    public record TriggerInstance(List<PossessedItemPredicate> possessedItemPredicates) implements SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(PossessedItemPredicate.CODEC.listOf().optionalFieldOf("possessed", Lists.newArrayList()).forGetter(TriggerInstance::possessedItemPredicates)).apply(instance, TriggerInstance::new));

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }

        private boolean matches(@NotNull CustomCriteriaHolder holder) {
            if (possessedItemPredicates.isEmpty()) return true;
            List<PossessedItemPredicate> predicates = new java.util.ArrayList<>(List.copyOf(possessedItemPredicates));
            predicates.removeIf(possessedItemPredicate -> possessedItemPredicate.matches(holder));
            return predicates.isEmpty();
        }
    }
}
