package net.arkadiyhimself.fantazia.client.particless.particles;

import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class TimeTravelParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    public TimeTravelParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet spriteSet)  {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);

        this.friction = 0.8F;
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.quadSize *= 1.1f;
        this.lifetime = 15;
        this.spriteSet = spriteSet;

        this.setSpriteFromAge(spriteSet);

        float color = RandomUtil.nextFloat() * 0.6f + 0.4f;
        this.rCol = color * 0.8f;
        this.bCol = color * 0.2f;
        this.gCol = color * 0.5f;
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
            return new TimeTravelParticle(level, x, y, z, dx, dy, dz, this.spriteSet);
        }
    }
}
