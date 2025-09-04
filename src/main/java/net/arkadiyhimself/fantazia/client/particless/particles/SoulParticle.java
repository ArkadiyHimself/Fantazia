package net.arkadiyhimself.fantazia.client.particless.particles;

import net.arkadiyhimself.fantazia.client.particless.options.EntityChasingParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SoulParticle extends MovingEntityChasingParticle {

    public SoulParticle(ClientLevel pLevel, SpriteSet spriteSet, int id, Vec3 relative)  {
        super(pLevel, spriteSet, id, relative);

        this.friction = 0.8F;
        this.quadSize *= 1.1f;
        this.lifetime = 15;

        this.setSpriteFromAge(spriteSet);

        this.rCol = 1F;
        this.bCol = 1F;
        this.gCol = 1F;
    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
        rise();
    }

    private void fadeOut() {
        this.alpha = (-(0.5F/(float)lifetime) * age + 1);
    }

    private void rise() {
        double DY = 0.0175;
        Vec3 newRel = getRelative().add(0, DY, 0);
        setRelative(newRel);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public record Provider(SpriteSet spriteSet) implements ParticleProvider<EntityChasingParticleOption<?>> {
        public Particle createParticle(@NotNull EntityChasingParticleOption<?> particleOption, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new SoulParticle(level, spriteSet, particleOption.getEntityId(), particleOption.getRelative());
        }
    }
}
