package net.arkadiyhimself.fantazia.common.entity.skong;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZEntityTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Pimpillo extends Entity {

    private static final int LIFETIME = 60;

    public static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(Pimpillo.class, EntityDataSerializers.INT);

    private UUID ownerUUID = null;
    private @Nullable Entity cachedOwner = null;
    private float explosionDamage;

    public Pimpillo(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.explosionDamage = 1.5f;
    }

    public Pimpillo(Level level, Entity owner, float explosionDamage) {
        super(FTZEntityTypes.PIMPILLO.get(), level);
        this.ownerUUID = owner.getUUID();
        this.cachedOwner = owner;

        this.explosionDamage = explosionDamage;
    }

    public void shootFrom(Entity shooter, float power) {
        Vec3 lookAngle = shooter.getLookAngle().normalize();
        this.setDeltaMovement(lookAngle.scale(power).add(shooter.getDeltaMovement()));
    }

    private void explode() {
        DamageSourcesHolder holder = LevelAttributesHelper.getDamageSources(level());
        if (this.level().isClientSide() || holder == null) return;
        DamageSource damageSource = holder.pimpillo(this);
        Fantazia.LOGGER.info("Pimpillo explosion power: {}", explosionDamage);
        this.level().explode(this, damageSource, null, position(), explosionDamage, false, Level.ExplosionInteraction.MOB);
        this.discard();
    }

    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) return this.cachedOwner;
        else {
            if (this.ownerUUID != null && this.level() instanceof ServerLevel serverlevel) return (this.cachedOwner = serverlevel.getEntity(this.ownerUUID));
            return null;
        }
    }

    public void setLife(int i) {
        this.entityData.set(LIFE, i);
    }

    public int getLife() {
        return this.entityData.get(LIFE);
    }

    private void applyFriction() {
        if (!onGround()) return;
        BlockPos blockPos = getBlockPosBelowThatAffectsMyMovement();
        BlockState state = level().getBlockState(blockPos);
        float friction = state.getFriction(level(), blockPos, this);
        Vec3 delta = getDeltaMovement();
        float multiplier = 0.1295f / (friction * friction * friction * friction);
        setDeltaMovement(delta.x * multiplier, delta.y, delta.z * multiplier);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    public void tick() {
        super.tick();
        if (getLife() == 60 && this.level().isClientSide())
            FantazicUtil.entityChasingSound(this, FTZSoundEvents.PIMPILLO_FUSE_BURN.value(), SoundSource.NEUTRAL);

        if (!this.isNoGravity()) this.applyGravity();
        this.applyFriction();
        int i = getLife() - 1;
        setLife(i);
        if (i <= 0) {
            explode();
            return;
        }
        this.move(MoverType.SELF, getDeltaMovement());

        AABB aabb = this.getBoundingBox().inflate(0.075);
        List<LivingEntity> livingEntities = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
        livingEntities.removeIf(entity -> entity == getOwner());
        if (!livingEntities.isEmpty()) explode();

        Vec3 pos = this.position();
        this.level().addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z,0,0,0);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        Vec3 delta = getDeltaMovement();
        setDeltaMovement(delta.x, delta.y * -0.85, delta.z);
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(LIFE, LIFETIME);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        if (compoundTag.contains("owner")) {
            this.ownerUUID = compoundTag.getUUID("owner");
            this.cachedOwner = null;
        }

        if (compoundTag.contains("life")) entityData.set(LIFE, compoundTag.getInt("life"));
        this.explosionDamage = compoundTag.getFloat("explosion");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        if (ownerUUID != null) compoundTag.putUUID("owner", ownerUUID);
        compoundTag.putInt("life", entityData.get(LIFE));
        compoundTag.putFloat("explosion", explosionDamage);
    }

}
