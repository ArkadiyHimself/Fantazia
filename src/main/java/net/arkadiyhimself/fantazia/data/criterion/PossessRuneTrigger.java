package net.arkadiyhimself.fantazia.data.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.CustomCriteriaHolder;
import net.arkadiyhimself.fantazia.data.predicate.PossessedRunePredicate;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PossessRuneTrigger extends SimpleCriterionTrigger<PossessRuneTrigger.TriggerInstance> {

    public static final PossessRuneTrigger INSTANCE = new PossessRuneTrigger();

    public void trigger(@NotNull ServerPlayer pPlayer, @NotNull CustomCriteriaHolder customCriteriaHolder) {
        this.trigger(pPlayer, triggerInstance -> triggerInstance.matches(customCriteriaHolder));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(PossessedRunePredicate predicate) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<PossessRuneTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        PossessedRunePredicate.CODEC.optionalFieldOf("possessed", PossessedRunePredicate.builder().build()).forGetter(PossessRuneTrigger.TriggerInstance::predicate))
                .apply(instance, PossessRuneTrigger.TriggerInstance::new));

        public static Criterion<PossessRuneTrigger.TriggerInstance> runesOfTag(TagKey<Rune> tagKey, int amount) {
            PossessedRunePredicate predicate = PossessedRunePredicate.builder().tagKey(tagKey).tagAmount(amount).build();
            return INSTANCE.createCriterion(new PossessRuneTrigger.TriggerInstance(predicate));
        }

        @SafeVarargs
        public static Criterion<PossessRuneTrigger.TriggerInstance> specificRunes(Holder<Rune>... holders) {
            PossessedRunePredicate predicate = PossessedRunePredicate.builder().addRunes(holders).build();
            return INSTANCE.createCriterion(new PossessRuneTrigger.TriggerInstance(predicate));
        }

        public static Criterion<PossessRuneTrigger.TriggerInstance> amountOfRunes(int amount) {
            PossessedRunePredicate predicate = PossessedRunePredicate.builder().runes(amount).build();
            return INSTANCE.createCriterion(new PossessRuneTrigger.TriggerInstance(predicate));
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
