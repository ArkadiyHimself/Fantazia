package net.arkadiyhimself.fantazia.common.entity.magic_projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class AbstractMagicProjectile extends Entity {

    public static final EntityDataAccessor<Integer> OWNER = SynchedEntityData.defineId(AbstractMagicProjectile.class, EntityDataSerializers.INT);

    private @Nullable UUID ownerUUID = null;
    private @Nullable Entity cachedOwner = null;
    protected Vec3 angle = Vec3.ZERO;
    public int lifeSpan = -1;
    public double velocity = 0;
    private boolean destroyedByCollision = false;
    private boolean canBeDeflected = false;
    private boolean isMeleeBlocked = false;

    public AbstractMagicProjectile(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public AbstractMagicProjectile(EntityType<?> entityType, Level level, @Nullable Entity cachedOwner, Vec3 angle, int lifeSpan, float velocity) {
        super(entityType, level);
        this.cachedOwner = cachedOwner;
        this.angle = angle;
        this.ownerUUID = cachedOwner == null ? null : cachedOwner.getUUID();
        this.setDeltaMovement(angle);
        this.lifeSpan = lifeSpan;
        this.velocity = velocity;

        if (cachedOwner != null) entityData.set(OWNER, cachedOwner.getId());
    }

    protected void onHitEntity(Entity entity) {
        discard();
    }

    protected void onHitBlock(BlockState blockState) {
        if (destroyedByCollision) discard();
    }

    public final void setOwner(@Nullable Entity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.cachedOwner = owner;
            entityData.set(OWNER, owner.getId());
        }
    }

    public void setDestroyedByCollision(boolean value) {
        this.destroyedByCollision = value;
    }

    public void setCanBeDeflected(boolean value) {
        this.canBeDeflected = value;
    }

    public void setMeleeBlocked(boolean value) {
        this.isMeleeBlocked = value;
    }

    public boolean isMeleeBlocked() {
        return isMeleeBlocked;
    }

    public void deflect(@Nullable Entity attacker) {
        if (!canBeDeflected) return;
        this.discard();
        this.level().gameEvent(GameEvent.ENTITY_DAMAGE, this.position(), GameEvent.Context.of(this));
    }

    @Nullable
    public final Entity getOwnerClient() {
        return level().getEntity(entityData.get(OWNER));
    }

    @Nullable
    public final Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) return this.cachedOwner;
        else {
            if (this.ownerUUID != null) {
                Level var2 = this.level();
                if (var2 instanceof ServerLevel serverlevel) {
                    this.cachedOwner = serverlevel.getEntity(this.ownerUUID);
                    if (cachedOwner != null) entityData.set(OWNER, cachedOwner.getId());
                    return this.cachedOwner;
                }
            }
            return null;
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
            this.cachedOwner = null;
        }

        lifeSpan = compoundTag.contains("Lifespan") ? compoundTag.getInt("Lifespan") : -1;
        velocity = compoundTag.getDouble("Velocity");
        destroyedByCollision = compoundTag.getBoolean("destroyedByCollision");
        canBeDeflected = compoundTag.getBoolean("canBeDeflected");
        isMeleeBlocked = compoundTag.getBoolean("isMeleeBlocked");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        if (this.ownerUUID != null) compoundTag.putUUID("Owner", this.ownerUUID);
        compoundTag.putInt("Lifespan", lifeSpan);
        compoundTag.putDouble("Velocity", velocity);
        compoundTag.putBoolean("destroyedByCollision", destroyedByCollision);
        compoundTag.putBoolean("canBeDeflected", canBeDeflected);
        compoundTag.putBoolean("isMeleeBlocked", isMeleeBlocked);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(OWNER,-1);
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(angle.normalize().scale(velocity));
        this.setPos(position().add(getDeltaMovement()));
        BlockState blockState = this.level().getBlockState(blockPosition());
        if (blockState.isSolid()) onHitBlock(blockState);

        if (lifeSpan == -1) return;

        if (lifeSpan == 0) discard();
        lifeSpan--;
    }

    @Override
    public boolean isPickable() {
        return true;
    }


}
