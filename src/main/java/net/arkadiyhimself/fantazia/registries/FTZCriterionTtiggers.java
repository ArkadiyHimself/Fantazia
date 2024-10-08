package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.criteritas.ObtainTalentTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZCriterionTtiggers {
    private FTZCriterionTtiggers() {}
    private static final DeferredRegister<CriterionTrigger<?>> REGISTER = DeferredRegister.create(Registries.TRIGGER_TYPE, Fantazia.MODID);
    public static final DeferredHolder<CriterionTrigger<?>, CriterionTrigger<ObtainTalentTrigger.TriggerInstance>> OBTAIN_TALENT = REGISTER.register("talent_obtain", () -> ObtainTalentTrigger.INSTANCE);
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
