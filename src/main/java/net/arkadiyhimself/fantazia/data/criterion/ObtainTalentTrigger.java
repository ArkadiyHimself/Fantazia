package net.arkadiyhimself.fantazia.data.criterion;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.data.talent.types.ITalent;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ObtainTalentTrigger extends SimpleCriterionTrigger<ObtainTalentTrigger.TriggerInstance> {

    public static final ObtainTalentTrigger INSTANCE = new ObtainTalentTrigger();

    public void trigger(@NotNull ServerPlayer pPlayer, @NotNull TalentsHolder talentsHolder) {
        this.trigger(pPlayer, triggerInstance -> triggerInstance.matches(talentsHolder));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(List<TalentPredicate> talentPredicate) implements SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(TalentPredicate.CODEC.listOf().optionalFieldOf("talents", List.of()).forGetter(TriggerInstance::talentPredicate)).apply(instance, TriggerInstance::new));

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }

        private boolean matches(@NotNull TalentsHolder talentsHolder) {
            if (talentPredicate.isEmpty()) return true;
            List<ITalent> talents = Lists.newArrayList(talentsHolder.getTalents());
            List<TalentPredicate> predicates = new java.util.ArrayList<>(List.copyOf(talentPredicate));
            predicates.removeIf(talentPredicate1 -> talentPredicate1.matches(talents));
            return predicates.isEmpty();
        }
    }
}
