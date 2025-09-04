package net.arkadiyhimself.fantazia.data.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.advanced.spell.SpellCastResult;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.data.predicate.SpellCastPredicate;
import net.arkadiyhimself.fantazia.data.predicate.SpellPredicate;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class UseSpellTrigger extends SimpleCriterionTrigger<UseSpellTrigger.TriggerInstance> {

    public static final UseSpellTrigger INSTANCE = new UseSpellTrigger();

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer serverPlayer, Holder<AbstractSpell> spell, SpellCastResult result) {
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(serverPlayer, spell, result));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> playerPredicate, Optional<SpellPredicate> spellPredicate, Optional<SpellCastPredicate> spellCastPredicate) implements SimpleInstance {

        public static final Codec<UseSpellTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::playerPredicate),
                SpellPredicate.CODEC.optionalFieldOf("spell").forGetter(TriggerInstance::spellPredicate),
                SpellCastPredicate.CODEC.optionalFieldOf("cast_result").forGetter(TriggerInstance::spellCastPredicate)
        ).apply(instance, TriggerInstance::new));

        private boolean matches(ServerPlayer serverPlayer, Holder<AbstractSpell> spell, SpellCastResult result) {
            if (spellPredicate.isPresent() && !spellPredicate.get().test(spell)) return false;
            if (spellCastPredicate.isPresent() && !spellCastPredicate.get().matches(serverPlayer, result)) return false;

            return true;
        }

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return playerPredicate;
        }

        public static TriggerInstance useSpell(Holder<AbstractSpell> holder) {
            return new TriggerInstance(Optional.empty(), Optional.of(SpellPredicate.builder().addSpells(holder).build()), Optional.empty());
        }

        public static TriggerInstance useSpellOn(Holder<AbstractSpell> holder, EntityPredicate targetPredicate) {
            return new TriggerInstance(
                    Optional.empty(),
                    Optional.of(SpellPredicate.builder().addSpells(holder).build()),
                    Optional.of(SpellCastPredicate.builder().success(true).entity(targetPredicate).build())
                    );
        }
    }
}
