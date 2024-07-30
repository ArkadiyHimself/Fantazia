package net.arkadiyhimself.fantazia.registries;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.util.FantazicLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class FTZLootModifiers extends FTZRegistry<Codec<? extends IGlobalLootModifier>>{
    private static final FTZLootModifiers INSTANCE = new FTZLootModifiers();
    private FTZLootModifiers() {
        super(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS);
        this.register("fantazic_loot_modifier", FantazicLootModifier.CODEC::get);
    }
}
