package net.arkadiyhimself.fantazia.common.entity;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZEntityTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZParticleTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class BlockFly extends FlyingMob implements FlyingAnimal {

    private static final int LIFETIME = 1200;
    private static final float DISTANCE_TO_OWNER_WANDER = 5.5f;
    private static final float DISTANCE_TO_OWNER_ATTACK = 8.5f;
    private static final ResourceLocation SLOW_DOWN_MODIFIER = Fantazia.location("slow_down");
    private static final EntityDataAccessor<Boolean> IS_ANGRY = SynchedEntityData.defineId(BlockFly.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_LEFTY = SynchedEntityData.defineId(BlockFly.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FUNNY_DEATH = SynchedEntityData.defineId(BlockFly.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState wingsFlappingFine = new AnimationState();
    public final AnimationState wingsFlappingDamaged = new AnimationState();
    public final AnimationState wingsFlappingMoribund = new AnimationState();
    public final AnimationState dying = new AnimationState();
    public final AnimationState funnyDying = new AnimationState();
    private int life = LIFETIME;
    private UUID ownerUUID = null;
    private @Nullable Entity cachedOwner = null;
    public double visualY = 0;
    private @Nullable Entity killer = null;

    public BlockFly(EntityType<? extends FlyingMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new BlockFlyControl();
        this.entityData.set(IS_LEFTY, RandomUtil.nextBoolean());
    }

    public BlockFly(Level level, Entity owner, float damage) {
        this(FTZEntityTypes.BLOCK_FLY.value(), level);
        this.ownerUUID = owner.getUUID();
        this.cachedOwner = owner;

        if (damage > 0) {
            AttributeInstance attack = getAttribute(Attributes.ATTACK_DAMAGE);
            if (attack != null) attack.setBaseValue(damage);
        }
    }

    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) return this.cachedOwner;
        else {
            if (this.ownerUUID != null && this.level() instanceof ServerLevel serverlevel) return (this.cachedOwner = serverlevel.getEntity(this.ownerUUID));
            return null;
        }
    }

    private void funnyDeath(DamageSource source) {
        Vec3 delta;
        Entity direct = source.getDirectEntity();
        if (source.is(FTZDamageTypeTags.MELEE_ATTACK) && direct != null) {
            delta = direct.getLookAngle().normalize();
        } else {
            Vec3 pos = source.sourcePositionRaw();
            if (source.getDirectEntity() != null) pos = source.getDirectEntity().getEyePosition();
            if (pos == null) return;
            delta = this.position().subtract(pos).normalize();
        }

        this.setDeltaMovement(delta.scale(2));
        this.entityData.set(FUNNY_DEATH, true);
        this.killer = source.getDirectEntity();
        AttributeInstance gravity = getAttribute(Attributes.GRAVITY);
        if (gravity != null) gravity.addOrReplacePermanentModifier(
                new AttributeModifier(
                        Fantazia.location("funny_death"),
                        -0.5,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
        );
    }

    private void setupAnim() {
        if (isDeadOrDying()) {
            if (entityData.get(FUNNY_DEATH)) {
                this.dying.stop();
                this.funnyDying.startIfStopped(this.tickCount);
            }
            else {
                this.dying.startIfStopped(this.tickCount);
                this.funnyDying.stop();
            }
            return;
        }
        switch (getState()) {
            case FINE -> {
                this.wingsFlappingFine.startIfStopped(this.tickCount);
                this.wingsFlappingDamaged.stop();
                this.wingsFlappingMoribund.stop();
            }
            case DAMAGED -> {
                this.wingsFlappingFine.stop();
                this.wingsFlappingDamaged.startIfStopped(this.tickCount);
                this.wingsFlappingMoribund.stop();
            }
            case MORIBUND -> {
                this.wingsFlappingFine.stop();
                this.wingsFlappingDamaged.stop();
                this.wingsFlappingMoribund.startIfStopped(this.tickCount);
            }
        }
    }

    public boolean isAngry() {
        return this.entityData.get(IS_ANGRY);
    }

    public boolean isLefty() {
        return this.entityData.get(IS_LEFTY);
    }

    public boolean shouldTryTeleportToOwner() {
        Entity livingentity = this.getOwner();
        return livingentity != null && this.distanceTo(this.getOwner()) >= 16;
    }

    public void tryToTeleportToOwner() {
        Entity livingentity = this.getOwner();
        if (livingentity != null) {
            this.teleportToAroundBlockPos(livingentity.blockPosition());
        }
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.moveTo(x + 0.5, y, z + 0.5, this.getYRot(), this.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        PathType pathtype = WalkNodeEvaluator.getPathTypeStatic(this, pos);
        if (pathtype != PathType.WALKABLE) {
            return false;
        } else {
            BlockPos blockpos = pos.subtract(this.blockPosition());
            return this.level().noCollision(this, this.getBoundingBox().move(blockpos));
        }
    }

    private void teleportToAroundBlockPos(BlockPos pos) {
        for(int i = 0; i < 10; ++i) {
            int j = this.random.nextIntBetweenInclusive(-3, 3);
            int k = this.random.nextIntBetweenInclusive(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = this.random.nextIntBetweenInclusive(-1, 1);
                if (this.maybeTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
                    return;
                }
            }
        }
    }

    public WellBeing getState() {
        float percent = getHealth() / getMaxHealth();
        return WellBeing.getState(percent);
    }

    private void visualTick() {
        WellBeing state = getState();
        Vec3 pos = getEyePosition().add(0, 0.3 - visualY, 0);
        if (state == WellBeing.DAMAGED) {
            Vec3 delta = RandomUtil.randomVec3().normalize().scale(0.2);
            Vec3 finalPos = pos.add(delta);
            level().addParticle(ParticleTypes.WHITE_SMOKE, finalPos.x, finalPos.y, finalPos.z, 0, 0, 0);
        } else if (state == WellBeing.MORIBUND) {
            Vec3 delta = RandomUtil.randomVec3().normalize().scale(0.15);
            Vec3 finalPos = pos.add(delta);
            level().addParticle(ParticleTypes.SMOKE, finalPos.x, finalPos.y, finalPos.z, 0, 0, 0);
        }
    }

    public boolean wantsToAttack(LivingEntity target) {
        if (target instanceof Creeper || target instanceof ArmorStand) return false;
        if (target instanceof BlockFly blockFly) return blockFly.getOwner() != this.getOwner();
        if (target instanceof Player targetPlayer && getOwner() instanceof Player ownerPlayer)
            return !ownerPlayer.canHarmPlayer(targetPlayer);
        if (target instanceof AbstractHorse horse) return !horse.isTamed();
        if (target instanceof TamableAnimal animal) return !animal.isTame();
        return true;
    }

    public void adjustAttributes() {
        AttributeInstance flyingSpeed = getAttribute(Attributes.FLYING_SPEED);
        if (flyingSpeed == null) return;

        WellBeing state = getState();
        if (state == WellBeing.FINE) {
            flyingSpeed.removeModifier(SLOW_DOWN_MODIFIER);
            return;
        }
        AttributeModifier modifier = new AttributeModifier(
                SLOW_DOWN_MODIFIER,
                state == WellBeing.MORIBUND ? -0.575 : -0.33,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        flyingSpeed.addOrReplacePermanentModifier(modifier);
    }

    public float flyingSpeed() {
        AttributeInstance flyingSpeed = getAttribute(Attributes.FLYING_SPEED);
        return flyingSpeed == null ? 0.1f : (float) flyingSpeed.getValue();
    }

    public boolean hasOwnerAttackingEntity() {
        Entity owner = this.getOwner();
        if (!(owner instanceof LivingEntity livingOwner)) return false;

        LivingEntity ownerLastHurt = livingOwner.getLastHurtByMob();
        if (ownerLastHurt == null) return false;
        return this.canAttack(ownerLastHurt, TargetingConditions.DEFAULT) && this.wantsToAttack(ownerLastHurt) && ownerLastHurt.distanceTo(owner) <= DISTANCE_TO_OWNER_ATTACK;
    }

    public boolean hasEntityAttackingOwner() {
        return false;
    }

    @Override
    protected void tickDeath() {
        if (!entityData.get(FUNNY_DEATH)) {
            super.tickDeath();
            return;
        }
        ++this.deathTime;
        boolean collision = this.horizontalCollision || this.verticalCollision;

        boolean touching = !level().getEntitiesOfClass(LivingEntity.class, getBoundingBox()).stream().filter(livingEntity -> livingEntity != this && livingEntity != killer).toList().isEmpty();
        boolean explode = this.deathTime >= 25 || collision || touching;
        if (explode && !this.isRemoved() && !level().isClientSide()) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1f, Level.ExplosionInteraction.MOB);
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 60 && this.entityData.get(FUNNY_DEATH)) {
            for (int i = 0; i < 45; i++) {
                ParticleOptions options = FTZParticleTypes.METAL_SCRAP.random();
                if (options == null) continue;
                Vec3 delta = RandomUtil.randomVec3().normalize().scale(0.3);
                Vec3 pos = position().add(delta);
                delta = delta.normalize().scale(0.7);
                level().addParticle(options, pos.x, pos.y, pos.z, delta.x, delta.y, delta.z);
            }
        } else super.handleEntityEvent(id);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_ANGRY,false);
        builder.define(IS_LEFTY,false);
        builder.define(FUNNY_DEATH,false);
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return FTZSoundEvents.BLOCK_FLY_DEATH.value();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return FTZSoundEvents.BLOCK_FLY_HURT.value();
    }

    @Override
    public void setPose(@NotNull Pose pose) {}

    @Override
    public void actuallyHurt(@NotNull DamageSource source, float amount) {
        super.actuallyHurt(source, amount);
        if (damageContainers != null) {
            boolean fancyDying = this.isDeadOrDying() && damageContainers.peek().getNewDamage() > getMaxHealth() * 2;
            if (fancyDying) funnyDeath(source);
        }

        int j = RandomUtil.nextInt((int) amount, (int) amount + 3);
        j = Math.min(j, (int) getMaxHealth());
        if (level() instanceof ServerLevel serverLevel && source.is(FTZDamageTypeTags.SPAWNS_SCRAP_PARTICLES_ON_BLOCK_FLY)) for (int i = 0; i < j; i++) {
            ParticleOptions options = FTZParticleTypes.METAL_SCRAP.random();
            if (options == null) return;
            Vec3 delta = RandomUtil.randomVec3().normalize().scale(0.3f);
            Vec3 pos = position().add(delta);
            serverLevel.sendParticles(options, pos.x, pos.y, pos.z, 1, 0.35, 0.35, 0.35, 0);
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(@NotNull Entity entity) {}

    @Override
    public boolean isFlying() {
        return isAlive();
    }

    @Override
    public void die(@NotNull DamageSource damageSource) {
        this.wingsFlappingFine.stop();
        this.wingsFlappingDamaged.stop();
        this.wingsFlappingMoribund.stop();
        this.addDeltaMovement(new Vec3(0, -0.2,0));
        super.die(damageSource);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {}

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        DamageSourcesHolder holder = LevelAttributesHelper.getDamageSources(level());
        if (holder == null) return false;

        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damagesource = holder.blockFly(this);
        Level var5 = this.level();
        if (var5 instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, f);
        }

        boolean flag = entity.hurt(damagesource, f);
        if (flag) {
            this.playSound(FTZSoundEvents.BLOCK_FLY_PUNCH.value());
            float f1 = this.getKnockback(entity, damagesource);
            if (f1 > 0.0F && entity instanceof LivingEntity livingentity) {
                livingentity.knockback((f1 * 0.5F), Mth.sin(this.getYRot() * 0.017453292F), (-Mth.cos(this.getYRot() * 0.017453292F)));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            Level var7 = this.level();
            if (var7 instanceof ServerLevel serverlevel) {
                EnchantmentHelper.doPostAttackEffects(serverlevel, entity, damagesource);
            }

            this.setLastHurtMob(entity);
            this.playAttackSound();
        }

        return flag;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) visualTick();
        else entityData.set(IS_ANGRY, getTarget() != null);

        setupAnim();
        if (isDeadOrDying()) {
            Vec3 vec3 = this.getDeltaMovement();
            this.setYRot(-((float)Mth.atan2(vec3.x, vec3.z)) * (180.0F / (float)Math.PI));
            this.yBodyRot = this.getYRot();
            applyGravity();
        }
        if (life > 0) life--;
        if (life == 0 && !isDeadOrDying()) {
            setHealth(0f);
            die(damageSources().generic());
            return;
        }

        if (tickCount % 8 == 0 && this.level().isClientSide() && isAlive() && getState() != WellBeing.MORIBUND) {
            FantazicUtil.entityChasingSound(
                    this,
                    FTZSoundEvents.BLOCK_FLY_BUZZING.value(),
                    SoundSource.NEUTRAL,
                    getState() == WellBeing.DAMAGED ? 0.5f : 1f,
                    1f
            );
        }
        adjustAttributes();
    }

    @Override
    public void knockback(double strength, double x, double z) {}

    @Override
    public void heal(float healAmount) {}

    @Override
    public int getNoActionTime() {
        return super.getNoActionTime();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BlockFly.AttackGoal());
        this.goalSelector.addGoal(4, new BlockFly.FollowOwnerGoal());
        this.targetSelector.addGoal(2, new BlockFly.OwnerHurtByTargetGoal());
        this.targetSelector.addGoal(3, new BlockFly.OwnerHurtTargetGoal());
        this.goalSelector.addGoal(4, new BlockFly.WanderGoal());
        this.goalSelector.addGoal(5, new BlockFly.LookGoal());
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("owner")) {
            this.ownerUUID = tag.getUUID("owner");
            this.cachedOwner = null;
        }
        this.life = tag.getInt("life");
        this.entityData.set(IS_LEFTY, tag.getBoolean("lefty"));
        this.entityData.set(FUNNY_DEATH, tag.getBoolean("funny_death"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUUID != null) tag.putUUID("owner", ownerUUID);
        tag.putInt("life", life);
        tag.putBoolean("lefty", entityData.get(IS_LEFTY));
        tag.putBoolean("funny_death", entityData.get(FUNNY_DEATH));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 9.0)
                .add(Attributes.ARMOR, 14.0)
                .add(Attributes.ATTACK_DAMAGE, 3.5)
                .add(Attributes.FLYING_SPEED, 0.1)
                .add(Attributes.GRAVITY);
    }

    public enum WellBeing {
        FINE, DAMAGED, MORIBUND;

        static WellBeing getState(float percent) {
            if (percent > 0.7f) return FINE;
            if (percent > 0.4f) return DAMAGED;
            else return MORIBUND;
        }
    }

    class BlockFlyControl extends MoveControl {
        
        private final BlockFly blockFly = BlockFly.this;
        private int floatDuration;

        public BlockFlyControl() {
            super(BlockFly.this);
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration = this.floatDuration + blockFly.getRandom().nextInt(5) + 2;
                    Vec3 vec3 = new Vec3(this.wantedX - blockFly.getX(), this.wantedY - blockFly.getY(), this.wantedZ - blockFly.getZ());
                    double d0 = vec3.length();
                    vec3 = vec3.normalize();
                    if (this.canReach(vec3, Mth.ceil(d0))) {
                        blockFly.setDeltaMovement(blockFly.getDeltaMovement().add(vec3.scale(flyingSpeed())));
                    } else {
                        this.operation = MoveControl.Operation.WAIT;
                    }
                }
            }
        }

        private boolean canReach(Vec3 pos, int length) {
            AABB aabb = blockFly.getBoundingBox();

            for (int i = 1; i < length; i++) {
                aabb = aabb.move(pos);
                if (!blockFly.level().noCollision(blockFly, aabb)) {
                    return false;
                }
            }

            return true;
        }
    }

    class FollowOwnerGoal extends Goal {

        private int timeToRecalculatePath;
        private float oldWaterCost;
        private final BlockFly blockFly = BlockFly.this;
        private final PathNavigation navigation;

        public FollowOwnerGoal() {
            this.navigation = blockFly.getNavigation();
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            Entity entity = blockFly.getOwner();
            if (entity == null) return false;
            else return !(blockFly.distanceTo(entity) < DISTANCE_TO_OWNER_WANDER);
        }

        public boolean canContinueToUse() {
            if (blockFly.getOwner() == null || this.navigation.isDone()) return false;
            else return blockFly.distanceTo(blockFly.getOwner()) > 1.5f;
        }

        public void start() {
            this.timeToRecalculatePath = 0;
            this.oldWaterCost = blockFly.getPathfindingMalus(PathType.WATER);
            blockFly.setPathfindingMalus(PathType.WATER, 0.0F);
        }

        public void stop() {
            this.navigation.stop();
            blockFly.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
        }

        public void tick() {
            Entity owner = blockFly.getOwner();
            if (owner == null) return;
            boolean flag = blockFly.shouldTryTeleportToOwner();
            if (!flag) blockFly.getLookControl().setLookAt(blockFly.getOwner(), 10.0F, blockFly.getMaxHeadXRot());

            if (--this.timeToRecalculatePath <= 0) {
                this.timeToRecalculatePath = this.adjustedTickDelay(10);
                if (flag) blockFly.tryToTeleportToOwner();
                else this.navigation.moveTo(blockFly.getOwner(), 1.2);
            }

            double d1 = owner.getX() - blockFly.getX();
            double d2 = owner.getZ() - blockFly.getZ();
            blockFly.setYRot(-((float)Mth.atan2(d1, d2)) * (180.0F / (float)Math.PI));
            blockFly.yBodyRot = blockFly.getYRot();
        }
    }

    class AttackGoal extends Goal {

        private int interval = 0;
        private final BlockFly blockFly = BlockFly.this;

        public AttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            LivingEntity target = blockFly.getTarget();
            return target != null && target.isAlive();
        }

        public boolean canContinueToUse() {
            return blockFly.getMoveControl().hasWanted()
                    && blockFly.getTarget() != null && blockFly.getTarget().isAlive()
                    && blockFly.getTarget().distanceTo(blockFly) < DISTANCE_TO_OWNER_ATTACK;
        }

        public void start() {
            LivingEntity livingentity = blockFly.getTarget();
            if (livingentity != null) {
                Vec3 vec3 = livingentity.getEyePosition();
                blockFly.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 2.5);
            }

        }

        public void stop() {
            blockFly.setTarget(null);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (interval > 0) interval--;

            LivingEntity target = blockFly.getTarget();
            if (target != null) {
                AABB attackBox = blockFly.getBoundingBox().inflate(0.8, 0, 0.8);
                if (attackBox.intersects(target.getBoundingBox()) && interval == 0) {
                    this.interval = 20;
                    blockFly.doHurtTarget(target);
                } else {
                    Vec3 vec3 = target.getEyePosition();
                    blockFly.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 5.5);
                }
            }

        }
    }

    class LookGoal extends Goal {

        private final BlockFly blockFly = BlockFly.this;
        
        public LookGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = blockFly.getTarget();
            if (target == null || target.isRemoved()) {
                Vec3 vec3 = blockFly.getDeltaMovement();
                blockFly.setYRot(-((float)Mth.atan2(vec3.x, vec3.z)) * (180.0F / (float)Math.PI));
                blockFly.yBodyRot = blockFly.getYRot();
            } else {
                LivingEntity livingentity = blockFly.getTarget();
                if (livingentity.distanceToSqr(blockFly) < 4096.0) {
                    double d1 = livingentity.getX() - blockFly.getX();
                    double d2 = livingentity.getZ() - blockFly.getZ();
                    blockFly.setYRot(-((float)Mth.atan2(d1, d2)) * (180.0F / (float)Math.PI));
                    blockFly.yBodyRot = blockFly.getYRot();
                }
            }
        }
    }

    class WanderGoal extends Goal {
        
        private final BlockFly blockFly = BlockFly.this;

        private int interval = 0;

        public WanderGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (interval > 0) {
                interval--;
                return false;
            }

            MoveControl movecontrol = blockFly.getMoveControl();
            if (!movecontrol.hasWanted()) {
                interval = RandomUtil.nextInt(25, 65);
                return true;
            } else {
                double d0 = movecontrol.getWantedX() - blockFly.getX();
                double d1 = movecontrol.getWantedY() - blockFly.getY();
                double d2 = movecontrol.getWantedZ() - blockFly.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                boolean flag = d3 < 1.0 || d3 > 3600.0;
                if (flag) interval = RandomUtil.nextInt(25, 65);
                return flag;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            Entity owner = blockFly.getOwner();
            Vec3 center = owner == null ? blockFly.position() : owner.position();

            Vec3 delta = RandomUtil.randomVec3().normalize().scale(RandomUtil.nextDouble(1f, 3f));

            double X = center.x + delta.x;
            double Y = center.y + delta.y;
            double Z = center.z + delta.z;

            if (owner != null) {
                double oY = owner.getY();
                Y = RandomUtil.nextDouble(oY + 1.5, oY + 3.75);
            }

            blockFly.getMoveControl().setWantedPosition(X, Y, Z, 1.0);
        }
    }

    class OwnerHurtTargetGoal extends TargetGoal {

        private final BlockFly blockFly = BlockFly.this;
        private LivingEntity ownerLastHurt = null;
        private int timestamp;

        public OwnerHurtTargetGoal() {
            super(BlockFly.this, false);
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        public boolean canUse() {
            Entity owner = blockFly.getOwner();
            if (!(owner instanceof LivingEntity livingOwner)) return false;

            this.ownerLastHurt = livingOwner.getLastHurtMob();
            if (this.ownerLastHurt == null) return false;
            int i = livingOwner.getLastHurtMobTimestamp();
            return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.blockFly.wantsToAttack(this.ownerLastHurt);
        }

        @Override
        public boolean canContinueToUse() {
            Entity owner = blockFly.getOwner();
            boolean close = this.ownerLastHurt != null && owner != null && this.ownerLastHurt.distanceTo(owner) < DISTANCE_TO_OWNER_ATTACK;
            return this.ownerLastHurt != null && close && this.ownerLastHurt.isAlive();
        }

        public void start() {
            this.mob.setTarget(this.ownerLastHurt);
            Entity owner = this.blockFly.getOwner();
            if (owner instanceof LivingEntity livingEntity)
                this.timestamp = livingEntity.getLastHurtMobTimestamp();
            super.start();
        }
    }

    class OwnerHurtByTargetGoal extends TargetGoal {
        private final BlockFly blockFly = BlockFly.this;
        private LivingEntity ownerLastHurtBy;
        private int timestamp;

        public OwnerHurtByTargetGoal() {
            super(BlockFly.this, false);
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        public boolean canUse() {
            Entity owner = blockFly.getOwner();
            if (!(owner instanceof LivingEntity livingOwner)) return false;
            this.ownerLastHurtBy = livingOwner.getLastHurtByMob();
            int i = livingOwner.getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && blockFly.wantsToAttack(this.ownerLastHurtBy);
        }

        public void start() {
            blockFly.setTarget(this.ownerLastHurtBy);
            Entity owner = blockFly.getOwner();
            if (owner instanceof LivingEntity livingOwner) {
                this.timestamp = livingOwner.getLastHurtByMobTimestamp();
            }

            super.start();
        }
    }
}
