package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.particless.BarrierParticle;
import net.arkadiyhimself.fantazia.particless.BloodParticle;
import net.arkadiyhimself.fantazia.particless.SoulParticle;
import net.arkadiyhimself.fantazia.util.library.RandomList;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

public class FTZParticleTypes extends FTZRegistry<ParticleType<?>>{
    @SuppressWarnings("unused")
    private static final FTZParticleTypes INSTANCE = new FTZParticleTypes();
    @ObjectHolder(value = Fantazia.MODID + ":blood1", registryName = "particle_type")
    public static final SimpleParticleType BLOOD1 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":blood2", registryName = "particle_type")
    public static final SimpleParticleType BLOOD2 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":blood3", registryName = "particle_type")
    public static final SimpleParticleType BLOOD3 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":blood4", registryName = "particle_type")
    public static final SimpleParticleType BLOOD4 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":blood5", registryName = "particle_type")
    public static final SimpleParticleType BLOOD5 = null; // finished and implemented

    @ObjectHolder(value = Fantazia.MODID + ":fallen_soul", registryName = "particle_type")
    public static final SimpleParticleType FALLEN_SOUL = null; // finished and implemented

    @ObjectHolder(value = Fantazia.MODID + ":doomed_soul1", registryName = "particle_type")
    public static final SimpleParticleType DOOMED_SOUL1 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":doomed_soul2", registryName = "particle_type")
    public static final SimpleParticleType DOOMED_SOUL2 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":doomed_soul3", registryName = "particle_type")
    public static final SimpleParticleType DOOMED_SOUL3 = null; // finished and implemented

    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece1", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE1 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece2", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE2 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece3", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE3 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece4", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE4 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece5", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE5 = null; // finished and implemented

    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece1_fury", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE1_FURY = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece2_fury", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE2_FURY = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece3_fury", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE3_FURY = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece4_fury", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE4_FURY = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece5_fury", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE5_FURY = null; // finished and implemented

    @ObjectHolder(value = Fantazia.MODID + ":lifesteal1", registryName = "particle_type")
    public static final SimpleParticleType LIFESTEAL1 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":lifesteal2", registryName = "particle_type")
    public static final SimpleParticleType LIFESTEAL2 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":lifesteal3", registryName = "particle_type")
    public static final SimpleParticleType LIFESTEAL3 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":lifesteal4", registryName = "particle_type")
    public static final SimpleParticleType LIFESTEAL4 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":lifesteal5", registryName = "particle_type")
    public static final SimpleParticleType LIFESTEAL5 = null; // finished and implemented

    @ObjectHolder(value = Fantazia.MODID + ":regen1", registryName = "particle_type")
    public static final SimpleParticleType REGEN1 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":regen2", registryName = "particle_type")
    public static final SimpleParticleType REGEN2 = null; // finished and implemented
    @ObjectHolder(value = Fantazia.MODID + ":regen3", registryName = "particle_type")
    public static final SimpleParticleType REGEN3 = null; // finished and implemented

    public FTZParticleTypes() {
        super(ForgeRegistries.PARTICLE_TYPES);

        this.registerAndList("blood1", () -> new SimpleParticleType(true), BloodParticle.BLOOD);
        this.registerAndList("blood2", () -> new SimpleParticleType(true), BloodParticle.BLOOD);
        this.registerAndList("blood3", () -> new SimpleParticleType(true), BloodParticle.BLOOD);
        this.registerAndList("blood4", () -> new SimpleParticleType(true), BloodParticle.BLOOD);
        this.registerAndList("blood5", () -> new SimpleParticleType(true), BloodParticle.BLOOD);

        this.register("fallen_soul", () -> new SimpleParticleType(true));

        this.registerAndList("doomed_soul1", () -> new SimpleParticleType(true), SoulParticle.DOOMED_SOULS);
        this.registerAndList("doomed_soul2", () -> new SimpleParticleType(true), SoulParticle.DOOMED_SOULS);
        this.registerAndList("doomed_soul3", () -> new SimpleParticleType(true), SoulParticle.DOOMED_SOULS);

        this.registerAndList("barrier_piece1", () -> new SimpleParticleType(true), BarrierParticle.PIECES);
        this.registerAndList("barrier_piece2", () -> new SimpleParticleType(true), BarrierParticle.PIECES);
        this.registerAndList("barrier_piece3", () -> new SimpleParticleType(true), BarrierParticle.PIECES);
        this.registerAndList("barrier_piece4", () -> new SimpleParticleType(true), BarrierParticle.PIECES);
        this.registerAndList("barrier_piece5", () -> new SimpleParticleType(true), BarrierParticle.PIECES);

        this.registerAndList("barrier_piece1_fury", () -> new SimpleParticleType(true), BarrierParticle.PIECES_FURY);
        this.registerAndList("barrier_piece2_fury", () -> new SimpleParticleType(true), BarrierParticle.PIECES_FURY);
        this.registerAndList("barrier_piece3_fury", () -> new SimpleParticleType(true), BarrierParticle.PIECES_FURY);
        this.registerAndList("barrier_piece4_fury", () -> new SimpleParticleType(true), BarrierParticle.PIECES_FURY);
        this.registerAndList("barrier_piece5_fury", () -> new SimpleParticleType(true), BarrierParticle.PIECES_FURY);

        this.register("lifesteal1", () -> new SimpleParticleType(true));
        this.register("lifesteal2", () -> new SimpleParticleType(true));
        this.register("lifesteal3", () -> new SimpleParticleType(true));
        this.register("lifesteal4", () -> new SimpleParticleType(true));
        this.register("lifesteal5", () -> new SimpleParticleType(true));

        this.register("regen1", () -> new SimpleParticleType(true));
        this.register("regen2", () -> new SimpleParticleType(true));
        this.register("regen3", () -> new SimpleParticleType(true));
    }
    private <T extends ParticleType<?>> void registerAndList(String name, Supplier<T> particleTypeSupplier, RandomList<T> particleTypes) {
        T t = particleTypeSupplier.get();
        register(name, () -> t);
        if (!particleTypes.contains(particleTypeSupplier.get())) particleTypes.add(t);
    }
}
