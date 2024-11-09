package net.arkadiyhimself.fantazia.data.spawn;

import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.FantazicRegistries;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.util.library.pseudorandom.PSERANInstance;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public record AuraSpawnInstance(BasicAura<?> basicAura, PSERANInstance pseranInstance) {

    public void tryAddAura(LivingEntity livingEntity) {
        if (!pseranInstance.performAttempt()) return;
        livingEntity.getData(FTZAttachmentTypes.ADDED_AURAS).addAura(basicAura);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        ResourceLocation auraID = FantazicRegistries.AURAS.getKey(basicAura);
        if (auraID == null) throw new IllegalStateException("Aura's id has not been found");
        tag.putString("aura", auraID.toString());
        tag.put("random", pseranInstance.serialize());

        return tag;
    }

    public static AuraSpawnInstance deserialize(CompoundTag tag) {
        ResourceLocation auraID = ResourceLocation.parse(tag.getString("aura"));
        Optional<Holder.Reference<BasicAura<?>>> basicAuraReference = FantazicRegistries.AURAS.getHolder(auraID);
        if (basicAuraReference.isEmpty()) throw new IllegalStateException("Aura has not been found: " + auraID);

        PSERANInstance pseranInstance = PSERANInstance.deserialize(tag.getCompound("random"));

        return new AuraSpawnInstance(basicAuraReference.get().value(), pseranInstance);
    }

    public static class Builder {

        private final Holder<BasicAura<?>> basicAuraHolder;
        private final double chance;

        public Builder(Holder<BasicAura<?>> basicAuraHolder, double chance) {
            this.basicAuraHolder = basicAuraHolder;
            this.chance = chance;
        }

        public AuraSpawnInstance build() {
            return new AuraSpawnInstance(basicAuraHolder.value(), new PSERANInstance(chance));
        }
    }
}
