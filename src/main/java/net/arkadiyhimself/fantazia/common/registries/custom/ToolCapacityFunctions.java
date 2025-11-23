package net.arkadiyhimself.fantazia.common.registries.custom;

import com.mojang.serialization.MapCodec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.ToolCapacityLevelFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ToolCapacityFunctions {

    private static final DeferredRegister<MapCodec<? extends ToolCapacityLevelFunction>> REGISTER =
            DeferredRegister.create(FantazicRegistries.TOOL_CAPACITY_LEVEL_FUNCTIONS, Fantazia.MODID);

    private static void register(String name, MapCodec<? extends ToolCapacityLevelFunction> codec) {
        REGISTER.register(name, () -> codec);
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);

        register("linear", ToolCapacityLevelFunction.Linear.CODEC);
        register("constant", ToolCapacityLevelFunction.Constant.CODEC);
    }
}
