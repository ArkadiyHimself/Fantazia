package net.arkadiyhimself.fantazia.data.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

        public static final Codec<EuphoriaTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("ticks").forGetter(TriggerInstance::ticks),
                Codec.INT.optionalFieldOf("peakTicks").forGetter(TriggerInstance::peaking),
                Codec.INT.optionalFieldOf("combo").forGetter(TriggerInstance::combo)
        ).apply(instance, EuphoriaTrigger.TriggerInstance::new));

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }

        private boolean matches(int currentTicks, int peakingTicks, int currentCombo) {
            return (ticks.isEmpty() || currentTicks >= ticks.get()) && (peaking.isEmpty() || peakingTicks > peaking.get()) && (combo.isEmpty() || currentCombo >= combo.get());
        }
    }
}
