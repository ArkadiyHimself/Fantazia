package net.arkadiyhimself.fantazia.common.registries;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.RechargeableToolData;
import net.arkadiyhimself.fantazia.util.simpleobjects.RegistryObjectList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;


public class FTZDataMapTypes {

    public static final DataMapType<EntityType<?>, Item> SKULLS;
    public static final DataMapType<Item, RechargeableToolData> RECHARGEABLE_TOOLS;
    public static final DataMapType<MobEffect, RegistryObjectList<EntityType<?>>> MOB_EFFECT_WHITE_LIST;
    public static final DataMapType<MobEffect, RegistryObjectList<EntityType<?>>> MOB_EFFECT_BLACK_LIST;

    private static <T, M> DataMapType<T, M> createDataMap(String name, ResourceKey<Registry<T>> registry, Codec<M> codec) {
        return DataMapType.builder(Fantazia.location(name), registry, codec).build();
    }

    static {
        SKULLS = createDataMap("skulls", Registries.ENTITY_TYPE, BuiltInRegistries.ITEM.byNameCodec());
        RECHARGEABLE_TOOLS = createDataMap("rechargeable_tool", Registries.ITEM, RechargeableToolData.CODEC);
        MOB_EFFECT_WHITE_LIST = createDataMap(
                "white_list",
                Registries.MOB_EFFECT,
                RegistryObjectList.codec(Registries.ENTITY_TYPE, "entity_types", "tags")
        );
        MOB_EFFECT_BLACK_LIST = createDataMap(
                "black_list",
                Registries.MOB_EFFECT,
                RegistryObjectList.codec(Registries.ENTITY_TYPE, "entity_types", "tags")
        );
    }
}
