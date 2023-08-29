package net.arkadiyhimself.combatimprovement.Registries.Particless;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CombatImprovement.MODID);
    public static final RegistryObject<SimpleParticleType> registerBlood(final String name, final Supplier<? extends SimpleParticleType> sup) {
        RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, sup);
        bloodParticles.add(particle);
        return particle;
    }
    public static void register(IEventBus eventBus) { PARTICLES.register(eventBus); }
    public static ArrayList<RegistryObject<SimpleParticleType>> bloodParticles = new ArrayList<>();
    public static final RegistryObject<SimpleParticleType> BLOOD1;
    public static final RegistryObject<SimpleParticleType> BLOOD2;
    public static final RegistryObject<SimpleParticleType> BLOOD3;
    public static final RegistryObject<SimpleParticleType> BLOOD4;
    public static final RegistryObject<SimpleParticleType> BLOOD5;
    static {
        BLOOD1 = registerBlood("blood1", () -> new SimpleParticleType(true));
        BLOOD2 = registerBlood("blood2", () -> new SimpleParticleType(true));
        BLOOD3 = registerBlood("blood3", () -> new SimpleParticleType(true));
        BLOOD4 = registerBlood("blood4", () -> new SimpleParticleType(true));
        BLOOD5 = registerBlood("blood5", () -> new SimpleParticleType(true));
    }
}
