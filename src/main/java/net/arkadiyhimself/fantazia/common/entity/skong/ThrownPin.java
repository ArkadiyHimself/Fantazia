package net.arkadiyhimself.fantazia.common.entity.skong;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZEntityTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ThrownPin extends Projectile {

    private float cachedDamage;
    private UUID ownerUUID = null;
    private @Nullable Entity cachedOwner = null;
    private double gravity;

    public ThrownPin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.cachedDamage = 4f;
        rotate();
        this.gravity = 0.05;
    }

    public ThrownPin(Level level, Entity owner, float cachedDamage) {
        super(FTZEntityTypes.THROWN_PIN.value(), level);
        this.cachedDamage = cachedDamage;
        this.ownerUUID = owner.getUUID();
        this.cachedOwner = owner;
        this.gravity = 0.0025;
    }

    public void rotate() {
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = vec3.horizontalDistance();
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0 / 3.1415927410125732));
        this.setXRot((float)(Mth.atan2(vec3.y, d0) * 180.0 / 3.1415927410125732));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void shootFrom(Entity shooter, float power) {
        Vec3 lookAngle = shooter.getLookAngle().normalize();
        this.setDeltaMovement(lookAngle.scale(power));
    }

    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) return this.cachedOwner;
        else {
            if (this.ownerUUID != null && this.level() instanceof ServerLevel serverlevel) return (this.cachedOwner = serverlevel.getEntity(this.ownerUUID));
            return null;
        }
    }

    public void collide(LivingEntity entity) {
        if (entity.invulnerableTime > 10) return;
        boolean hurt = LevelAttributesHelper.hurtEntity(entity, this, cachedDamage, DamageSourcesHolder::thrownPin);
        Vec3 delta = getDeltaMovement().reverse();
        if (hurt) entity.knockback(0.4, delta.x, delta.z);
        Fantazia.LOGGER.info("Thrown pin damage before: {}", cachedDamage);
        this.cachedDamage *= 0.8f;
        Fantazia.LOGGER.info("Thrown pin damage after: {}", cachedDamage);
    }

    public void destroy() {
        discard();
        level().playSound(null, blockPosition(), FTZSoundEvents.PIN_BREAKING.value(), SoundSource.NEUTRAL);

        if (level() instanceof ServerLevel serverLevel)
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,
                    Blocks.IRON_BLOCK.defaultBlockState()), getX(), getY(), getZ(),
                    15,0.125,0.125,0.125,0);
    }

    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, startVec, endVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity);
    }

    @Override
    protected double getDefaultGravity() {
        return gravity;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoGravity()) this.applyGravity();
        rotate();

        Vec3 pos1 = position();
        Vec3 delta = getDeltaMovement();
        Vec3 pos2 = pos1.add(delta);
        EntityHitResult entityHitResult = findHitEntity(pos1, pos2);
        if (entityHitResult != null) {
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof LivingEntity livingEntity) collide(livingEntity);
        }

        this.move(MoverType.SELF, getDeltaMovement());
        if (this.horizontalCollision || this.verticalCollision) destroy();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        this.cachedDamage = compoundTag.getFloat("damage");
        this.gravity = compoundTag.getDouble("gravity");
        if (compoundTag.contains("owner")) {
            this.ownerUUID = compoundTag.getUUID("owner");
            this.cachedOwner = null;
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        compoundTag.putFloat("damage", cachedDamage);
        if (ownerUUID != null) compoundTag.putUUID("owner", ownerUUID);
        compoundTag.putDouble("gravity", gravity);
    }
}
