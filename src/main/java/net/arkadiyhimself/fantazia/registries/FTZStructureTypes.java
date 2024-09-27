package net.arkadiyhimself.fantazia.registries;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.world.gen.structures.SimpleNetherStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class FTZStructureTypes {
    private FTZStructureTypes() {}
    public static final StructureType<SimpleNetherStructure> SIZE_CHECKING_NETHER_STRUCTURE = registerStructureType("simple_nether_structure", SimpleNetherStructure.CODEC);
    private static <S extends Structure> StructureType<S> registerStructureType(String registryName, Codec<S> codec) {
        return Registry.register(BuiltInRegistries.STRUCTURE_TYPE, new ResourceLocation(Fantazia.MODID, registryName), () -> codec);
    }
    public static void register() {}
}
