package net.arkadiyhimself.fantazia.api.custom_registry;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.advanced.runes.Rune;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.client.screen.TalentTab;
import net.arkadiyhimself.fantazia.data.loot.LootModifier;
import net.arkadiyhimself.fantazia.data.spawn_effect.EffectSpawnApplier;
import net.arkadiyhimself.fantazia.data.talent.Talent;
import net.arkadiyhimself.fantazia.data.talent.TalentHierarchyBuilder;
import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardsCombined;
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

    public static Runes createRunes(String modid) {
        return new Runes(modid);
    }

    public static final Registry<AbstractSpell> SPELLS = new RegistryBuilder<>(Keys.SPELL).sync(true).create();
    public static final Registry<Aura> AURAS = new RegistryBuilder<>(Keys.AURA).sync(true).create();
    public static final Registry<Rune> RUNES = new RegistryBuilder<>(Keys.RUNE).sync(true).create();

    public static final class Keys {

        public static final ResourceKey<Registry<AbstractSpell>> SPELL = Fantazia.resKey("spell");
        public static final ResourceKey<Registry<Aura>> AURA = Fantazia.resKey("aura");
        public static final ResourceKey<Registry<Rune>> RUNE = Fantazia.resKey("rune");
        public static final ResourceKey<Registry<HealingType>> HEALING_TYPE = Fantazia.resKey("healing_type");
        public static final ResourceKey<Registry<LootModifier.Builder>> LOOT_MODIFIER = Fantazia.resKey("loot_modifier");
        public static final ResourceKey<Registry<EffectSpawnApplier.Builder>> EFFECT_SPAWN_APPLIER = Fantazia.resKey("effect_spawn_applier");
        public static final ResourceKey<Registry<Talent.Builder>> TALENT = Fantazia.resKey("talent_reload/talent");
        public static final ResourceKey<Registry<TalentHierarchyBuilder>> TALENT_HIERARCHY = Fantazia.resKey("talent_reload/talent_hierarchy");
        public static final ResourceKey<Registry<TalentTab.Builder>> TALENT_TAB = Fantazia.resKey("talent_reload/talent_tab");
        public static final ResourceKey<Registry<WisdomRewardsCombined.Builder>> WISDOM_REWARD_CATEGORY = Fantazia.resKey("talent_reload/wisdom_reward");
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

    public static class Auras extends DeferredRegister<Aura> {

        protected Auras(String namespace) {
            super(Keys.AURA, namespace);
        }

        @SuppressWarnings("unchecked")
        public <I extends Aura> @NotNull DeferredAura<I> register(@NotNull String name, @NotNull Function<ResourceLocation, ? extends I> func) {
            return (DeferredAura<I>)super.register(name, func);
        }

        public <I extends Aura> @NotNull DeferredAura<I> register(@NotNull String name, @NotNull Supplier<? extends I> sup) {
            return register(name, key -> sup.get());
        }

        protected <I extends Aura> @NotNull DeferredAura<I> createHolder(@NotNull ResourceKey<? extends Registry<Aura>> registryKey, @NotNull ResourceLocation key) {
            return DeferredAura.createAura(ResourceKey.create(registryKey, key));
        }
    }

    public static class Runes extends DeferredRegister<Rune> {

        protected Runes(String namespace) {
            super(Keys.RUNE, namespace);
        }

        @SuppressWarnings("unchecked")
        public <T extends Rune> @NotNull DeferredRune<T> register(@NotNull String name, @NotNull Function<ResourceLocation, ? extends T> func) {
            return (DeferredRune<T>)super.register(name, func);
        }

        public <T extends Rune> @NotNull DeferredRune<T> register(@NotNull String name, @NotNull Supplier<? extends T> sup) {
            return register(name, key -> sup.get());
        }

        protected <T extends Rune> @NotNull DeferredRune<T> createHolder(@NotNull ResourceKey<? extends Registry<Rune>> registryKey, @NotNull ResourceLocation key) {
            return DeferredRune.createAura(ResourceKey.create(registryKey, key));
        }
    }
}
