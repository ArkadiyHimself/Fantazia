package net.arkadiyhimself.fantazia.registries;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.loot.FantazicLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FTZLootModifiers{
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Fantazia.MODID);
    public static RegistryObject<Codec<? extends IGlobalLootModifier>> FANTAZIC_LOOT_MODIFIER = REGISTER.register("fantazic_loot_modifier", FantazicLootModifier.CODEC);
    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
