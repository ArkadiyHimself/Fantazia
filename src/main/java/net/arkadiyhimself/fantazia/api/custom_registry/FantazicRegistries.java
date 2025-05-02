package net.arkadiyhimself.fantazia.api.custom_registry;

import com.mojang.serialization.Lifecycle;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class FantazicRegistries {

    public static Spells createSpells(String modid) {
        return new Spells(modid);
    }

    public static Auras createAuras(String modid) {
        return new Auras(modid);
    }

    public static final Registry<AbstractSpell> SPELLS = new MappedRegistry<>(Keys.SPELL, Lifecycle.stable(), false);
    public static final Registry<BasicAura> AURAS = new RegistryBuilder<>(Keys.AURA).sync(true).create();

    public static final class Keys {
        private Keys() {}
        public static final ResourceKey<Registry<AbstractSpell>> SPELL = Fantazia.resKey("spell");
        public static final ResourceKey<Registry<BasicAura>> AURA = Fantazia.resKey("aura");
        public static final ResourceKey<Registry<HealingType>> HEALING_TYPE = Fantazia.resKey("healing_type");
    }

    public static class Spells extends DeferredRegister<AbstractSpell> {

        protected Spells(String namespace) {
            super(Keys.SPELL, namespace);
        }

        @SuppressWarnings("unchecked")
        public <I extends AbstractSpell> @NotNull DeferredSpell<I> register(@NotNull String name, @NotNull Function<ResourceLocation, ? extends I> func) {
            return (DeferredSpell<I>)super.register(name, func);
        }

        public <I extends AbstractSpell> @NotNull DeferredSpell<I> register(@NotNull String name, @NotNull Supplier<? extends I> sup) {
            return register(name, key -> sup.get());
        }

        protected <I extends AbstractSpell> @NotNull DeferredSpell<I> createHolder(@NotNull ResourceKey<? extends Registry<AbstractSpell>> registryKey, @NotNull ResourceLocation key) {
            return DeferredSpell.createSpell(ResourceKey.create(registryKey, key));
        }
    }

    public static class Auras extends DeferredRegister<BasicAura> {

        protected Auras(String namespace) {
            super(Keys.AURA, namespace);
        }

        @SuppressWarnings("unchecked")
        public <I extends BasicAura> @NotNull DeferredAura<I> register(@NotNull String name, @NotNull Function<ResourceLocation, ? extends I> func) {
            return (DeferredAura<I>)super.register(name, func);
        }

        public <I extends BasicAura> @NotNull DeferredAura<I> register(@NotNull String name, @NotNull Supplier<? extends I> sup) {
            return register(name, key -> sup.get());
        }

        protected <I extends BasicAura> @NotNull DeferredAura<I> createHolder(@NotNull ResourceKey<? extends Registry<BasicAura>> registryKey, @NotNull ResourceLocation key) {
            return DeferredAura.createAura(ResourceKey.create(registryKey, key));
        }
    }
}
