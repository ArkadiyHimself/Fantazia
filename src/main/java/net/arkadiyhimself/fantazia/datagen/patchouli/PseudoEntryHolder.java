package net.arkadiyhimself.fantazia.datagen.patchouli;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PseudoEntryHolder(ResourceLocation id, PseudoEntry entry) {

    public boolean equals(Object other) {
        if (this == other) return true;
        else {
            if (other instanceof PseudoEntryHolder pseudoEntryHolder) return this.id.equals(pseudoEntryHolder.id);
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
