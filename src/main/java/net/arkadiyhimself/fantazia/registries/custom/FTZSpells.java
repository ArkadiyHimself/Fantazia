package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.spell.Spells;
import net.arkadiyhimself.fantazia.advanced.spell.types.PassiveSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.SelfSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.TargetedSpell;
import net.arkadiyhimself.fantazia.api.custom_registry.DeferredSpell;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.bus.api.IEventBus;

public class FTZSpells {
    private FTZSpells() {}

    public static final FantazicRegistries.Spells REGISTER = FantazicRegistries.createSpells(Fantazia.MODID);

    // self
    public static final DeferredSpell<SelfSpell> ENTANGLE = REGISTER.register("entangle", () -> Spells.Self.ENTANGLE);
    public static final DeferredSpell<SelfSpell> REWIND = REGISTER.register("rewind", () -> Spells.Self.REWIND);
    public static final DeferredSpell<SelfSpell> TRANSFER = REGISTER.register("transfer", () -> Spells.Self.TRANSFER);
    public static final DeferredSpell<SelfSpell> VANISH = REGISTER.register("vanish", () -> Spells.Self.VANISH);
    public static final DeferredSpell<SelfSpell> ALL_IN = REGISTER.register("all_in", () -> Spells.Self.ALL_IN);
    public static final DeferredSpell<SelfSpell> WANDERERS_SPIRIT = REGISTER.register("wanderers_spirit", () -> Spells.Self.WANDERERS_SPIRIT);
    // targeted
    public static final DeferredSpell<TargetedSpell<?>> DEVOUR = REGISTER.register("devour", () -> Spells.Targeted.DEVOUR);
    public static final DeferredSpell<TargetedSpell<?>> SONIC_BOOM = REGISTER.register("sonic_boom", () -> Spells.Targeted.SONIC_BOOM);
    public static final DeferredSpell<TargetedSpell<?>> BOUNCE = REGISTER.register("bounce", () -> Spells.Targeted.BOUNCE);
    public static final DeferredSpell<TargetedSpell<?>> LIGHTNING_STRIKE = REGISTER.register("lightning_strike", () -> Spells.Targeted.LIGHTNING_STRIKE);
    public static final DeferredSpell<TargetedSpell<Monster>> PUPPETEER = REGISTER.register("puppeteer", () -> Spells.Targeted.PUPPETEER);
    public static final DeferredSpell<TargetedSpell<LivingEntity>> KNOCK_OUT = REGISTER.register("knock_out", () -> Spells.Targeted.KNOCK_OUT);
    // passive
    public static final DeferredSpell<PassiveSpell> REFLECT = REGISTER.register("reflect", () -> Spells.Passive.REFLECT);
    public static final DeferredSpell<PassiveSpell> DAMNED_WRATH = REGISTER.register("damned_wrath", () -> Spells.Passive.DAMNED_WRATH);
    public static final DeferredSpell<PassiveSpell> SHOCKWAVE = REGISTER.register("shockwave", () -> Spells.Passive.SHOCKWAVE);
    public static final DeferredSpell<PassiveSpell> SUSTAIN = REGISTER.register("sustain", () -> Spells.Passive.SUSTAIN);
    public static final DeferredSpell<PassiveSpell> REINFORCE = REGISTER.register("reinforce", () -> Spells.Passive.REINFORCE);

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
