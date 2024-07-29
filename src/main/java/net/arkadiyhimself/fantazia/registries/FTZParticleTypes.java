package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class FTZParticleTypes extends FTZRegistry<ParticleType<?>>{
    private static final FTZParticleTypes INSTANCE = new FTZParticleTypes();
    @ObjectHolder(value = Fantazia.MODID + ":blood1", registryName = "particle_type")
    public static final SimpleParticleType BLOOD1 = null;
    @ObjectHolder(value = Fantazia.MODID + ":blood2", registryName = "particle_type")
    public static final SimpleParticleType BLOOD2 = null;
    @ObjectHolder(value = Fantazia.MODID + ":blood3", registryName = "particle_type")
    public static final SimpleParticleType BLOOD3 = null;
    @ObjectHolder(value = Fantazia.MODID + ":blood4", registryName = "particle_type")
    public static final SimpleParticleType BLOOD4 = null;
    @ObjectHolder(value = Fantazia.MODID + ":blood5", registryName = "particle_type")
    public static final SimpleParticleType BLOOD5 = null;

    @ObjectHolder(value = Fantazia.MODID + ":fallen_soul", registryName = "particle_type")
    public static final SimpleParticleType FALLEN_SOUL = null;

    @ObjectHolder(value = Fantazia.MODID + ":doomed_soul1", registryName = "particle_type")
    public static final SimpleParticleType DOOMED_SOUL1 = null;
    @ObjectHolder(value = Fantazia.MODID + ":doomed_soul2", registryName = "particle_type")
    public static final SimpleParticleType DOOMED_SOUL2 = null;
    @ObjectHolder(value = Fantazia.MODID + ":doomed_soul3", registryName = "particle_type")
    public static final SimpleParticleType DOOMED_SOUL3 = null;

    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece1", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE1 = null;
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece2", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE2 = null;
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece3", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE3 = null;
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece4", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE4 = null;
    @ObjectHolder(value = Fantazia.MODID + ":barrier_piece5", registryName = "particle_type")
    public static final SimpleParticleType BARRIER_PIECE5 = null;

    @ObjectHolder(value = Fantazia.MODID + ":lifesteal1", registryName = "particle_type")
    public static final SimpleParticleType LIFESTEAL1 = null;
    @ObjectHolder(value = Fantazia.MODID + ":lifesteal2", registryName = "particle_type")
    public static final SimpleParticleType LIFESTEAL2 = null;
    @ObjectHolder(value = Fantazia.MODID + ":lifesteal3", registryName = "particle_type")
    public static final SimpleParticleType LIFESTEAL3 = null;
    @ObjectHolder(value = Fantazia.MODID + ":lifesteal4", registryName = "particle_type")
    public static final SimpleParticleType LIFESTEAL4 = null;
    @ObjectHolder(value = Fantazia.MODID + ":lifesteal5", registryName = "particle_type")
    public static final SimpleParticleType LIFESTEAL5 = null;

    @ObjectHolder(value = Fantazia.MODID + ":regen1", registryName = "particle_type")
    public static final SimpleParticleType REGEN1 = null;
    @ObjectHolder(value = Fantazia.MODID + ":regen2", registryName = "particle_type")
    public static final SimpleParticleType REGEN2 = null;
    @ObjectHolder(value = Fantazia.MODID + ":regen3", registryName = "particle_type")
    public static final SimpleParticleType REGEN3 = null;

    public FTZParticleTypes() {
        super(ForgeRegistries.PARTICLE_TYPES);

        this.register("blood1", () -> new SimpleParticleType(true));
        this.register("blood2", () -> new SimpleParticleType(true));
        this.register("blood3", () -> new SimpleParticleType(true));
        this.register("blood4", () -> new SimpleParticleType(true));
        this.register("blood5", () -> new SimpleParticleType(true));

        this.register("fallen_soul", () -> new SimpleParticleType(true));

        this.register("doomed_soul1", () -> new SimpleParticleType(true));
        this.register("doomed_soul2", () -> new SimpleParticleType(true));
        this.register("doomed_soul3", () -> new SimpleParticleType(true));

        this.register("barrier_piece1", () -> new SimpleParticleType(true));
        this.register("barrier_piece2", () -> new SimpleParticleType(true));
        this.register("barrier_piece3", () -> new SimpleParticleType(true));
        this.register("barrier_piece4", () -> new SimpleParticleType(true));
        this.register("barrier_piece5", () -> new SimpleParticleType(true));


        this.register("lifesteal1", () -> new SimpleParticleType(true));
        this.register("lifesteal2", () -> new SimpleParticleType(true));
        this.register("lifesteal3", () -> new SimpleParticleType(true));
        this.register("lifesteal4", () -> new SimpleParticleType(true));
        this.register("lifesteal5", () -> new SimpleParticleType(true));

        this.register("regen1", () -> new SimpleParticleType(true));
        this.register("regen2", () -> new SimpleParticleType(true));
        this.register("regen3", () -> new SimpleParticleType(true));
    }
}
