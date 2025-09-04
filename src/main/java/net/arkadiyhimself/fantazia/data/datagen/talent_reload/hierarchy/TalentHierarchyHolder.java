package net.arkadiyhimself.fantazia.data.datagen.talent_reload.hierarchy;

import net.arkadiyhimself.fantazia.data.talent.TalentHierarchyBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TalentHierarchyHolder(ResourceLocation id, TalentHierarchyBuilder hierarchy) {

    public boolean equals(Object other) {
        if (this == other) return true;
        else {
            if (other instanceof TalentHierarchyHolder talentHolder) return this.id.equals(talentHolder.id);
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
