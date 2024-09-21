package net.arkadiyhimself.fantazia.data.criteritas;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ObtainTalentTrigger extends SimpleCriterionTrigger<ObtainTalentTrigger.TriggerInstance> {
    private static final ResourceLocation ID = Fantazia.res("talent_obtain");
    public static final ObtainTalentTrigger INSTANCE = new ObtainTalentTrigger();
    @Override
    protected @NotNull TriggerInstance createInstance(@NotNull JsonObject pJson, @NotNull ContextAwarePredicate pPredicate, @NotNull DeserializationContext pDeserializationContext) {
        TalentPredicate[] talentPredicates = TalentPredicate.fromJsonArray(pJson.get("talents"));
        return new TriggerInstance(pPredicate, talentPredicates);
    }
    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }
    public void trigger(@NotNull ServerPlayer pPlayer, @NotNull TalentsHolder talentsHolder, @NotNull BasicTalent talent) {
        this.trigger(pPlayer, triggerInstance -> triggerInstance.matches(talentsHolder, talent));
    }
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final TalentPredicate[] predicates;
        public TriggerInstance(ContextAwarePredicate pPlayer, TalentPredicate[] talentPredicate) {
            super(ObtainTalentTrigger.ID, pPlayer);
            this.predicates = talentPredicate;
        }
        public boolean matches(@NotNull TalentsHolder talentsHolder, BasicTalent obtained) {
            int i = this.predicates.length;
            if (i == 0) return true;
            else if (i != 1) {
                List<TalentPredicate> list = new ObjectArrayList<>(this.predicates);

                for (BasicTalent talent : talentsHolder.getTalents()) list.removeIf(talentPredicate -> talentPredicate.matches(talent));

                return list.isEmpty();
            } else return this.predicates[0].matches(obtained);
        }
    }
}
