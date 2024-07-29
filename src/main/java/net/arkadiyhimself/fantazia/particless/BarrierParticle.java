package net.arkadiyhimself.fantazia.particless;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public class BarrierParticle extends TextureSheetParticle {
    private static final ArrayList<SimpleParticleType> PIECES = new ArrayList<>(){{
        add(FTZParticleTypes.BARRIER_PIECE1);
        add(FTZParticleTypes.BARRIER_PIECE2);
        add(FTZParticleTypes.BARRIER_PIECE3);
        add(FTZParticleTypes.BARRIER_PIECE4);
        add(FTZParticleTypes.BARRIER_PIECE5);
    }};
    public static SimpleParticleType randomBarrierParticle() {
        int i = Fantazia.RANDOM.nextInt(0, PIECES.size());
        return PIECES.get(i);
    }
    public BarrierParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet spriteSet)  {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.friction = 0.8F;
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.quadSize *= 1;
        this.lifetime = 12;
        this.setSpriteFromAge(spriteSet);
        this.hasPhysics = true;
        this.rCol = 1F;
        this.bCol = 1F;
        this.gCol = 1F;

    }
    @Override
    public void tick() {
        super.tick();
        fadeOut();
        gravity();
    }
    public void gravity() {
        if (this.onGround) { this.yd = 0D;
        } else { this.yd -= 0.025D; }
    }
    public void fadeOut() { this.alpha = (-(1F/(float)lifetime) * age + 1); }
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
        public record Provider(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
                BarrierParticle barrierParticle = new BarrierParticle(level, x, y, z, dx, dy, dz, spriteSet);
                barrierParticle.pickSprite(spriteSet);
                return barrierParticle;
            }
        }
}
