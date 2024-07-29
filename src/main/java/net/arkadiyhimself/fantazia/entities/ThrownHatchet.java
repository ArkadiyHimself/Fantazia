package net.arkadiyhimself.fantazia.entities;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.data.DataManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.data.newdata.HatchetStuck;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZEnchantments;
import net.arkadiyhimself.fantazia.registries.FTZEntityTypes;
import net.arkadiyhimself.fantazia.util.library.SPHEREBOX;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class ThrownHatchet extends AbstractArrow {
    private enum Direction {
        ONLY$X, ONLY$Y, ONLY$Z, X$Y, X$Z, Y$Z, XYZ;
    }
    public static final EntityDataAccessor<Byte> ID_PHASING = SynchedEntityData.defineId(ThrownHatchet.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Byte> ID_RICOCHET = SynchedEntityData.defineId(ThrownHatchet.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Byte> ID_HEADSHOT = SynchedEntityData.defineId(ThrownHatchet.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(ThrownHatchet.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(ThrownHatchet.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Float> VISUAL_ROT0 = SynchedEntityData.defineId(ThrownHatchet.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> VISUAL_ROT1 = SynchedEntityData.defineId(ThrownHatchet.class, EntityDataSerializers.FLOAT);
    private float rotSpeed;
    private int ricochets;
    private int phasingTicks;
    private boolean isPhasing = false;
    private boolean retrieving = false;
    private boolean ricocheted;
    public ThrownHatchet(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public ThrownHatchet(Level pLevel, LivingEntity shooter, ItemStack hatchetItem, float charge) {
        super(FTZEntityTypes.HATCHET, shooter, pLevel);
        shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, charge * 2F, 1.0F);
        rotSpeed();

        if (shooter instanceof Player player && player.getAbilities().instabuild) this.pickup = Pickup.CREATIVE_ONLY;
        else this.pickup = Pickup.ALLOWED;

        throwingData(hatchetItem);
    }
    public ThrownHatchet(Level level, Vec3 vec3, ItemStack stack) {
        super(FTZEntityTypes.HATCHET, level);
        setPos(vec3);
        droppingData(stack);
        entityData.set(STACK, stack);
        pickup = Pickup.ALLOWED;
    }

    @Override
    public void tick() {
        if (phasingTicks > 0) phasingTicks--;
        if (!this.inGround) rotate();
        else this.entityData.set(VISUAL_ROT0, this.entityData.get(VISUAL_ROT1));
        Vec3 vec3 = this.getDeltaMovement();
        double d5 = vec3.x;
        double d1 = vec3.z;

        if (isPhasing) {
            noPhysics = true;
            if (phasingTicks <= 0) retrieving = true;
            if (retrieving) {
                setDeltaMovement(chasing(getOwner(), 0.2f));
                setNoGravity(true);
                pickup = Pickup.ALLOWED;
            }
            EntityHitResult entityHitResult = findEntity();
            if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity livingEntity) hitEntity(livingEntity);
        }
        super.tick();
        if (noPhysics) this.setYRot((float)(Mth.atan2(d5, d1) * (double)(180F / (float)Math.PI)));
    }

    @Override
    public @NotNull ItemStack getPickupItem() {
        return this.entityData.get(STACK).copy();
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ID_PHASING, (byte)0);
        entityData.define(ID_RICOCHET, (byte)0);
        entityData.define(ID_HEADSHOT, (byte)0);
        entityData.define(ID_FOIL, false);
        entityData.define(STACK, ItemStack.EMPTY);
        entityData.define(VISUAL_ROT0, 0f);
        entityData.define(VISUAL_ROT1, 0f);
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        if (!(pResult.getEntity() instanceof LivingEntity livingEntity) || level().isClientSide()) return;
        hitEntity(livingEntity);
        if (!ricocheted) {
            ricocheted = true;
            if (ricochets > 0) {
                ricochets--;
                if (!ricochetTarget(entity -> entity != livingEntity)) ricochetIntoNowhere(0.1f);
            } else {
                if (pickup == Pickup.ALLOWED) {
                    if (tryStuck(livingEntity)) discard();
                    else ricochetIntoNowhere(0.1f);
                }
            }
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        if (level().isClientSide()) return;
        if (isBlockDestroyable(level().getBlockState(pResult.getBlockPos()))) level().destroyBlock(pResult.getBlockPos(), false, getOwner());
        else if (ricochets > 0) {
            ricochets--;
            boolean flag = ricochetTarget((entity) -> true);
            if (!flag) {
                Direction dir = direction(pResult.getBlockPos());
                if (dir != Direction.ONLY$Y || !ricocheted) ricochetIntoNowhere(dir, 0.45f);
                else actuallyHitBlock(pResult);
            }
        } else actuallyHitBlock(pResult);
    }
    @Nullable
    public EntityHitResult findEntity() {
        Vec3 pos = this.position();
        Vec3 delta = this.getDeltaMovement();
        Vec3 nextPos = pos.add(delta);

        return this.findHitEntity(pos, nextPos);
    }
    public DamageSource source() {
        return new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(FTZDamageTypes.HATCHET), this, getOwner() == null ? this : getOwner());
    }
    public void hitEntity(LivingEntity livingEntity) {
        float dmg = 1.5f;
        if (getPickupItem().getItem() instanceof HatchetItem hatchetItem) dmg += hatchetItem.getTier().getAttackDamageBonus();
        double headDist = Math.abs(this.getY() - livingEntity.getEyeY());
        if (headDist <= 0.3) dmg += entityData.get(ID_HEADSHOT);
        livingEntity.hurt(source(), dmg);

        int projProt = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, livingEntity);
        if (projProt < 4) EffectHelper.makeStunned(livingEntity, 15 * (4 - projProt));
    }
    private void throwingData(ItemStack stack) {
        entityData.set(ID_PHASING, (byte) stack.getEnchantmentLevel(FTZEnchantments.PHASING));
        entityData.set(ID_RICOCHET, (byte) stack.getEnchantmentLevel(FTZEnchantments.RICOCHET));
        entityData.set(ID_HEADSHOT, (byte) stack.getEnchantmentLevel(FTZEnchantments.HEADSHOT));
        entityData.set(ID_FOIL, stack.hasFoil());
        entityData.set(STACK, stack);
        ricochets = stack.getEnchantmentLevel(FTZEnchantments.RICOCHET);
        int phasing = stack.getEnchantmentLevel(FTZEnchantments.PHASING);
        if (phasing > 0) {
            phasingTicks = phasing * 5 + 5;
            isPhasing = true;
            pickup = Pickup.DISALLOWED;
        }


    }
    private void droppingData(ItemStack stack) {
        entityData.set(ID_PHASING, (byte) 0);
        entityData.set(ID_RICOCHET, (byte) 0);
        entityData.set(ID_HEADSHOT, (byte) 0);
        entityData.set(ID_FOIL, stack.hasFoil());
        entityData.set(STACK, stack);
        ricochets = 0;
        phasingTicks = 0;
        isPhasing = false;
    }
    public void actuallyHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        ricochets = 0;
        BlockPos blockPos = blockPosition().subtract(pResult.getBlockPos());
        float rot;
        if (blockPos.getY() > 0) rot = Mth.clamp(getXRot(), 60f, 120f);
        else if (blockPos.getY() < 0) rot = Mth.clamp(getXRot(), -60f, 120f);
        else rot = Mth.clamp(getXRot(), 120f, 180f);
        entityData.set(VISUAL_ROT0, rot);
        entityData.set(VISUAL_ROT1, rot);
    }
    public void rotSpeed() {
        this.rotSpeed = (float) getDeltaMovement().horizontalDistance() * 10;
    }
    public void rotate() {
        float rot1 = this.entityData.get(VISUAL_ROT1);
        this.entityData.set(VISUAL_ROT0, rot1);
        this.entityData.set(VISUAL_ROT1, rot1 - rotSpeed);
    }
    private void startFalling() {
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F));
    }
    public boolean isDestroyable(BlockState state) {
        return phasingTicks <= 0 && (state.getBlock() instanceof GlassBlock || state.getBlock() instanceof StainedGlassPaneBlock || state.getBlock() == Blocks.GLASS_PANE || state.getBlock() instanceof LeavesBlock);
    }
    public boolean isFoil() {
        return entityData.get(ID_FOIL);
    }
    public int phasingTicks() {
        return phasingTicks;
    }
    public Vec3 chasing(Entity target, float multiplier) {
        return chasing(target.position(), multiplier);
    }
    public Vec3 chasing(Vec3 target, float multiplier) {
        if (target == null) return Vec3.ZERO;
        Vec3 delta = target.subtract(this.position());
        return delta.normalize().scale(multiplier);
    }
    public boolean tryStuck(LivingEntity entity) {
        DataManager dataManager = DataGetter.getUnwrap(entity);
        if (dataManager == null) return false;
        HatchetStuck hatchetStuck = dataManager.takeData(HatchetStuck.class);
        if (hatchetStuck == null) return false;
        return hatchetStuck.stuck(this);
    }
    public void ricochetIntoNowhere(Direction direction, float mult) {
        ricocheted = true;
        Vec3 vec3 = this.getDeltaMovement();
        Vec3 newV3 = switch (direction) {
            case ONLY$X -> vec3.subtract(vec3.x() * 2,0,0).scale(mult);
            case ONLY$Y -> vec3.subtract(0,vec3.y() * 2,0).scale(mult);
            case ONLY$Z -> vec3.subtract(0,0,vec3.z() * 2).scale(mult);
            case X$Y -> vec3.subtract(vec3.x() * 2,vec3.y() * 2,0).scale(mult);
            case X$Z -> vec3.subtract(vec3.x() * 2,0,vec3.z() * 2).scale(mult);
            case Y$Z -> vec3.subtract(0,vec3.y() * 2,vec3.z() * 2).scale(mult);
            case XYZ -> vec3.subtract(vec3.x() * 2, vec3.y() * 2, vec3.z() * 2).scale(mult);
        };
        this.setDeltaMovement(newV3);
    }
    public boolean ricochetOffEntity(LivingEntity livingEntity) {
        SPHEREBOX spherebox = new SPHEREBOX(8, this.position());
        List<LivingEntity> entityList = spherebox.entitiesInside(level(), LivingEntity.class);
        entityList.removeIf(entity1 -> entity1 == getOwner() || entity1 == livingEntity);
        entityList.removeIf(this::noLOS);
        if (entityList.isEmpty()) return false;
        int i = Fantazia.RANDOM.nextInt(0, entityList.size());
        LivingEntity target = entityList.get(i);
        this.setDeltaMovement(chasing(target, 1f));
        return true;
    }
    public boolean ricochetOffBlock() {
        SPHEREBOX spherebox = new SPHEREBOX(8, this.position());
        List<LivingEntity> entityList = spherebox.entitiesInside(level(), LivingEntity.class);
        entityList.removeIf(this::noLOS);
        entityList.removeIf(entity1 -> entity1 == getOwner());
        if (entityList.isEmpty()) return false;
        int i = Fantazia.RANDOM.nextInt(0, entityList.size());
        LivingEntity target = entityList.get(i);
        this.setDeltaMovement(chasing(target, 1f));
        return true;
    }
    public boolean ricochetTarget(Predicate<LivingEntity> livingEntityPredicate) {
        SPHEREBOX spherebox = new SPHEREBOX(8, this.position());
        List<LivingEntity> entityList = spherebox.entitiesInside(level(), LivingEntity.class);
        entityList.removeIf(this::noLOS);
        entityList.removeIf(Predicate.not(livingEntityPredicate));
        entityList.removeIf(entity1 -> entity1 == getOwner() || entity1.hurtTime > 0);
        if (entityList.isEmpty()) return false;
        int i = Fantazia.RANDOM.nextInt(0, entityList.size());
        LivingEntity target = entityList.get(i);
        Vec3 head = target.getEyePosition();
        this.setDeltaMovement(chasing(head, 1f));
        return true;
    }
    public boolean noLOS(Entity pEntity) {
        if (pEntity.level() != this.level()) {
            return true;
        } else {
            Vec3 vec3 = new Vec3(this.getX(), this.getEyeY(), this.getZ());
            Vec3 vec31 = new Vec3(pEntity.getX(), pEntity.getEyeY(), pEntity.getZ());
            if (vec31.distanceTo(vec3) > 128.0D) {
                return true;
            } else {
                return this.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
            }
        }
    }
    public void ricochetIntoNowhere(float mult) {
        this.setDeltaMovement(this.getDeltaMovement().scale(mult));
    }
    public boolean isBlockDestroyable(BlockState state) {
        return (state.getBlock() instanceof GlassBlock || state.getBlock() instanceof StainedGlassPaneBlock || state.getBlock() == Blocks.GLASS_PANE || state.getBlock() instanceof LeavesBlock);
    }
    public Direction direction(BlockPos pos) {
        BlockPos blockPos = blockPosition().subtract(pos);
        // reminder: x is for West and East, z is for North and South
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        int modX = Math.abs(x);
        int modY = Math.abs(y);
        int modZ = Math.abs(z);
        if (modX == modY && modY == modZ) return Direction.XYZ;
        else {
            if (modX > modY && modX > modZ) return Direction.ONLY$X;
            else if (modY > modZ && modY > modX) return Direction.ONLY$Y;
            else if (modZ > modY && modZ > modX) return Direction.ONLY$Z;
            else {
                BlockState blockStateX = level().getBlockState(blockPosition().subtract(new BlockPos(x,0,0)));
                BlockState blockStateY = level().getBlockState(blockPosition().subtract(new BlockPos(0, y,0)));
                BlockState blockStateZ = level().getBlockState(blockPosition().subtract(new BlockPos(0,0, z)));
                if (modX == modY) {
                    if (blockStateX.isAir() && !blockStateY.isAir()) return Direction.ONLY$Y;
                    else if (!blockStateX.isAir() && blockStateY.isAir()) return Direction.ONLY$X;
                    else return Direction.X$Y;
                } else if (modY == modZ) {
                    if (blockStateY.isAir() && !blockStateZ.isAir()) return Direction.ONLY$Z;
                    else if (!blockStateY.isAir() && blockStateZ.isAir()) return Direction.ONLY$Y;
                    else return Direction.Y$Z;
                } else {
                    if (blockStateX.isAir() && !blockStateZ.isAir()) return Direction.ONLY$Z;
                    else if (!blockStateX.isAir() && blockStateZ.isAir()) return Direction.ONLY$X;
                    else return Direction.X$Z;
                }
            }
        }
    }
}
