package net.arkadiyhimself.fantazia.particless;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GenericParticle extends RisingParticle {
    public static final ArrayList<SimpleParticleType> LIFESTEAL = new ArrayList<>(){{
        add(FTZParticleTypes.LIFESTEAL1);
        add(FTZParticleTypes.LIFESTEAL2);
        add(FTZParticleTypes.LIFESTEAL3);
        add(FTZParticleTypes.LIFESTEAL4);
        add(FTZParticleTypes.LIFESTEAL5);
    }};
    public static final ArrayList<SimpleParticleType> REGENERATION = new ArrayList<>(){{
        add(FTZParticleTypes.REGEN1);
        add(FTZParticleTypes.REGEN2);
        add(FTZParticleTypes.REGEN3);
    }};
    public static SimpleParticleType randomLifestealParticle() {
        int i = Fantazia.RANDOM.nextInt(0, LIFESTEAL.size());
        return LIFESTEAL.get(i);
    }
    public static SimpleParticleType randomRegenParticle() {
        int i = Fantazia.RANDOM.nextInt(0, REGENERATION.size());
        return REGENERATION.get(i);
    }
    private final SpriteSet spriteSet;
    public GenericParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet spriteSet)  {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);

        this.friction = 0.8F;
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.quadSize *= 1.1;
        this.lifetime = 15;
        this.spriteSet = spriteSet;

        this.setSpriteFromAge(spriteSet);

        this.rCol = 1F;
        this.bCol = 1F;
        this.gCol = 1F;
    }
    @Override
    public void tick() {
        this.setSpriteFromAge(this.spriteSet);
        this.fadeOut();
        super.tick();
    }

    public void fadeOut() {
        this.alpha = (-(0.5F/(float)lifetime) * age + 1);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
        public record Provider(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
                return new GenericParticle(level, x, y, z, dx, dy, dz, this.spriteSet);
            }
        }
}
