package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.advanced.spell.AbstractSpell;
import net.arkadiyhimself.fantazia.advanced.spell.Spells;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class FTZSpells {
    private FTZSpells() {}
    // self
    public static final RegistryObject<AbstractSpell> ENTANGLE = FantazicRegistry.SPELLS.register("entangle", () -> Spells.Self.ENTANGLE);
    public static final RegistryObject<AbstractSpell> REWIND = FantazicRegistry.SPELLS.register("rewind", () -> Spells.Self.REWIND);
    // targeted
    public static final RegistryObject<AbstractSpell> DEVOUR = FantazicRegistry.SPELLS.register("devour", () -> Spells.Targeted.DEVOUR);
    public static final RegistryObject<AbstractSpell> SONIC_BOOM = FantazicRegistry.SPELLS.register("sonic_boom", () -> Spells.Targeted.SONIC_BOOM);
    public static final RegistryObject<AbstractSpell> BOUNCE = FantazicRegistry.SPELLS.register("bounce", () -> Spells.Targeted.BOUNCE);
    public static final RegistryObject<AbstractSpell> LIGHTNING_STRIKE = FantazicRegistry.SPELLS.register("lightning_strike", () -> Spells.Targeted.LIGHTNING_STRIKE);
    // passive
    public static final RegistryObject<AbstractSpell> REFLECT = FantazicRegistry.SPELLS.register("reflect", () -> Spells.Passive.REFLECT);
    public static final RegistryObject<AbstractSpell> DAMNED_WRATH = FantazicRegistry.SPELLS.register("damned_wrath", () -> Spells.Passive.DAMNED_WRATH);
}
