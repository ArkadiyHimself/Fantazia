package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.minecraft.tags.TagKey;

public interface FTZSpellTags {
    TagKey<AbstractSpell> NOT_BLOCKABLE = createTargeted("not_blockable");
    TagKey<AbstractSpell> NOT_REFLECTABLE = createTargeted("not_reflectable");
    TagKey<AbstractSpell> THROUGH_WALLS = createTargeted("through_walls");
    private static TagKey<AbstractSpell> createTargeted(String pName) {
        return TagKey.create(FantazicRegistries.Keys.SPELL, Fantazia.res("targeted/" + pName));
    }
}
