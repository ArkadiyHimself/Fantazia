package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.minecraft.tags.TagKey;

public interface FTZSpellTags {

    TagKey<AbstractSpell> IS_CHAINED = create("is_chained");

    TagKey<AbstractSpell> NOT_BLOCKABLE = createTargeted("not_blockable");
    TagKey<AbstractSpell> NOT_REFLECTABLE = createTargeted("not_reflectable");
    TagKey<AbstractSpell> THROUGH_WALLS = createTargeted("through_walls");

    private static TagKey<AbstractSpell> create(String name) {
        return TagKey.create(FantazicRegistries.Keys.SPELL, Fantazia.location(name));
    }

    private static TagKey<AbstractSpell> createTargeted(String pName) {
        return TagKey.create(FantazicRegistries.Keys.SPELL, Fantazia.location("targeted/" + pName));
    }
}
