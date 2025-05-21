package net.arkadiyhimself.fantazia.tags;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public interface FTZPools {

    ResourceKey<StructureTemplatePool> BLACKSTONE_ALTAR_START_POOL = key("blackstone_altar/start_pool");

    private static ResourceKey<StructureTemplatePool> key(String name) {
        return Pools.parseKey(Fantazia.res(name).toString());
    }

    static void bootStrap(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<StructureProcessorList> holdergetter = context.lookup(Registries.PROCESSOR_LIST);
        Holder<StructureProcessorList> holder = holdergetter.getOrThrow(ProcessorLists.OUTPOST_ROT);
        HolderGetter<StructureTemplatePool> holdergetter1 = context.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> holder1 = holdergetter1.getOrThrow(Pools.EMPTY);
        context.register(BLACKSTONE_ALTAR_START_POOL, new StructureTemplatePool(holder1, ImmutableList.of(Pair.of(StructurePoolElement.single("fantazia:blackstone_altar"), 1)), StructureTemplatePool.Projection.RIGID));
    }
}
