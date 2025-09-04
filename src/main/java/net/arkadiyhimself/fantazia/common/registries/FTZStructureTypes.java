package net.arkadiyhimself.fantazia.common.registries;

import com.mojang.serialization.MapCodec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.datagen.worldgen.structures.SimpleNetherStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZStructureTypes {

    private static final DeferredRegister<StructureType<?>> REGISTER = DeferredRegister.create(Registries.STRUCTURE_TYPE, Fantazia.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<SimpleNetherStructure>> SIMPLE_NETHER_STRUCTURE = REGISTER.register("simple_nether_structure", () -> codecIntoSupplier(SimpleNetherStructure.CODEC));

    private static <T extends Structure> StructureType<T> codecIntoSupplier(MapCodec<T> structureCodec) {
        return () -> structureCodec;
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
