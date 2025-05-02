package net.arkadiyhimself.fantazia.particless.particles;

import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.particless.options.EntityChasingParticleOption;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EntityChasingParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;
    private final @Nullable Entity entity;
    private final Vec3 relative;

    public EntityChasingParticle(ClientLevel level, SpriteSet spriteSet, int id, Vec3 relative) {
        super(level,0,-128,0);
        this.spriteSet = spriteSet;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.lifetime = 3;
        this.quadSize *= 1.25f;
        this.setSpriteFromAge(spriteSet);
        this.entity = level.getEntity(id);
        this.relative = relative;

        if (this.entity == null || this.entity == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            this.remove();
            return;
        }

        Vec3 finalPos = entity.position().add(relative);
        this.setPos(finalPos.x, finalPos.y, finalPos.z);
        this.xo = finalPos.x;
        this.yo = finalPos.y;
        this.zo = finalPos.z;

        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (LivingEffectHelper.hasEffect(livingEntity, FTZMobEffects.FURY.value())) {
            this.rCol = 1f;
            this.gCol = 0.15f;
            this.bCol = 0.15f;
            this.quadSize += 1.5f;
        }
    }

    @Override
    public void tick() {
        this.setSpriteFromAge(spriteSet);
        if (entity == null) return;
        Vec3 pos = entity.position().add(relative);
        this.setPos(pos.x, pos.y, pos.z);
        super.tick();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public record Provider(SpriteSet spriteSet) implements ParticleProvider<EntityChasingParticleOption<?>> {

        @Override
        public @NotNull Particle createParticle(@NotNull EntityChasingParticleOption<?> option, @NotNull ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
            return new EntityChasingParticle(clientLevel, spriteSet, option.getEntityId(), option.getRelative());
        }
    }
}
