package net.arkadiyhimself.fantazia.particless.particles;

import net.arkadiyhimself.fantazia.particless.options.EntityChasingParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChasingEntityParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;
    private final @Nullable Entity entity;
    private Vec3 relative;

    public ChasingEntityParticle(ClientLevel level, SpriteSet spriteSet, int id, Vec3 relative) {
        super(level, 0,0,0);
        this.spriteSet = spriteSet;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.lifetime = 3;
        this.quadSize *= 1.25f;
        this.setSpriteFromAge(spriteSet);
        this.entity = level.getEntity(id);
        this.relative = relative;

        if (this.entity == null) {
            this.remove();
            return;
        }

        Vec3 finalPos = entity.position().add(relative);
        this.setPos(finalPos.x, finalPos.y, finalPos.z);
        this.xo = finalPos.x;
        this.yo = finalPos.y;
        this.zo = finalPos.z;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(spriteSet);
        if (entity == null) return;
        Vec3 pos = entity.position().add(relative);
        this.setPos(pos.x, pos.y, pos.z);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public Vec3 getRelative() {
        return relative;
    }

    public void setRelative(Vec3 relative) {
        this.relative = relative;
    }

    public record Provider<T extends ParticleType<?>, M extends EntityChasingParticleOption<T>>(SpriteSet spriteSet) implements ParticleProvider<M> {
        @Override
        public @NotNull Particle createParticle(@NotNull M option, @NotNull ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
            return new ChasingEntityParticle(clientLevel, spriteSet, option.getEntityId(), option.getRelative());
        }
    }
}
