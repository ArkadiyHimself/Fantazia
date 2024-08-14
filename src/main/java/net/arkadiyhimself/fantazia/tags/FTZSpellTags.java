package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.spell.Spell;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.minecraft.tags.TagKey;

public interface FTZSpellTags {
    TagKey<Spell> NOT_BLOCKABLE = createTargeted("not_blockable");
    TagKey<Spell> NOT_REFLECTABLE = createTargeted("not_reflectable");
    TagKey<Spell> THROUGH_WALLS = createTargeted("through_walls");
    private static TagKey<Spell> createTargeted(String pName) {
        return TagKey.create(FantazicRegistry.Keys.SPELL, Fantazia.res("targeted/" + pName));
    }
}
