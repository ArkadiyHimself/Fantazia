package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.particless.BarrierParticle;
import net.arkadiyhimself.fantazia.particless.BloodParticle;
import net.arkadiyhimself.fantazia.particless.SoulParticle;
import net.arkadiyhimself.fantazia.util.library.RandomList;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FTZParticleTypes {
    private FTZParticleTypes() {}
    private static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, Fantazia.MODID);
    private static <T extends ParticleType<?>> DeferredHolder<ParticleType<?>, T> registerAndList(String name, Supplier<T> particleTypeSupplier, RandomList<T> particleTypes) {
        T t = particleTypeSupplier.get();
        if (!particleTypes.contains(particleTypeSupplier.get())) particleTypes.add(t);
        return REGISTER.register(name, () -> t);
    }
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD1 = registerAndList("blood1", () -> new SimpleParticleType(true), BloodParticle.BLOOD); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD2 = registerAndList("blood2", () -> new SimpleParticleType(true), BloodParticle.BLOOD); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD3 = registerAndList("blood3", () -> new SimpleParticleType(true), BloodParticle.BLOOD); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD4 = registerAndList("blood4", () -> new SimpleParticleType(true), BloodParticle.BLOOD); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD5 = registerAndList("blood5", () -> new SimpleParticleType(true), BloodParticle.BLOOD); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FALLEN_SOUL = REGISTER.register("fallen_soul", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DOOMED_SOUL1 = registerAndList("doomed_soul1", () -> new SimpleParticleType(true), SoulParticle.DOOMED_SOULS); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DOOMED_SOUL2 = registerAndList("doomed_soul2", () -> new SimpleParticleType(true), SoulParticle.DOOMED_SOULS); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DOOMED_SOUL3 = registerAndList("doomed_soul3", () -> new SimpleParticleType(true), SoulParticle.DOOMED_SOULS); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE1 = registerAndList("barrier_piece1", () -> new SimpleParticleType(true), BarrierParticle.PIECES); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE2 = registerAndList("barrier_piece2", () -> new SimpleParticleType(true), BarrierParticle.PIECES); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE3 = registerAndList("barrier_piece3", () -> new SimpleParticleType(true), BarrierParticle.PIECES); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE4 = registerAndList("barrier_piece4", () -> new SimpleParticleType(true), BarrierParticle.PIECES); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE5 = registerAndList("barrier_piece5", () -> new SimpleParticleType(true), BarrierParticle.PIECES); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE1_FURY = registerAndList("barrier_piece1_fury", () -> new SimpleParticleType(true), BarrierParticle.PIECES_FURY); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE2_FURY = registerAndList("barrier_piece2_fury", () -> new SimpleParticleType(true), BarrierParticle.PIECES_FURY); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE3_FURY = registerAndList("barrier_piece3_fury", () -> new SimpleParticleType(true), BarrierParticle.PIECES_FURY); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE4_FURY = registerAndList("barrier_piece4_fury", () -> new SimpleParticleType(true), BarrierParticle.PIECES_FURY); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BARRIER_PIECE5_FURY = registerAndList("barrier_piece5_fury", () -> new SimpleParticleType(true), BarrierParticle.PIECES_FURY); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIFESTEAL1 = REGISTER.register("lifesteal1", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIFESTEAL2 = REGISTER.register("lifesteal2", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIFESTEAL3 = REGISTER.register("lifesteal3", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIFESTEAL4 = REGISTER.register("lifesteal4", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIFESTEAL5 = REGISTER.register("lifesteal5", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> REGEN1 = REGISTER.register("regen1", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> REGEN2 = REGISTER.register("regen2", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> REGEN3 = REGISTER.register("regen3", () -> new SimpleParticleType(true)); // finished and implemented
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TIME_TRAVEL = REGISTER.register("time_travel", () -> new SimpleParticleType(true));
    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
