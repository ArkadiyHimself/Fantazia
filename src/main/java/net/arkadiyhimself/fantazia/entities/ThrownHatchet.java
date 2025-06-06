package net.arkadiyhimself.fantazia.entities;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.StuckHatchetHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.registries.FTZEnchantments;
import net.arkadiyhimself.fantazia.registries.FTZEntityTypes;
import net.arkadiyhimself.fantazia.util.library.SphereBox;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ApplyEffect;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ThrownHatchet extends AbstractArrow {

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
        super(FTZEntityTypes.HATCHET.get(), shooter, pLevel, hatchetItem, null);
        shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, charge * 2F, 1.0F);
        rotSpeed();

        if (shooter instanceof Player player && player.hasInfiniteMaterials()) this.pickup = Pickup.CREATIVE_ONLY;
        else this.pickup = Pickup.ALLOWED;

        throwingData(hatchetItem);
    }

    public ThrownHatchet(Level level, Vec3 vec3, ItemStack stack) {
        super(FTZEntityTypes.HATCHET.get(), level);
        setPos(vec3);
        droppingData(stack);

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
                Vec3 delta = getOwner() == null ? new Vec3(0,0,0) : chasing(getOwner(), 0.2f);
                setDeltaMovement(delta);
                setNoGravity(true);
                if (pickup != Pickup.CREATIVE_ONLY) pickup = Pickup.ALLOWED;
            }
            EntityHitResult entityHitResult = findEntity();
            if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity livingEntity) attackEntity(livingEntity);
        }
        super.tick();
        if (noPhysics) this.setYRot((float)(Mth.atan2(d5, d1) * (double)(180F / (float)Math.PI)));
    }

    @Override
    public @NotNull ItemStack getPickupItem() {
        return this.entityData.get(STACK).copy();
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(ID_PHASING, (byte)0);
        pBuilder.define(ID_RICOCHET, (byte)0);
        pBuilder.define(ID_HEADSHOT, (byte)0);
        pBuilder.define(ID_FOIL, false);
        pBuilder.define(STACK, ItemStack.EMPTY);
        pBuilder.define(VISUAL_ROT0, 0f);
        pBuilder.define(VISUAL_ROT1, 0f);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("hatchet", 10)) this.entityData.set(STACK, ItemStack.parse(this.registryAccess(), pCompound.getCompound("hatchet")).orElse(getDefaultPickupItem()));

        this.rotSpeed = pCompound.getFloat("rotSpeed");
        this.ricochets = pCompound.getInt("ricochets");
        this.phasingTicks = pCompound.getInt("phasingTicks");
        this.isPhasing = pCompound.getBoolean("isPhasing");
        this.retrieving = pCompound.getBoolean("retrieving");
        this.ricocheted = pCompound.getBoolean("ricocheted");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        ItemStack stack = this.getPickupItem();
        if (stack.getItem() instanceof HatchetItem) pCompound.put("hatchet", stack.save(this.registryAccess()));

        pCompound.putFloat("rotSpeed", this.rotSpeed);
        pCompound.putInt("ricochets", this.ricochets);
        pCompound.putInt("phasingTicks", this.phasingTicks);
        pCompound.putBoolean("isPhasing", this.isPhasing);
        pCompound.putBoolean("retrieving", this.retrieving);
        pCompound.putBoolean("ricocheted", this.ricocheted);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        if (!(pResult.getEntity() instanceof LivingEntity livingEntity) || level().isClientSide()) return;
        attackEntity(livingEntity);
        if (!ricocheted) {
            ricocheted = true;
            if (ricochets > 0) {
                ricochets--;
                if (!ricochetTarget(entity -> entity != livingEntity)) ricochetIntoNowhere(0.1f);
            } else if (pickup == Pickup.ALLOWED) {
                if (tryStuck(livingEntity)) discard();
                else ricochetIntoNowhere(0.1f);
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
                FancyDirection dir = direction(pResult.getBlockPos());
                if (dir != FancyDirection.ONLY$Y || !ricocheted) ricochetIntoNowhere(dir, 0.45f);
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

    @Override
    protected boolean tryPickup(@NotNull Player pPlayer) {
        if (this.phasingTicks > 0) return false;
        return super.tryPickup(pPlayer);
    }

    public void attackEntity(LivingEntity livingEntity) {
        Registry<Enchantment> enchantmentRegistry = registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        float dmg = 1.5f;
        if (getPickupItem().getItem() instanceof HatchetItem hatchetItem) dmg += hatchetItem.getTier().getAttackDamageBonus();
        double headDist = Math.abs(getY() - livingEntity.getEyeY());
        if (headDist <= 0.35) dmg += entityData.get(ID_HEADSHOT) * 2;
        LevelAttributesHelper.hurtEntity(livingEntity, this, dmg, DamageSourcesHolder::hatchet);
        if (livingEntity.getType() == EntityType.ENDERMAN && !isPhasing) return;

        Optional<Holder.Reference<Enchantment>> projProt = enchantmentRegistry.getHolder(Enchantments.PROJECTILE_PROTECTION);
        int projProtect = projProt.map(enchantmentReference -> EnchantmentHelper.getEnchantmentLevel(enchantmentReference, livingEntity)).orElse(0);

        if (projProtect < 4) ApplyEffect.makeStunned(livingEntity, 15 * (4 - projProtect));
    }

    private void throwingData(ItemStack stack) {
        Registry<Enchantment> enchantmentRegistry = this.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        Optional<Holder.Reference<Enchantment>> phasing = enchantmentRegistry.getHolder(FTZEnchantments.PHASING);
        entityData.set(ID_PHASING, phasing.isEmpty() ? 0 : (byte) stack.getEnchantmentLevel(phasing.get()));

        Optional<Holder.Reference<Enchantment>> ricochet = enchantmentRegistry.getHolder(FTZEnchantments.RICOCHET);
        entityData.set(ID_RICOCHET, ricochet.isEmpty() ? 0 : (byte) stack.getEnchantmentLevel(ricochet.get()));

        Optional<Holder.Reference<Enchantment>> headshot = enchantmentRegistry.getHolder(FTZEnchantments.HEADSHOT);
        entityData.set(ID_HEADSHOT, headshot.isEmpty() ? 0 : (byte) stack.getEnchantmentLevel(headshot.get()));

        entityData.set(ID_FOIL, stack.hasFoil());
        entityData.set(STACK, stack);

        ricochets = entityData.get(ID_RICOCHET);
        int phasTicks = entityData.get(ID_PHASING);

        if (phasTicks > 0) {
            phasingTicks = phasTicks * 5 + 5;
            isPhasing = true;
            if (pickup != Pickup.CREATIVE_ONLY) pickup = Pickup.DISALLOWED;
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
    public int phasingTicks() {
        return phasingTicks;
    }
    public boolean isPhasing() {
        return isPhasing;
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
        StuckHatchetHolder stuckHatchetHolder = LivingDataHelper.takeHolder(entity, StuckHatchetHolder.class);
        if (stuckHatchetHolder == null) return false;
        return stuckHatchetHolder.stuck(this);
    }
    public void ricochetIntoNowhere(FancyDirection fancyDirection, float multip) {
        ricocheted = true;
        Vec3 vec3 = this.getDeltaMovement();
        Vec3 newV3 = switch (fancyDirection) {
            case ONLY$X -> vec3.subtract(vec3.x() * 2,0,0).scale(multip);
            case ONLY$Y -> vec3.subtract(0,vec3.y() * 2,0).scale(multip);
            case ONLY$Z -> vec3.subtract(0,0,vec3.z() * 2).scale(multip);
            case X$Y -> vec3.subtract(vec3.x() * 2,vec3.y() * 2,0).scale(multip);
            case X$Z -> vec3.subtract(vec3.x() * 2,0,vec3.z() * 2).scale(multip);
            case Y$Z -> vec3.subtract(0,vec3.y() * 2,vec3.z() * 2).scale(multip);
            case XYZ -> vec3.subtract(vec3.x() * 2, vec3.y() * 2, vec3.z() * 2).scale(multip);
        };
        this.setDeltaMovement(newV3);
    }
    public boolean ricochetTarget(Predicate<LivingEntity> livingEntityPredicate) {
        SphereBox spherebox = new SphereBox(8, this.position());
        List<LivingEntity> entityList = spherebox.entitiesInside(level(), LivingEntity.class);
        entityList.removeIf(this::noLOS);
        entityList.removeIf(Predicate.not(livingEntityPredicate));
        entityList.removeIf(ArmorStand.class::isInstance);
        entityList.removeIf(entity1 -> entity1 == getOwner() || entity1.hurtTime > 0);
        if (entityList.isEmpty()) return false;
        int i = RandomUtil.nextInt(0, entityList.size());
        LivingEntity target = entityList.get(i);
        Vec3 head = target.getEyePosition();
        setDeltaMovement(chasing(head, 1f));
        return true;
    }
    public boolean noLOS(Entity pEntity) {
        if (pEntity.level() != this.level()) return true;
        else {
            Vec3 vec3 = new Vec3(this.getX(), this.getEyeY(), this.getZ());
            Vec3 vec31 = new Vec3(pEntity.getX(), pEntity.getEyeY(), pEntity.getZ());
            if (vec31.distanceTo(vec3) > 128.0D) return true;
            else return this.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
        }
    }
    public void ricochetIntoNowhere(float multiplier) {
        this.setDeltaMovement(this.getDeltaMovement().scale(multiplier));
    }
    public boolean isBlockDestroyable(BlockState state) {
        return (state.getBlock() instanceof StainedGlassPaneBlock || state.getBlock() == Blocks.GLASS_PANE || state.getBlock() instanceof LeavesBlock);
    }

    public FancyDirection direction(BlockPos pos) {
        BlockPos blockPos = blockPosition().subtract(pos);
        // reminder: x is for West and East, z is for North and South
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        int modX = Math.abs(x);
        int modY = Math.abs(y);
        int modZ = Math.abs(z);
        if (modX == modY && modY == modZ) return FancyDirection.XYZ;
        else {
            if (modX > modY && modX > modZ) return FancyDirection.ONLY$X;
            else if (modY > modZ && modY > modX) return FancyDirection.ONLY$Y;
            else if (modZ > modY && modZ > modX) return FancyDirection.ONLY$Z;
            else {
                BlockState blockStateX = level().getBlockState(blockPosition().subtract(new BlockPos(x,0,0)));
                BlockState blockStateY = level().getBlockState(blockPosition().subtract(new BlockPos(0, y,0)));
                BlockState blockStateZ = level().getBlockState(blockPosition().subtract(new BlockPos(0,0, z)));
                if (modX == modY) {
                    if (blockStateX.isAir() && !blockStateY.isAir()) return FancyDirection.ONLY$Y;
                    else if (!blockStateX.isAir() && blockStateY.isAir()) return FancyDirection.ONLY$X;
                    else return FancyDirection.X$Y;
                } else if (modY == modZ) {
                    if (blockStateY.isAir() && !blockStateZ.isAir()) return FancyDirection.ONLY$Z;
                    else if (!blockStateY.isAir() && blockStateZ.isAir()) return FancyDirection.ONLY$Y;
                    else return FancyDirection.Y$Z;
                } else {
                    if (blockStateX.isAir() && !blockStateZ.isAir()) return FancyDirection.ONLY$Z;
                    else if (!blockStateX.isAir() && blockStateZ.isAir()) return FancyDirection.ONLY$X;
                    else return FancyDirection.X$Z;
                }
            }
        }
    }

    public enum FancyDirection {
        ONLY$X, ONLY$Y, ONLY$Z, X$Y, X$Z, Y$Z, XYZ
    }
}
