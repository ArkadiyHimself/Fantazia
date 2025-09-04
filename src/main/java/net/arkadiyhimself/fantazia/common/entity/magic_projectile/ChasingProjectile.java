package net.arkadiyhimself.fantazia.common.entity.magic_projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class ChasingProjectile extends AbstractMagicProjectile {

    private @Nullable UUID targetUUID = null;
    private @Nullable Entity cachedTarget = null;
    private boolean needsTarget = false;

    public ChasingProjectile(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public ChasingProjectile(EntityType<?> entityType, Level level, @Nullable Entity cachedOwner, int lifeSpan, float velocity) {
        super(entityType, level, cachedOwner, Vec3.ZERO, lifeSpan, velocity);
    }

    public void setTarget(@Nullable Entity target) {
        this.cachedTarget = target;
        this.targetUUID = target == null ? null : target.getUUID();
    }

    public @Nullable Entity getTarget() {
        if (this.cachedTarget != null && !this.cachedTarget.isRemoved()) return this.cachedTarget;
        else {
            if (this.targetUUID != null && level() instanceof ServerLevel serverlevel) return this.cachedTarget = serverlevel.getEntity(this.targetUUID);
            return null;
        }
    }

    public void setNeedsTarget(boolean needsTarget) {
        this.needsTarget = needsTarget;
    }

    public boolean needsTarget() {
        return needsTarget;
    }

    public Vec3 calculateAngle() {
        Entity entity = getTarget();
        return entity == null ? Vec3.ZERO : entity.getEyePosition().subtract(this.position());
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.targetUUID != null) compoundTag.putUUID("Target", this.targetUUID);
        compoundTag.putBoolean("NeedsTarget", this.needsTarget);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.hasUUID("Target")) {
            this.targetUUID = compoundTag.getUUID("Target");
            this.cachedTarget = null;
        }
        this.needsTarget = compoundTag.getBoolean("NeedsTarget");
    }

    @Override
    public void tick() {
        super.tick();

        Entity target = getTarget();
        this.angle = calculateAngle();

        if (target == null) {
            if (needsTarget) discard();
            return;
        }
        if (target.getEyePosition().distanceTo(this.position()) <= 0.5) onHitEntity(target);
    }
}
