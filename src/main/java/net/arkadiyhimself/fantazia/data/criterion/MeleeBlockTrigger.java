package net.arkadiyhimself.fantazia.data.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MeleeBlockTrigger extends SimpleCriterionTrigger<MeleeBlockTrigger.TriggerInstance> {

    public static final MeleeBlockTrigger INSTANCE = new MeleeBlockTrigger();

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(@NotNull ServerPlayer player, int blockAmount, int parryAmount, boolean parried, @Nullable LivingEntity blocked) {
        LootContext lootcontext = blocked == null ? null : EntityPredicate.createContext(player, blocked);
        this.trigger(player, triggerInstance -> triggerInstance.matches(lootcontext, blockAmount, parryAmount, parried));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> entityPredicate, Optional<Integer> blocks, Optional<Integer> parries, boolean mustParry) implements SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entityPredicate),
                Codec.INT.optionalFieldOf("blocks").forGetter(TriggerInstance::blocks),
                Codec.INT.optionalFieldOf("parries").forGetter(TriggerInstance::parries),
                Codec.BOOL.optionalFieldOf("mustParry", false).forGetter(TriggerInstance::mustParry)
                ).apply(instance, TriggerInstance::new));

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }

        private boolean matches(LootContext context, int blockAmount, int parryAmount, boolean parried) {
            if (parries.isPresent() && parries.get() > parryAmount) return false;
            if (blocks.isPresent() && blocks.get() > blockAmount) return false;
            if (entityPredicate.isPresent() && !entityPredicate.get().matches(context)) return false;
            if (mustParry && !parried) return false;
            return true;
        }
    }
}
