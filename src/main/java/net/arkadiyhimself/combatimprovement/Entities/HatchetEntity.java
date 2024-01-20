package net.arkadiyhimself.combatimprovement.Entities;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.api.DamageTypeRegistry;
import net.arkadiyhimself.combatimprovement.api.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.api.EntityTypeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class HatchetEntity extends AbstractArrow {
    public DamageSource hatchet;
    public static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(HatchetEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private ItemStack hatchetItem;
    public HatchetEntity(EntityType<? extends HatchetEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public HatchetEntity(Level level, LivingEntity shooter, ItemStack stack) {
        super(EntityTypeRegistry.HATCHET.get(), level);
        this.setPos(shooter.getX(), shooter.getEyeY() - (double)0.1F, shooter.getZ());
        this.setXRot(shooter.getXRot() + 90);
        this.inGround = false;
        this.entityData.set(STACK, stack);
        this.hatchetItem = stack.copy();
        this.hatchet = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.HATCHET), this, this.getOwner() == null ? this : this.getOwner());
    }
    public HatchetEntity(Level level, Vec3 vec3, ItemStack stack) {
        super(EntityTypeRegistry.HATCHET.get(), level);
        this.setPos(vec3);
        this.entityData.set(STACK, stack);
        this.hatchetItem = stack.copy();
    }
    public @NotNull ItemStack getPickupItem() {
        return this.hatchetItem;
    }
    @Override
    public void tick() {
        if (!this.inGround) {

        }
        if (!this.level().isClientSide) {
            this.setXRot(this.getXRot() - 10f);
        }
        super.tick();
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(STACK, ItemStack.EMPTY);
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        if (this.level().isClientSide()) return;
        if (pResult.getEntity() instanceof LivingEntity livingEntity) {
            float dmg = 1.5f;
            dmg += getPickupItem().getItem().getDamage(getPickupItem());
              boolean flag = livingEntity.hurt(this.hatchet, dmg);
              if (flag) {
                  WhereMagicHappens.Abilities.addEffectWithoutParticles(livingEntity, MobEffectRegistry.STUN.get(), 40);
                  getStuck(livingEntity);
              }
        }
        this.setYRot(this.getYRot() + 180);
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
    }
    public void getStuck(LivingEntity entity) {
        WhereMagicHappens.Abilities.hatchetStuck.put(entity, this.getPickupItem());
        this.discard();
    }
}
