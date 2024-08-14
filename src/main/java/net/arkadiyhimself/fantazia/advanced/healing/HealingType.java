package net.arkadiyhimself.fantazia.advanced.healing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.util.library.RandomList;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record HealingType(String id, float exhaustion, RandomList<ResourceLocation> particleTypes) {
    public static final Codec<HealingType> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(HealingType::id),
            Codec.FLOAT.optionalFieldOf("exhaustion", 0f).forGetter(HealingType::exhaustion),
            ResourceLocation.CODEC.listOf().optionalFieldOf("particles", RandomList.emptyRandomList()).forGetter(HealingType::particleTypes))
            .apply(instance, HealingType::new));

    public HealingType(String id, float exhaustion, List<ResourceLocation> particleTypes) {
        this(id, exhaustion, new RandomList<>(particleTypes));
    }
}
