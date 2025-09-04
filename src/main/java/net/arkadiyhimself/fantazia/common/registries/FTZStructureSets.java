package net.arkadiyhimself.fantazia.common.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

public interface FTZStructureSets {

    ResourceKey<StructureSet> BLACKSTONE_ALTAR = key("blackstone_altar");

    private static ResourceKey<StructureSet> key(String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET, Fantazia.location(name));
    }

    static void bootStrap(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structureHolderGetter = context.lookup(Registries.STRUCTURE);

        context.register(BLACKSTONE_ALTAR, new StructureSet(
                structureHolderGetter.getOrThrow(FTZStructures.BLACKSTONE_ALTAR), new RandomSpreadStructurePlacement(4, 2, RandomSpreadType.LINEAR, 1738502718)
        ));
    }
}
