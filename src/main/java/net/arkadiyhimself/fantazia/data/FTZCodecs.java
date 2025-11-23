package net.arkadiyhimself.fantazia.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.util.library.hierarchy.HierarchyType;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageType;

import java.util.ArrayList;
import java.util.List;

public class FTZCodecs {

    public static final Codec<ResourceKey<DamageType>> DAMAGE_TYPE = ResourceKey.codec(Registries.DAMAGE_TYPE);

    public static <T> MapCodec<IHierarchy<T>> hierarchyMapCodec(String typeName, String hierarchyName, Codec<T> codec) {
        return ExtraCodecs.dispatchOptionalValue(typeName, hierarchyName, HierarchyType.CODEC, IHierarchy::getType, hierarchyType -> hierarchyType.getCodec(codec));
    }

    public static <T, M> Codec<Pair<T, M>> pairCodec(String first, String second, Codec<T> tCodec, Codec<M> mCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                tCodec.fieldOf(first).forGetter(Pair::getFirst),
                mCodec.fieldOf(second).forGetter(Pair::getSecond)
        ).apply(instance, Pair::new));
    }

    public static <T> Codec<ArrayList<T>> arrayListCodec(Codec<T> codec) {
        return codec.listOf().xmap(ArrayList::new, List::copyOf);
    }
}
