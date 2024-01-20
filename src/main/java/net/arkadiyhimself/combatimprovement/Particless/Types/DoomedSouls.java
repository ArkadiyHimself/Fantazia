package net.arkadiyhimself.combatimprovement.Particless.Types;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DoomedSouls extends RisingParticle {
    private final SpriteSet spriteSet;
    public DoomedSouls(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet spriteSet)  {
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
        super.tick();
        fadeOut();
    }

    public void fadeOut() {
        this.alpha = (-(0.5F/(float)lifetime) * age + 1);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet) { this.spriteSet = spriteSet; }
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new DoomedSouls(level, x, y, z, dx, dy, dz, this.spriteSet);
        }
    }
}
