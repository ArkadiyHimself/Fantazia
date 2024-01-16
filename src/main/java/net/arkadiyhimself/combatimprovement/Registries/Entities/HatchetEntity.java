package net.arkadiyhimself.combatimprovement.Registries.Entities;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Registries.Items.Weapons.Mixed.Hatchet;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.client.Render.Models.Entity.NewEntitites.Hatchet.HatchetRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class HatchetEntity extends AbstractArrow implements GeoEntity {
    public static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(HatchetEntity.class,
            EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(HatchetEntity.class,
            EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(HatchetEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private final AnimatableInstanceCache animatableInstanceCache = new SingletonAnimatableInstanceCache(this);
    public ItemStack hatchetItem = ItemStack.EMPTY;
    private boolean dealtDamage;
    public HatchetEntity(EntityType<? extends HatchetEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public HatchetEntity(Level pLevel, LivingEntity pShooter, ItemStack pStack) {
        super(EntityTypeRegistry.HATCHET.get(), pShooter, pLevel);
        this.hatchetItem = pStack;
        this.entityData.set(YAW, pShooter.getXRot());
        this.entityData.set(PITCH, pShooter.getYRot());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(YAW, 0f);
        entityData.define(PITCH, 0f);
        entityData.define(STACK, hatchetItem);

    }

    @Override
    protected ItemStack getPickupItem() {
        return entityData.get(STACK);
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
            animationState.getController().stop();
            return PlayState.STOP;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        this.turn(10,10);
    }
    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        DamageSource source = new IndirectEntityDamageSource("hatchet", this, this.getOwner() == null ? this : this.getOwner());
        this.dealtDamage = true;
        if (pResult.getEntity() instanceof LivingEntity livingEntity) {
            float dmg = 1.5f;
            if (getPickupItem() != null) {
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
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("yaw", entityData.get(YAW));
        tag.putFloat("pitch", entityData.get(PITCH));
        if (getPickupItem() != null && !getPickupItem().isEmpty()) {
            tag.put("item", getPickupItem().getOrCreateTag());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        setItemStack(ItemStack.of(nbt.getCompound("item")));
        entityData.set(YAW, nbt.getFloat("yaw"));
        entityData.set(PITCH, nbt.getFloat("pitch"));
    }
    public void setItemStack(ItemStack stack) {
        entityData.set(STACK, stack);
    }
    public ResourceLocation getTextureLocation() {
        if (entityData.get(STACK).getItem() instanceof Hatchet hatchet) {
            Tier tier = hatchet.getTier();
            ResourceLocation res;
            if (tier == Tiers.STONE) {
                res = HatchetRenderer.STONE;
            } else if (tier == Tiers.IRON) {
                res = HatchetRenderer.IRON;
            } else if (tier == Tiers.GOLD) {
                res = HatchetRenderer.GOLD;
            } else if (tier == Tiers.DIAMOND) {
                res = HatchetRenderer.DIAMOND;
            } else if (tier == Tiers.NETHERITE) {
                res = HatchetRenderer.NETHERITE;
            } else {
                return HatchetRenderer.WOODEN;
            }
            return res;
        }
        return HatchetRenderer.WOODEN;
    }
}
