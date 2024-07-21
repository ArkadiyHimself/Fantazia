package net.arkadiyhimself.fantazia.registry;

import com.google.common.collect.ImmutableList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Fantazia.MODID);
    public static RegistryObject<SimpleParticleType> registerBlood(final String name, final Supplier<? extends SimpleParticleType> sup) {
        RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, sup);
        BLOOD.add(particle);
        return particle;
    }
    public static RegistryObject<SimpleParticleType> registerDoomedSoul(final String name, final Supplier<? extends SimpleParticleType> sup) {
        RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, sup);
        DOOMED_SOULS.add(particle);
        return particle;
    }
    public static RegistryObject<SimpleParticleType> registerBarrierPiece(final String name, final Supplier<? extends SimpleParticleType> sup) {
        RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, sup);
        BARRIER_PIECES.add(particle);
        return particle;
    }
    public static RegistryObject<SimpleParticleType> registerLifesteal(final String name, final Supplier<? extends SimpleParticleType> sup) {
        RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, sup);
        LIFESTEAL.add(particle);
        return particle;
    }
    public static RegistryObject<SimpleParticleType> registerRegen(final String name, final Supplier<? extends SimpleParticleType> sup) {
        RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, sup);
        REGENERATION.add(particle);
        return particle;
    }
    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
    public static ArrayList<RegistryObject<SimpleParticleType>> BLOOD = new ArrayList<>();
    public static ArrayList<RegistryObject<SimpleParticleType>> DOOMED_SOULS = new ArrayList<>();
    public static ArrayList<RegistryObject<SimpleParticleType>> BARRIER_PIECES = new ArrayList<>();
    public static ArrayList<RegistryObject<SimpleParticleType>> LIFESTEAL = new ArrayList<>();
    public static ArrayList<RegistryObject<SimpleParticleType>> REGENERATION = new ArrayList<>();
    public static final RegistryObject<SimpleParticleType> BLOOD1; // implemented
    public static final RegistryObject<SimpleParticleType> BLOOD2; // implemented
    public static final RegistryObject<SimpleParticleType> BLOOD3; // implemented
    public static final RegistryObject<SimpleParticleType> BLOOD4; // implemented
    public static final RegistryObject<SimpleParticleType> BLOOD5; // implemented
    public static final RegistryObject<SimpleParticleType> FALLEN_SOUL; // implemented
    public static final RegistryObject<SimpleParticleType> DOOMED_SOUL1; // implemented
    public static final RegistryObject<SimpleParticleType> DOOMED_SOUL2; // implemented
    public static final RegistryObject<SimpleParticleType> DOOMED_SOUL3; // implemented
    public static final RegistryObject<SimpleParticleType> BARRIER_PIECE1; // implemented
    public static final RegistryObject<SimpleParticleType> BARRIER_PIECE2; // implemented
    public static final RegistryObject<SimpleParticleType> BARRIER_PIECE3; // implemented
    public static final RegistryObject<SimpleParticleType> BARRIER_PIECE4; // implemented
    public static final RegistryObject<SimpleParticleType> BARRIER_PIECE5; // implemented
    public static final RegistryObject<SimpleParticleType> LIFESTEAL1;
    public static final RegistryObject<SimpleParticleType> LIFESTEAL2;
    public static final RegistryObject<SimpleParticleType> LIFESTEAL3;
    public static final RegistryObject<SimpleParticleType> LIFESTEAL4;
    public static final RegistryObject<SimpleParticleType> LIFESTEAL5;
    public static final RegistryObject<SimpleParticleType> REGEN1;
    public static final RegistryObject<SimpleParticleType> REGEN2;
    public static final RegistryObject<SimpleParticleType> REGEN3;

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

        LIFESTEAL1 = registerLifesteal("lifesteal1", () -> new SimpleParticleType(true));
        LIFESTEAL2 = registerLifesteal("lifesteal2", () -> new SimpleParticleType(true));
        LIFESTEAL3 = registerLifesteal("lifesteal3", () -> new SimpleParticleType(true));
        LIFESTEAL4 = registerLifesteal("lifesteal4", () -> new SimpleParticleType(true));
        LIFESTEAL5 = registerLifesteal("lifesteal5", () -> new SimpleParticleType(true));

        REGEN1 = registerRegen("regen1", () -> new SimpleParticleType(true));
        REGEN2 = registerRegen("regen2", () -> new SimpleParticleType(true));
        REGEN3 = registerRegen("regen3", () -> new SimpleParticleType(true));
    }
    public static class Sprite implements SpriteSet {

        private List<TextureAtlasSprite> sprites;

        @Override
        public @NotNull TextureAtlasSprite get(int pParticleAge, int pParticleMaxAge) {
            return this.sprites.get(pParticleAge * (this.sprites.size() - 1) / pParticleMaxAge);
        }

        @Override
        public @NotNull TextureAtlasSprite get(RandomSource pRandom) {
            return this.sprites.get(pRandom.nextInt(this.sprites.size()));
        }

        public void rebind(List<TextureAtlasSprite> pSprites) {
            this.sprites = ImmutableList.copyOf(pSprites);
        }
    }
}
