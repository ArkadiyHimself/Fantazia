package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.spell.Spells;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.PassiveSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.SelfSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.TargetedSpell;
import net.arkadiyhimself.fantazia.api.FantazicRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZSpells {
    private FTZSpells() {}

    public static final DeferredRegister<AbstractSpell> REGISTER = DeferredRegister.create(FantazicRegistries.Keys.SPELL, Fantazia.MODID);

    // self
    public static final DeferredHolder<AbstractSpell, SelfSpell> ENTANGLE = REGISTER.register("entangle", () -> Spells.Self.ENTANGLE);
    public static final DeferredHolder<AbstractSpell, SelfSpell> REWIND = REGISTER.register("rewind", () -> Spells.Self.REWIND);
    public static final DeferredHolder<AbstractSpell, SelfSpell> TRANSFER = REGISTER.register("transfer", () -> Spells.Self.TRANSFER);
    // targeted
    public static final DeferredHolder<AbstractSpell, TargetedSpell<?>> DEVOUR = REGISTER.register("devour", () -> Spells.Targeted.DEVOUR);
    public static final DeferredHolder<AbstractSpell, TargetedSpell<?>> SONIC_BOOM = REGISTER.register("sonic_boom", () -> Spells.Targeted.SONIC_BOOM);
    public static final DeferredHolder<AbstractSpell, TargetedSpell<?>> BOUNCE = REGISTER.register("bounce", () -> Spells.Targeted.BOUNCE);
    public static final DeferredHolder<AbstractSpell, TargetedSpell<?>> LIGHTNING_STRIKE = REGISTER.register("lightning_strike", () -> Spells.Targeted.LIGHTNING_STRIKE);
    // passive
    public static final DeferredHolder<AbstractSpell, PassiveSpell> REFLECT = REGISTER.register("reflect", () -> Spells.Passive.REFLECT);
    public static final DeferredHolder<AbstractSpell, PassiveSpell> DAMNED_WRATH = REGISTER.register("damned_wrath", () -> Spells.Passive.DAMNED_WRATH);
    public static final DeferredHolder<AbstractSpell, PassiveSpell> SHOCKWAVE = REGISTER.register("shockwave", () -> Spells.Passive.SHOCKWAVE);
    public static final DeferredHolder<AbstractSpell, PassiveSpell> SUSTAIN = REGISTER.register("sustain", () -> Spells.Passive.SUSTAIN);

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
