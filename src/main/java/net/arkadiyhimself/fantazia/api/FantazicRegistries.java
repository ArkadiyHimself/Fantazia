package net.arkadiyhimself.fantazia.api;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class FantazicRegistries {

    public static final Registry<AbstractSpell> SPELLS = new RegistryBuilder<>(Keys.SPELL).sync(true).create();
    public static final Registry<BasicAura<? extends Entity>> AURAS = new RegistryBuilder<>(Keys.AURA).sync(true).create();

    public static final class Keys {
        private Keys() {}
        public static final ResourceKey<Registry<AbstractSpell>> SPELL = Fantazia.resKey("spell");
        public static final ResourceKey<Registry<BasicAura<? extends Entity>>> AURA = Fantazia.resKey("aura");
        public static final ResourceKey<Registry<HealingType>> HEALING_TYPE = Fantazia.resKey("healing_type");
    }
}
