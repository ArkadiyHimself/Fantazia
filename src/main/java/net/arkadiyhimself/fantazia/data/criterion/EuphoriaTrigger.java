package net.arkadiyhimself.fantazia.data.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EuphoriaTrigger extends SimpleCriterionTrigger<EuphoriaTrigger.TriggerInstance> {

    public static final EuphoriaTrigger INSTANCE = new EuphoriaTrigger();

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(@NotNull ServerPlayer serverPlayer, int ticks, int peaking, int combo) {
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(ticks, peaking, combo));
    }

    public record TriggerInstance(Optional<Integer> ticks, Optional<Integer> peaking, Optional<Integer> combo) implements SimpleInstance {

        public static final Codec<EuphoriaTrigger.TriggerInstance> CODEC = RecordCodecBuilder.<EuphoriaTrigger.TriggerInstance>create(instance -> instance.group(
                Codec.INT.optionalFieldOf("ticks").forGetter(TriggerInstance::ticks),
                Codec.INT.optionalFieldOf("peakTicks").forGetter(TriggerInstance::peaking),
                Codec.INT.optionalFieldOf("combo").forGetter(TriggerInstance::combo)
        ).apply(instance, EuphoriaTrigger.TriggerInstance::new)).validate(TriggerInstance::validate);

        public static DataResult<TriggerInstance> validate(TriggerInstance instance) {
            if (instance.ticks.isPresent() && instance.ticks.get() < 0) return DataResult.error(() -> "Can not have negative value for ticks!");
            if (instance.peaking.isPresent() && instance.peaking.get() < 0) return DataResult.error(() -> "Can not have negative value for peaking ticks!");
            if (instance.combo.isPresent() && instance.combo.get() < 0) return DataResult.error(() -> "Can not have negative value for combo!");
            return DataResult.success(instance);
        }

        public static Criterion<TriggerInstance> hasEuphoria() {
            return INSTANCE.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static Criterion<TriggerInstance> tick(int ticks) {
            return INSTANCE.createCriterion(new TriggerInstance(Optional.of(ticks), Optional.empty(), Optional.empty()));
        }

        public static Criterion<TriggerInstance> peaking(int peaking) {
            return INSTANCE.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(peaking), Optional.empty()));
        }

        public static Criterion<TriggerInstance> combo(int combo) {
            return INSTANCE.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(combo)));
        }

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }

        private boolean matches(int currentTicks, int peakingTicks, int currentCombo) {
            return (ticks.isEmpty() || currentTicks >= ticks.get()) && (peaking.isEmpty() || peakingTicks > peaking.get()) && (combo.isEmpty() || currentCombo >= combo.get());
        }
    }
}
