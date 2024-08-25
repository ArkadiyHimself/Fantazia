package net.arkadiyhimself.fantazia.api;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.advanced.spell.Spell;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.Supplier;

public class FantazicRegistry {
    static { init(); }
    private static final List<DeferredRegister<?>> REGISTRIES = Lists.newArrayList();
    private static <T> DeferredRegister<T> createRegister(ResourceKey<Registry<T>> resourceKey) {
        DeferredRegister<T> register = DeferredRegister.create(resourceKey, Fantazia.MODID);
        REGISTRIES.add(register);
        return register;
    }
    public static final DeferredRegister<Spell> SPELLS = createRegister(Keys.SPELL);
    public static final DeferredRegister<BasicAura<?>> AURAS = createRegister(Keys.AURA);
    public static void register(IEventBus bus) {
        BakedRegistries.init();
        REGISTRIES.forEach(deferredRegister -> deferredRegister.register(bus));
    }
    private static <T> RegistryBuilder<T> taggedRegistryBuilder() {
        return new RegistryBuilder<T>().hasTags();
    }
    public static final class Keys {
        public static final ResourceKey<Registry<Spell>> SPELL = Fantazia.resKey("spell");
        public static final ResourceKey<Registry<BasicAura<?>>> AURA = Fantazia.resKey("aura");
        public static final ResourceKey<Registry<HealingType>> HEALING_TYPE = Fantazia.resKey("healing_type");
        private static void init() {}
    }
    public static final class BakedRegistries {
        public static Supplier<IForgeRegistry<Spell>> SPELL = SPELLS.makeRegistry(FantazicRegistry::taggedRegistryBuilder);
        public static Supplier<IForgeRegistry<BasicAura<?>>> AURA = AURAS.makeRegistry(FantazicRegistry::taggedRegistryBuilder);
        private static void init() {}
    }
    private static void init()
    {
        Keys.init();
    }
}
