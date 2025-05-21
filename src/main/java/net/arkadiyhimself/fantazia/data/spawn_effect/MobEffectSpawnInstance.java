package net.arkadiyhimself.fantazia.data.spawn_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public record MobEffectSpawnInstance(@NotNull Holder<MobEffect> mobEffect, int amplifier, boolean hidden) implements ISpawnEffectApplier {

    public MobEffectSpawnInstance(ResourceLocation mobEffect, int amplifier, boolean hidden) {
        this(BuiltInRegistries.MOB_EFFECT.getHolder(mobEffect).orElseThrow(), amplifier, hidden);
    }

    @Override
    public void addEffects(LivingEntity livingEntity) {
        livingEntity.addEffect(new MobEffectInstance(mobEffect, -1, amplifier, true, hidden, true));
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        ResourceLocation effectID = BuiltInRegistries.MOB_EFFECT.getKey(mobEffect.value());
        if (effectID == null) throw new IllegalStateException("MobEffect's id has not been found");
        tag.putString("effect", effectID.toString());

        tag.putInt("amplifier", amplifier);
        tag.putBoolean("hidden", hidden);

        return tag;
    }

    public static MobEffectSpawnInstance deserialize(CompoundTag tag) {
        ResourceLocation effectID = ResourceLocation.parse(tag.getString("effect"));
        Optional<Holder.Reference<MobEffect>> mobEffect = BuiltInRegistries.MOB_EFFECT.getHolder(effectID);
        if (mobEffect.isEmpty()) throw new IllegalStateException("MobEffect has not been found: " + effectID);

        int amplifier = tag.getInt("amplifier");
        boolean hidden = tag.getBoolean("hidden");

        return new MobEffectSpawnInstance(mobEffect.get(), amplifier, hidden);
    }

    public record Builder(Holder<MobEffect> mobEffect, int amplifier, boolean hidden) {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("mob_effect").forGetter(Builder::mobEffect),
                Codec.INT.optionalFieldOf("amplifier",0).forGetter(Builder::amplifier),
                Codec.BOOL.optionalFieldOf("hidden",false).forGetter(Builder::hidden)
        ).apply(instance, Builder::new));

        public MobEffectSpawnInstance build() {
            return new MobEffectSpawnInstance(mobEffect, amplifier, hidden);
        }
    }
}
