package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.spell.*;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.registries.FTZRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ObjectHolder;

public class FTZSpells extends FTZRegistry<Spell> {
    @SuppressWarnings("unused")
    private static final FTZSpells INSTANCE = new FTZSpells();
    @ObjectHolder(value = Fantazia.MODID + ":devour", registryName = Fantazia.MODID + ":spell")
    public static final TargetedSpell<Mob> DEVOUR = null;
    @ObjectHolder(value = Fantazia.MODID + ":sonic_boom", registryName = Fantazia.MODID + ":spell")
    public static final TargetedSpell<LivingEntity> SONIC_BOOM = null;
    @ObjectHolder(value = Fantazia.MODID + ":entangle", registryName = Fantazia.MODID + ":spell")
    public static final SelfSpell ENTANGLE = null;
    @ObjectHolder(value = Fantazia.MODID + ":reflect", registryName = Fantazia.MODID + ":spell")
    public static final PassiveSpell REFLECT = null;
    @ObjectHolder(value = Fantazia.MODID + ":damned_wrath", registryName = Fantazia.MODID + ":spell")
    public static final PassiveSpell DAMNED_WRATH = null;
    public FTZSpells() {
        super(FantazicRegistry.SPELLS);
        register("devour", () -> Spells.Targeted.DEVOUR);
        register("sonic_boom", () -> Spells.Targeted.SONIC_BOOM);
        register("entangle", () -> Spells.Self.ENTANGLE);
        register("reflect", () -> Spells.Passive.REFLECT);
        register("damned_wrath", () -> Spells.Passive.DAMNED_WRATH);
    }
}
