package net.arkadiyhimself.fantazia.common.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.criterion.*;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZCriterionTriggers {

    private static final DeferredRegister<CriterionTrigger<?>> REGISTER = DeferredRegister.create(Registries.TRIGGER_TYPE, Fantazia.MODID);

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);

        REGISTER.register("talent_obtain", () -> ObtainTalentTrigger.INSTANCE);
        REGISTER.register("possess_item", () -> PossessItemTrigger.INSTANCE);
        REGISTER.register("melee_block", () -> MeleeBlockTrigger.INSTANCE);
        REGISTER.register("euphoria", () -> EuphoriaTrigger.INSTANCE);
        REGISTER.register("possess_rune", () -> PossessRuneTrigger.INSTANCE);
        REGISTER.register("use_spell", () -> UseSpellTrigger.INSTANCE);
        REGISTER.register("read_blueprint", () -> ReadBlueprintTrigger.INSTANCE);
    }

}
