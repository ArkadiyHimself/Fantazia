package net.arkadiyhimself.fantazia.datagen.effect_spawn_applier;

import net.arkadiyhimself.fantazia.data.spawn_effect.EffectSpawnApplier;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record EffectSpawnApplierHolder(ResourceLocation id, EffectSpawnApplier.Builder builder) {

    public boolean equals(Object other) {
        if (this == other) return true;
        else {
            if (other instanceof EffectSpawnApplierHolder effectSpawnApplierHolder) return this.id.equals(effectSpawnApplierHolder.id);
            return false;
        }
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public @NotNull String toString() {
        return this.id.toString();
    }
}
