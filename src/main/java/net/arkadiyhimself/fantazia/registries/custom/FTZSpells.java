package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.spell.*;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZSpells {
    private FTZSpells() {}
    public static final DeferredRegister<AbstractSpell> REGISTER = DeferredRegister.create(FantazicRegistry.Keys.SPELL, Fantazia.MODID);
    // self
    public static final DeferredHolder<AbstractSpell, SelfSpell> ENTANGLE = REGISTER.register("entangle", () -> Spells.Self.ENTANGLE);
    public static final DeferredHolder<AbstractSpell, SelfSpell> REWIND = REGISTER.register("rewind", () -> Spells.Self.REWIND);
    // targeted
    public static final DeferredHolder<AbstractSpell, TargetedSpell<?>> DEVOUR = REGISTER.register("devour", () -> Spells.Targeted.DEVOUR);
    public static final DeferredHolder<AbstractSpell, TargetedSpell<?>> SONIC_BOOM = REGISTER.register("sonic_boom", () -> Spells.Targeted.SONIC_BOOM);
    public static final DeferredHolder<AbstractSpell, TargetedSpell<?>> BOUNCE = REGISTER.register("bounce", () -> Spells.Targeted.BOUNCE);
    public static final DeferredHolder<AbstractSpell, TargetedSpell<?>> LIGHTNING_STRIKE = REGISTER.register("lightning_strike", () -> Spells.Targeted.LIGHTNING_STRIKE);
    // passive
    public static final DeferredHolder<AbstractSpell, PassiveSpell> REFLECT = REGISTER.register("reflect", () -> Spells.Passive.REFLECT);
    public static final DeferredHolder<AbstractSpell, PassiveSpell> DAMNED_WRATH = REGISTER.register("damned_wrath", () -> Spells.Passive.DAMNED_WRATH);
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
