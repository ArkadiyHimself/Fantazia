package net.arkadiyhimself.fantazia.datagen.patchouli;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PseudoCategoryHolder(ResourceLocation id, PseudoCategory category) {

    public boolean equals(Object other) {
        if (this == other) return true;
        else {
            if (other instanceof PseudoCategoryHolder pseudoCategoryHolder) return this.id.equals(pseudoCategoryHolder.id);
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
