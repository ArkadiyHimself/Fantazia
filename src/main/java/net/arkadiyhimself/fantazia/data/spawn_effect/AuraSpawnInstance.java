package net.arkadiyhimself.fantazia.data.spawn_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public record AuraSpawnInstance(Holder<Aura> basicAura, int amplifier) implements ISpawnEffectApplier {

    @Override
    public void addEffects(LivingEntity livingEntity) {
        livingEntity.getData(FTZAttachmentTypes.ADDED_AURAS).addAura(basicAura);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        ResourceLocation auraID = FantazicRegistries.AURAS.getKey(basicAura.value());
        if (auraID == null) throw new IllegalStateException("Aura's id has not been found");
        tag.putString("aura", auraID.toString());
        tag.putInt("amplifier", amplifier);

        return tag;
    }

    public static AuraSpawnInstance deserialize(CompoundTag tag) {
        ResourceLocation auraID = ResourceLocation.parse(tag.getString("aura"));
        Optional<Holder.Reference<Aura>> basicAuraReference = FantazicRegistries.AURAS.getHolder(auraID);
        if (basicAuraReference.isEmpty()) throw new IllegalStateException("Aura has not been found: " + auraID);

        int amplifier = tag.getInt("amplifier");
        return new AuraSpawnInstance(basicAuraReference.get(), amplifier);
    }

    public record Builder(Holder<Aura> aura, int amplifier) {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FantazicRegistries.AURAS.holderByNameCodec().fieldOf("aura").forGetter(Builder::aura),
                Codec.INT.optionalFieldOf("amplifier", 0).forGetter(Builder::amplifier)
        ).apply(instance, Builder::new));

        public AuraSpawnInstance build() {
            return new AuraSpawnInstance(aura, amplifier);
        }
    }
}
