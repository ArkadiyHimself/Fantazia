package net.arkadiyhimself.fantazia.data.datagen.talent_reload.talent;

import net.arkadiyhimself.fantazia.data.talent.Talent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TalentBuilderHolder(ResourceLocation id, Talent.Builder talent) {

    public boolean equals(Object other) {
        if (this == other) return true;
        else {
            if (other instanceof TalentBuilderHolder talentBuilderHolder) return this.id.equals(talentBuilderHolder.id);
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
