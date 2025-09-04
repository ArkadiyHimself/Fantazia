package net.arkadiyhimself.fantazia.common.registries;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;


public class FTZDataMapTypes {

    public static final DataMapType<EntityType<?>, Item> SKULLS;

    private static <T, M> DataMapType<T, M> createDataMap(String name, ResourceKey<Registry<T>> registry, Codec<M> codec) {
        return DataMapType.builder(Fantazia.location(name), registry, codec).build();
    }

    static {
        SKULLS = createDataMap("skulls", Registries.ENTITY_TYPE, BuiltInRegistries.ITEM.byNameCodec());
    }
}
