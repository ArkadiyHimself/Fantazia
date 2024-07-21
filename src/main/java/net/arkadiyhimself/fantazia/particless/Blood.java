package net.arkadiyhimself.fantazia.particless;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Blood extends TextureSheetParticle {

    protected Blood(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet) {
        super(pLevel, pX, pY, pZ);
        this.friction = 0.8F;
        this.quadSize *= 1;
        this.lifetime = 100;
        this.setSpriteFromAge(spriteSet);
        this.hasPhysics = true;
        this.rCol = 1F;
        this.bCol = 1F;
        this.gCol = 1F;
    }

    @Override
    public void tick() {
        super.tick();
        gravity();
    }
    public void gravity() {
        if (this.onGround) { this.yd = 0D;
        } else { this.yd -= 0.025D; }
    }
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
        public record Provider(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
                Blood blood = new Blood(level, x, y, z, spriteSet);
                blood.pickSprite(spriteSet);
                return blood;
            }
        }
}
