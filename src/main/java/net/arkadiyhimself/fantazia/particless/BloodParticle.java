package net.arkadiyhimself.fantazia.particless;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public class BloodParticle extends TextureSheetParticle {
    private static final ArrayList<SimpleParticleType> BLOOD = new ArrayList<>(){{
        add(FTZParticleTypes.BLOOD1);
        add(FTZParticleTypes.BLOOD2);
        add(FTZParticleTypes.BLOOD3);
        add(FTZParticleTypes.BLOOD4);
        add(FTZParticleTypes.BLOOD5);
    }};
    public static SimpleParticleType randomBloodParticle() {
        int i = Fantazia.RANDOM.nextInt(0, BLOOD.size());
        return BLOOD.get(i);
    }
    protected BloodParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet) {
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
                BloodParticle bloodParticle = new BloodParticle(level, x, y, z, spriteSet);
                bloodParticle.pickSprite(spriteSet);
                return bloodParticle;
            }
        }
}
