package net.arkadiyhimself.fantazia.datagen.effect_from_damage;

import net.arkadiyhimself.fantazia.data.effect_from_damage.EffectFromDamage;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record EffectFromDamageHolder(ResourceLocation id, EffectFromDamage.Builder builder) {

    public boolean equals(Object other) {
        if (this == other) return true;
        else {
            if (other instanceof EffectFromDamageHolder effectFromDamageHolder) return this.id.equals(effectFromDamageHolder.id);
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
