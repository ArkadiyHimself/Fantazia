package net.arkadiyhimself.fantazia.data.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.arkadiyhimself.fantazia.data.predicate.BlueprintPredicate;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ReadBlueprintTrigger extends SimpleCriterionTrigger<ReadBlueprintTrigger.TriggerInstance> {

    public static final ReadBlueprintTrigger INSTANCE = new ReadBlueprintTrigger();

    public void trigger(@NotNull ServerPlayer pPlayer, @NotNull Holder<Blueprint> blueprintHolder) {
        this.trigger(pPlayer, triggerInstance -> triggerInstance.matches(blueprintHolder));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<BlueprintPredicate> blueprintPredicate) implements SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        BlueprintPredicate.CODEC.optionalFieldOf("blueprint").forGetter(TriggerInstance::blueprintPredicate))
                .apply(instance, TriggerInstance::new));

        public TriggerInstance(BlueprintPredicate.Builder builder) {
            this(Optional.of(builder.build()));
        }

        public TriggerInstance() {
            this(Optional.empty());
        }

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }

        @SafeVarargs
        public static Criterion<TriggerInstance> specific(Holder<Blueprint>... talents) {
            return INSTANCE.createCriterion(new TriggerInstance(BlueprintPredicate.builder().of(talents)));
        }

        public static Criterion<TriggerInstance> any() {
            return INSTANCE.createCriterion(new TriggerInstance());
        }

        private boolean matches(@NotNull Holder<Blueprint> holder) {
            return blueprintPredicate.map(predicate -> predicate.test(holder)).orElse(true);
        }
    }
}
