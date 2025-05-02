package net.arkadiyhimself.fantazia.entities;

import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.registries.FTZEntityTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.UUID;

public class Shockwave extends Entity {

    private static final int INITIAL_LIFE = 15;

    public static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(Shockwave.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Vector3f> DELTA = SynchedEntityData.defineId(Shockwave.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(Shockwave.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> FURIOUS = SynchedEntityData.defineId(Shockwave.class, EntityDataSerializers.BOOLEAN);

    private UUID ownerUUID = null;
    private @Nullable Entity cachedOwner = null;
    private float cachedDamage;

    public Shockwave(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public Shockwave(Level level, Entity owner, float cachedDamage) {
        super(FTZEntityTypes.SHOCKWAVE.get(), level);
        this.ownerUUID = owner.getUUID();
        this.cachedOwner = owner;

        this.cachedDamage = cachedDamage;

        this.entityData.set(OWNER, Optional.of(owner.getUUID()));

        boolean fury = owner instanceof LivingEntity livingEntity && livingEntity.hasEffect(FTZMobEffects.FURY);
        this.entityData.set(DELTA, new Vec3(owner.getLookAngle().x, 0, owner.getLookAngle().z).normalize().scale(fury ? 1.1125f : 0.655f).toVector3f());
        this.entityData.set(FURIOUS, fury);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(OWNER, Optional.empty());
        builder.define(DELTA, new Vector3f());
        builder.define(LIFE, INITIAL_LIFE);
        builder.define(FURIOUS, false);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        if (compoundTag.contains("owner")) {
            this.ownerUUID = compoundTag.getUUID("owner");
            this.cachedOwner = null;
        }


        this.cachedDamage = compoundTag.getFloat("damage");

        double dX = compoundTag.getDouble("deltaX");
        double dY = compoundTag.getDouble("deltaY");
        double dZ = compoundTag.getDouble("deltaZ");

        if (compoundTag.contains("life")) entityData.set(LIFE, compoundTag.getInt("life"));

        this.entityData.set(DELTA, new Vec3(dX, dY, dZ).toVector3f());
        this.entityData.set(OWNER, Optional.of(ownerUUID));
        this.entityData.set(FURIOUS, compoundTag.getBoolean("furious"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        if (ownerUUID != null) compoundTag.putUUID("owner", ownerUUID);

        compoundTag.putFloat("damage", cachedDamage);

        Vec3 delta = cachedDelta();

        compoundTag.putDouble("deltaX", delta.x);
        compoundTag.putDouble("deltaY", delta.y);
        compoundTag.putDouble("deltaZ", delta.z);

        compoundTag.putInt("life", entityData.get(LIFE));

        compoundTag.putBoolean("furious", entityData.get(FURIOUS));
    }

    @Override
    public void tick() {
        super.tick();

        int life = entityData.get(LIFE);
        life--;
        if (life <= 0 && !this.level().isClientSide()) this.discard();
        entityData.set(LIFE, life);

        this.setPos(position().add(cachedDelta()));
        this.rotateToMovement();
        if (life <= 5) this.slowdown();

        DamageSourcesHolder damageSourcesHolder = LevelAttributesHelper.getDamageSources(level());
        if (damageSourcesHolder == null) return;

        for (LivingEntity livingEntity : level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (livingEntity == getOwner()) continue;
            livingEntity.hurt(damageSourcesHolder.shockWave(this), cachedDamage);
        }
    }

    public Vec3 cachedDelta() {
        return new Vec3(entityData.get(DELTA));
    }

    // rotate the entity to look at the direction of where it moves;
    private void rotateToMovement() {
        Vec3 delta = cachedDelta();

        double dx = delta.x();
        double dz = delta.z();

        this.setYRot((float)(Mth.atan2(dz, dx) * 180.0 / Math.PI) - 90f);
    }

    private void slowdown() {
        Vec3 orig = cachedDelta();
        entityData.set(DELTA, orig.scale(0.85f).toVector3f());
    }

    public float lifePercentage() {
        return (float) entityData.get(LIFE) / INITIAL_LIFE;
    }

    public boolean furious() {
        return entityData.get(FURIOUS);
    }

    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) return this.cachedOwner;
        else {
            if (this.ownerUUID != null && this.level() instanceof ServerLevel serverlevel) return (this.cachedOwner = serverlevel.getEntity(this.ownerUUID));
            return null;
        }
    }
}
