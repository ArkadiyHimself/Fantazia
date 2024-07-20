package net.arkadiyhimself.fantazia.Entities;

import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.Items.Weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.api.DamageTypeRegistry;
import net.arkadiyhimself.fantazia.api.EnchantmentRegistry;
import net.arkadiyhimself.fantazia.api.EntityTypeRegistry;
import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class HatchetEntity extends AbstractArrow {
    @Nullable
    private BlockState lastState;
    private final DamageSource hatchet;
    public static final EntityDataAccessor<Byte> ID_PHASING = SynchedEntityData.defineId(HatchetEntity.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Byte> ID_RICOCHET = SynchedEntityData.defineId(HatchetEntity.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Byte> ID_HEADSHOT = SynchedEntityData.defineId(HatchetEntity.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(HatchetEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(HatchetEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> ROT_SPEED = SynchedEntityData.defineId(HatchetEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> VISUAL_ROT0 = SynchedEntityData.defineId(HatchetEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> VISUAL_ROT1 = SynchedEntityData.defineId(HatchetEntity.class, EntityDataSerializers.FLOAT);
    private ItemStack hatchetItem;
    public int ricochets;
    private boolean ricocheted = false;
    public int phasingTicks;
    public HatchetEntity(EntityType<? extends HatchetEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        hatchet = new DamageSource(pLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.HATCHET), this, getOwner() == null ? this : getOwner());

    }
    public HatchetEntity(Level level, LivingEntity shooter, ItemStack stack) {
        super(EntityTypeRegistry.HATCHET.get(), level);
        setOwner(shooter);
        setPos(shooter.getX(), shooter.getEyeY() - (double)0.1F, shooter.getZ());
        setXRot(shooter.getXRot() + 90);
        throwingData(stack);
        calculateRotSpeed();
        inGround = false;
        hatchetItem = stack.copy();
        hatchet = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.HATCHET), this, getOwner() == null ? this : getOwner());
    }
    public HatchetEntity(Level level, Vec3 vec3, ItemStack stack) {
        super(EntityTypeRegistry.HATCHET.get(), level);
        setPos(vec3);
        droppingData(stack);
        entityData.set(STACK, stack);
        hatchetItem = stack.copy();
        hatchet = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.HATCHET), this, getOwner() == null ? this : getOwner());
    }
    public void calculateRotSpeed() {
        float rotSpd = (float) getDeltaMovement().horizontalDistance() * 10;
        if (rotSpd > 0) {
            entityData.set(ROT_SPEED, rotSpd);
        }

    }
    public @NotNull ItemStack getPickupItem() {
        return this.entityData.get(STACK);
    }
    @Override
    public void tick() {
        // I had to rewrite the WHOLE fucking tick() method just because devs couldn't separate noPhysics from noGravity...
        baseTick();
        rotate();
        boolean flag = phasingTicks > 0;
        if (flag) phasingTicks--;
        if (this.blockPosition().getY() <= -63) phasingTicks = 0;
        boolean flag1 = this.isNoPhysics();
        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
            this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level().getBlockState(blockpos);
        if (!blockstate.isAir() && !flag1 && !flag && !isBlockDestroyable(blockstate)) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for(AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            this.clearFire();
        }

        if (this.inGround && !flag1) {
            if (this.lastState != blockstate && this.shouldFall()) {
                this.startFalling();
            }

            ++this.inGroundTime;
        } else {
            this.inGroundTime = 0;
            Vec3 vec32 = this.position();
            Vec3 vec33 = vec32.add(vec3);
            HitResult hitresult = this.level().clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hitresult.getType() != HitResult.Type.MISS) {
                vec33 = hitresult.getLocation();
            }

            while(!this.isRemoved()) {
                EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
                if (entityhitresult != null) {
                    hitresult = entityhitresult;
                }

                if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult)hitresult).getEntity();
                    Entity entity1 = this.getOwner();
                    if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                        hitresult = null;
                        entityhitresult = null;
                    }
                }

                if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !flag1) {
                    switch (net.minecraftforge.event.ForgeEventFactory.onProjectileImpactResult(this, hitresult)) {
                        case SKIP_ENTITY:
                            if (hitresult.getType() != HitResult.Type.ENTITY) { // If there is no entity, we just return default behaviour
                                this.onHit(hitresult);
                                this.hasImpulse = true;
                                break;
                            }
                            entityhitresult = null; // Don't process any further
                            break;
                        case STOP_AT_CURRENT_NO_DAMAGE:
                            this.discard();
                            entityhitresult = null; // Don't process any further
                            break;
                        case STOP_AT_CURRENT:
                            this.setPierceLevel((byte) 0);
                        case DEFAULT:
                            this.onHit(hitresult);
                            this.hasImpulse = true;
                            break;
                    }
                }

                if (entityhitresult == null || this.getPierceLevel() <= 0) {
                    break;
                }

                hitresult = null;
            }

            if (this.isRemoved())
                return;

            vec3 = this.getDeltaMovement();
            double d5 = vec3.x;
            double d6 = vec3.y;
            double d1 = vec3.z;

            double d7 = this.getX() + d5;
            double d2 = this.getY() + d6;
            double d3 = this.getZ() + d1;
            double d4 = vec3.horizontalDistance();

            this.setXRot((float)(Mth.atan2(d6, d4) * (double)(180F / (float)Math.PI)));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            float f = 0.99F;
            float f1 = 0.05F;
            if (this.isInWater()) {
                for(int j = 0; j < 4; ++j) {
                    float f2 = 0.25F;
                    this.level().addParticle(ParticleTypes.BUBBLE, d7 - d5 * 0.25D, d2 - d6 * 0.25D, d3 - d1 * 0.25D, d5, d6, d1);
                }

                f = this.getWaterInertia();
            }

            this.setDeltaMovement(vec3.scale((double)f));
            if (!this.isNoGravity() && !flag1) {
                Vec3 vec34 = this.getDeltaMovement();
                this.setDeltaMovement(vec34.x, vec34.y - (flag ? 0.015 : 0.05), vec34.z);
            }

            this.setPos(d7, d2, d3);
            this.checkInsideBlocks();
        }
    }
    private boolean shouldFall() {
        return this.inGround && this.level().noCollision((new AABB(this.position(), this.position())).inflate(0.06D));
    }
    public void rotate() {
        entityData.set(VISUAL_ROT0, entityData.get(VISUAL_ROT1));
        entityData.set(VISUAL_ROT1, entityData.get(VISUAL_ROT1) - entityData.get(ROT_SPEED));
    }
    private void startFalling() {
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ID_PHASING, (byte)0);
        entityData.define(ID_RICOCHET, (byte)0);
        entityData.define(ID_HEADSHOT, (byte)0);
        entityData.define(STACK, ItemStack.EMPTY);
        entityData.define(ID_FOIL, false);
        entityData.define(ROT_SPEED, 0f);
        entityData.define(VISUAL_ROT0, 0f);
        entityData.define(VISUAL_ROT1, 0f);
    }
    private void throwingData(ItemStack stack) {
        entityData.set(ID_PHASING, (byte) stack.getEnchantmentLevel(EnchantmentRegistry.PHASING.get()));
        entityData.set(ID_RICOCHET, (byte) stack.getEnchantmentLevel(EnchantmentRegistry.RICOCHET.get()));
        entityData.set(ID_HEADSHOT, (byte) stack.getEnchantmentLevel(EnchantmentRegistry.HEADSHOT.get()));
        entityData.set(STACK, stack);
        entityData.set(ID_FOIL, stack.hasFoil());
        ricochets = stack.getEnchantmentLevel(EnchantmentRegistry.RICOCHET.get());
        phasingTicks = stack.getEnchantmentLevel(EnchantmentRegistry.PHASING.get()) * 20;

    }
    private void droppingData(ItemStack stack) {
        entityData.set(ID_PHASING, (byte) 0);
        entityData.set(ID_RICOCHET, (byte) 0);
        entityData.set(ID_HEADSHOT, (byte) stack.getEnchantmentLevel(EnchantmentRegistry.HEADSHOT.get()));
        entityData.set(STACK, stack);
        entityData.set(ID_FOIL, stack.hasFoil());
        ricochets = 0;
        phasingTicks = 0;
    }
    public boolean isBlockDestroyable(BlockState state) {
        return phasingTicks <= 0 && (state.getBlock() instanceof GlassBlock || state.getBlock() instanceof StainedGlassPaneBlock || state.getBlock() == Blocks.GLASS_PANE || state.getBlock() instanceof LeavesBlock);
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        if (pResult.getEntity() instanceof LivingEntity livingEntity) {
            float dmg = 1.5f;
            if (getPickupItem().getItem() instanceof HatchetItem hatchetItem) {
                dmg += hatchetItem.getThrowDamage();
            }
            double headDist = Math.abs(this.getY() - livingEntity.getEyeY());
            if (headDist <= 0.3) {
                dmg += entityData.get(ID_HEADSHOT) * 2.5f;
            }
            livingEntity.hurt(hatchet, dmg);
            WhereMagicHappens.Abilities.addEffectWithoutParticles(livingEntity, MobEffectRegistry.STUN.get(), 60);
            if (phasingTicks <= 0) {
                if (ricochets > 0) {
                    ricochets = 0;
                    ricocheted = true;
                    this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
                } else if (!tryToGetStuck(livingEntity)) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
                }
            }
        }
    }
    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (phasingTicks <= 0) {
            if (isBlockDestroyable(level().getBlockState(pResult.getBlockPos())) && !level().isClientSide()) {
                level().destroyBlock(pResult.getBlockPos(), false, getOwner());
                return;
            }
            if (ricochets > 0) {
                WhereMagicHappens.ModdedEntities.Direction dir = direction(pResult.getBlockPos());
                ricochets--;
                if (dir != WhereMagicHappens.ModdedEntities.Direction.ONLY$Y || !ricocheted) {
                    WhereMagicHappens.ModdedEntities.hatchetRicochet(this, direction(pResult.getBlockPos()), 0.45f);
                    ricocheted = true;
                    return;
                }
            }
            actuallyHitBlock(pResult);
        }
    }
    public void actuallyHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        entityData.set(ROT_SPEED, 0f);
        ricochets = 0;
        lastState = level().getBlockState(pResult.getBlockPos());
        BlockPos blockPos = blockPosition().subtract(pResult.getBlockPos());
        float rot;
        if (blockPos.getY() > 0) rot = Mth.clamp(getXRot(), 60f, 120f);
        else if (blockPos.getY() < 0) rot = Mth.clamp(getXRot(), -60f, 120f);
        else rot = Mth.clamp(getXRot(), 120f, 180f);
        entityData.set(VISUAL_ROT0, rot);
        entityData.set(VISUAL_ROT1, rot);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return super.getDefaultHitGroundSoundEvent();
    }
    public WhereMagicHappens.ModdedEntities.Direction direction(BlockPos pos) {
        BlockPos blockPos = blockPosition().subtract(pos);
        // reminder: x is for Weast and East, z is for North and South
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        int modX = Math.abs(x);
        int modY = Math.abs(y);
        int modZ = Math.abs(z);
        if (modX == modY && modY == modZ) {
            return WhereMagicHappens.ModdedEntities.Direction.XYZ;
        } else {
            if (modX > modY && modX > modZ) {
                return WhereMagicHappens.ModdedEntities.Direction.ONLY$X;
            } else if (modY > modZ && modY > modX) {
                return WhereMagicHappens.ModdedEntities.Direction.ONLY$Y;
            } else if (modZ > modY && modZ > modX) {
                return WhereMagicHappens.ModdedEntities.Direction.ONLY$Z;
            } else {
                BlockState blockStateX = level().getBlockState(blockPosition().subtract(new BlockPos(x,0,0)));
                BlockState blockStateY = level().getBlockState(blockPosition().subtract(new BlockPos(0, y,0)));
                BlockState blockStateZ = level().getBlockState(blockPosition().subtract(new BlockPos(0,0, z)));
                if (modX == modY) {
                    if (blockStateX.isAir() && !blockStateY.isAir()) return WhereMagicHappens.ModdedEntities.Direction.ONLY$Y;
                    else if (!blockStateX.isAir() && blockStateY.isAir()) return WhereMagicHappens.ModdedEntities.Direction.ONLY$X;
                    else return WhereMagicHappens.ModdedEntities.Direction.X$Y;
                } else if (modY == modZ) {
                    if (blockStateY.isAir() && !blockStateZ.isAir()) return WhereMagicHappens.ModdedEntities.Direction.ONLY$Z;
                    else if (!blockStateY.isAir() && blockStateZ.isAir()) return WhereMagicHappens.ModdedEntities.Direction.ONLY$Y;
                    else return WhereMagicHappens.ModdedEntities.Direction.Y$Z;
                } else {
                    if (blockStateX.isAir() && !blockStateZ.isAir()) return WhereMagicHappens.ModdedEntities.Direction.ONLY$Z;
                    else if (!blockStateX.isAir() && blockStateZ.isAir()) return WhereMagicHappens.ModdedEntities.Direction.ONLY$X;
                    else return WhereMagicHappens.ModdedEntities.Direction.X$Z;
                }
            }
        }
    }
    public boolean tryToGetStuck(LivingEntity entity) {
        boolean flag = WhereMagicHappens.Abilities.tryToGetStuck(entity, entityData.get(STACK).copy());
        if (flag) discard();
        return flag;
    }
    public boolean isFoil() {
        return entityData.get(ID_FOIL);
    }
}
