package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.advanced.spell.Spell;
import net.arkadiyhimself.fantazia.advanced.spell.Spells;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.minecraftforge.registries.RegistryObject;

public class FTZSpells {
    // self
    public static final RegistryObject<Spell> ENTANGLE = FantazicRegistry.SPELLS.register("entangle", () -> Spells.Self.ENTANGLE);
    public static final RegistryObject<Spell> REWIND = FantazicRegistry.SPELLS.register("rewind", () -> Spells.Self.REWIND);
    // targeted
    public static final RegistryObject<Spell> DEVOUR = FantazicRegistry.SPELLS.register("devour", () -> Spells.Targeted.DEVOUR);
    public static final RegistryObject<Spell> SONIC_BOOM = FantazicRegistry.SPELLS.register("sonic_boom", () -> Spells.Targeted.SONIC_BOOM);
    // passive
    public static final RegistryObject<Spell> REFLECT = FantazicRegistry.SPELLS.register("reflect", () -> Spells.Passive.REFLECT);
    public static final RegistryObject<Spell> DAMNED_WRATH = FantazicRegistry.SPELLS.register("damned_wrath", () -> Spells.Passive.DAMNED_WRATH);
}
