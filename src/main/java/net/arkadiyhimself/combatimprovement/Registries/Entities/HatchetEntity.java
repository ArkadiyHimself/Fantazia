package net.arkadiyhimself.combatimprovement.Registries.Entities;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class HatchetEntity extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache animatableInstanceCache = new SingletonAnimatableInstanceCache(this);
    public ItemStack hatchetItem = ItemStack.EMPTY;
    private boolean dealtDamage;
    public HatchetEntity(EntityType<? extends HatchetEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public HatchetEntity(Level pLevel, LivingEntity pShooter, ItemStack pStack) {
        super(EntityTypeRegistry.HATCHET.get(), pShooter, pLevel);
        this.hatchetItem = pStack;
        this.setYRot((pShooter.getYRot()));
        this.setXRot((pShooter.getXRot()));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }
    @Override
    protected ItemStack getPickupItem() {
        return hatchetItem;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }
    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (!this.inGround) {
            animationState.getController().setAnimation(RawAnimation.begin().then("rotate", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else {
            animationState.getController().setAnimation(null);
            return PlayState.CONTINUE;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
    }
    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        DamageSource source = new IndirectEntityDamageSource("hatchet", this, this.getOwner() == null ? this : this.getOwner());
        this.dealtDamage = true;
        if (pResult.getEntity() instanceof LivingEntity livingEntity) {
            float dmg = 1.5f;
            if (this.hatchetItem != null) {
                dmg += hatchetItem.getItem().getDamage(hatchetItem);
            }
            livingEntity.hurt(source, dmg);
            WhereMagicHappens.Abilities.addEffectWithoutParticles(livingEntity, MobEffectRegistry.STUN.get(), 40);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
    }
}
