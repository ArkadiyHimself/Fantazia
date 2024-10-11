package net.arkadiyhimself.fantazia.particless;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;


public class ElectroParticle extends TextureSheetParticle {
    public ElectroParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.lifetime = 3;
        this.quadSize *= 1.5f;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public record Provider(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            ElectroParticle electroParticle = new ElectroParticle(level, x, y, z, spriteSet);
            electroParticle.pickSprite(spriteSet);
            return electroParticle;
        }
    }
}
