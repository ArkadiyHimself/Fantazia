package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.data_component.HiddenPotentialHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZDataComponentTypes {
    private FTZDataComponentTypes() {}
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Fantazia.MODID);

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<HiddenPotentialHolder>> HIDDEN_POTENTIAL = REGISTER.register("hidden_potential", () -> DataComponentType.<HiddenPotentialHolder>builder().persistent(HiddenPotentialHolder.CODEC).networkSynchronized(HiddenPotentialHolder.STREAM_CODEC).cacheEncoding().build());

    public static void register(IEventBus iEventBus) {
        REGISTER.register(iEventBus);
    }
}
