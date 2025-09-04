package net.arkadiyhimself.fantazia.data.datagen.loot_modifier;

import net.arkadiyhimself.fantazia.data.loot.LootModifier;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record LootModifierHolder(ResourceLocation id, LootModifier.Builder builder) {

    public boolean equals(Object other) {
        if (this == other) return true;
        else {
            if (other instanceof LootModifierHolder lootModifierHolder) return this.id.equals(lootModifierHolder.id);
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
