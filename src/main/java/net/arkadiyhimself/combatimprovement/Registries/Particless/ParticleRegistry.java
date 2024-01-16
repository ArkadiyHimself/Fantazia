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
    public static RegistryObject<SimpleParticleType> registerBlood(final String name, final Supplier<? extends SimpleParticleType> sup) {
        RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, sup);
        bloodParticles.add(particle);
        return particle;
    }
    public static RegistryObject<SimpleParticleType> registerDoomedSoul(final String name, final Supplier<? extends SimpleParticleType> sup) {
        RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, sup);
        doomedSoulParticles.add(particle);
        return particle;
    }
    public static RegistryObject<SimpleParticleType> registerBarrierPiece(final String name, final Supplier<? extends SimpleParticleType> sup) {
        RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, sup);
        barrierPirecesParticles.add(particle);
        return particle;
    }
    public static void register(IEventBus eventBus) { PARTICLES.register(eventBus); }
    public static ArrayList<RegistryObject<SimpleParticleType>> bloodParticles = new ArrayList<>();
    public static ArrayList<RegistryObject<SimpleParticleType>> doomedSoulParticles = new ArrayList<>();
    public static ArrayList<RegistryObject<SimpleParticleType>> barrierPirecesParticles = new ArrayList<>();
    public static final RegistryObject<SimpleParticleType> BLOOD1;
    public static final RegistryObject<SimpleParticleType> BLOOD2;
    public static final RegistryObject<SimpleParticleType> BLOOD3;
    public static final RegistryObject<SimpleParticleType> BLOOD4;
    public static final RegistryObject<SimpleParticleType> BLOOD5;
    public static final RegistryObject<SimpleParticleType> FALLEN_SOUL;
    public static final RegistryObject<SimpleParticleType> DOOMED_SOUL1;
    public static final RegistryObject<SimpleParticleType> DOOMED_SOUL2;
    public static final RegistryObject<SimpleParticleType> DOOMED_SOUL3;
    public static final RegistryObject<SimpleParticleType> BARRIER_PIECE1;
    public static final RegistryObject<SimpleParticleType> BARRIER_PIECE2;
    public static final RegistryObject<SimpleParticleType> BARRIER_PIECE3;
    public static final RegistryObject<SimpleParticleType> BARRIER_PIECE4;
    public static final RegistryObject<SimpleParticleType> BARRIER_PIECE5;
    static {
        BLOOD1 = registerBlood("blood1", () -> new SimpleParticleType(true));
        BLOOD2 = registerBlood("blood2", () -> new SimpleParticleType(true));
        BLOOD3 = registerBlood("blood3", () -> new SimpleParticleType(true));
        BLOOD4 = registerBlood("blood4", () -> new SimpleParticleType(true));
        BLOOD5 = registerBlood("blood5", () -> new SimpleParticleType(true));

        FALLEN_SOUL = PARTICLES.register("fallen_soul", () -> new SimpleParticleType(true));

        DOOMED_SOUL1 = registerDoomedSoul("doomed_soul1", () -> new SimpleParticleType(true));
        DOOMED_SOUL2 = registerDoomedSoul("doomed_soul2", () -> new SimpleParticleType(true));
        DOOMED_SOUL3 = registerDoomedSoul("doomed_soul3", () -> new SimpleParticleType(true));

        BARRIER_PIECE1 = registerBarrierPiece("barrier_piece1", () -> new SimpleParticleType(true));
        BARRIER_PIECE2 = registerBarrierPiece("barrier_piece2", () -> new SimpleParticleType(true));
        BARRIER_PIECE3 = registerBarrierPiece("barrier_piece3", () -> new SimpleParticleType(true));
        BARRIER_PIECE4 = registerBarrierPiece("barrier_piece4", () -> new SimpleParticleType(true));
        BARRIER_PIECE5 = registerBarrierPiece("barrier_piece5", () -> new SimpleParticleType(true));
    }
}
