package net.arkadiyhimself.fantazia.client.particless.particles;

import net.arkadiyhimself.fantazia.client.particless.options.EntityChasingParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ElectroParticle extends EntityChasingParticle {

    private final SpriteSet spriteSet;

    public ElectroParticle(ClientLevel level, SpriteSet spriteSet, int id, Vec3 relative) {
        super(level, spriteSet, id, relative);
        this.spriteSet = spriteSet;
        this.lifetime = 5;
    }

    @Override
    public void tick() {
        super.tick();
        this.pickSprite(spriteSet);
    }

    public record Provider(SpriteSet spriteSet) implements ParticleProvider<EntityChasingParticleOption<?>> {

        @Override
        public @NotNull Particle createParticle(@NotNull EntityChasingParticleOption<?> option, @NotNull ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
            return new ElectroParticle(clientLevel, spriteSet, option.getEntityId(), option.getRelative());
        }
    }
}
