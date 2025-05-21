package net.arkadiyhimself.fantazia.registries;

import com.mojang.serialization.MapCodec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.loot.FantazicLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class FTZLootModifiers {

    private FTZLootModifiers() {}

    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Fantazia.MODID);

    public static DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<? extends IGlobalLootModifier>> FANTAZIC_LOOT_MODIFIER = REGISTER.register("fantazic_loot_modifier", () -> FantazicLootModifier.CODEC);

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
