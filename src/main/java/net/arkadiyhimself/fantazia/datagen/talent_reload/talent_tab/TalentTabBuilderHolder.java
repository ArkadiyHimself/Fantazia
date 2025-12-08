package net.arkadiyhimself.fantazia.datagen.talent_reload.talent_tab;

import net.arkadiyhimself.fantazia.client.screen.TalentTab;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TalentTabBuilderHolder(ResourceLocation id, TalentTab.Builder talent) {

    public boolean equals(Object other) {
        if (this == other) return true;
        else {
            if (other instanceof TalentTabBuilderHolder talentTabBuilderHolder) return this.id.equals(talentTabBuilderHolder.id);
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
