package net.arkadiyhimself.fantazia.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.arkadiyhimself.fantazia.common.advanced.cleanse.Cleanse;
import net.arkadiyhimself.fantazia.common.advanced.cleanse.EffectCleansing;
import net.arkadiyhimself.fantazia.common.advanced.rune.RuneHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.common.FantazicHooks;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.common.registries.custom.Runes;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.data.tags.FTZMobEffectTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow public abstract void remove(@NotNull RemovalReason reason);

    @Shadow @javax.annotation.Nullable public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);

    @Unique
    private @Nullable DamageSource fantazia$hurtSource = null;

    public MixinLivingEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Unique
    private final LivingEntity fantazia$entity = (LivingEntity) (Object) this;

    @Unique
    private boolean fantazia$wasClimbing = false;

    @Inject(at = @At(value = "HEAD"), method = "playHurtSound", cancellable = true)
    private void hurtSound(DamageSource source, CallbackInfo ci) {
        if (source == null) return;
        if (!source.is(FTZDamageTypeTags.PIERCES_BARRIER)) for (MobEffectInstance mobEffectInstance : fantazia$entity.getActiveEffects()) if (mobEffectInstance.getEffect().is(FTZMobEffectTags.BARRIER)) ci.cancel();
        if (source.is(FTZDamageTypeTags.NO_HURT_SOUND)) ci.cancel();
        if (source.is(FTZDamageTypes.BLEEDING) && (fantazia$entity.tickCount & 10) == 0) fantazia$entity.level().playSound(null, fantazia$entity.blockPosition(), FTZSoundEvents.EFFECT_HAEMORRHAGE_BLOODLOSS.get(), SoundSource.HOSTILE);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;makeSound(Lnet/minecraft/sounds/SoundEvent;)V"), method = "hurt")
    private void deathSound(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.fantazia$hurtSource = source;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;makeSound(Lnet/minecraft/sounds/SoundEvent;)V"), method = "hurt")
    private void deathSound(LivingEntity instance, SoundEvent sound) {
        if (fantazia$hurtSource == null || !fantazia$hurtSource.is(FTZDamageTypeTags.NON_LETHAL)) instance.makeSound(sound);
    }

    @Inject(at = @At(value = "HEAD"), method = "onItemPickup", cancellable = true)
    protected void pickUp(ItemEntity pItemEntity, CallbackInfo ci) {
        if (!FantazicHooks.ForgeExtension.onLivingPickUpItem(fantazia$entity, pItemEntity)) ci.cancel();
    }

    @Inject(at = @At(value = "HEAD"), method = "removeEffectsCuredBy", cancellable = true, remap = false)
    private void milkBucket(EffectCure cure, CallbackInfoReturnable<Boolean> cir) {
        EffectCleansing.tryCleanseAll(fantazia$entity, Cleanse.BASIC);
        cir.setReturnValue(false);
    }

    @Inject(at = @At("HEAD"), method = "doHurtTarget", cancellable = true)
    private void cancelAttack(Entity pTarget, CallbackInfoReturnable<Boolean> cir) {
        if (fantazia$entity.hasEffect(FTZMobEffects.DISARM)) cir.setReturnValue(false);
    }

    @Inject(at = @At("HEAD"), method = "getAttackAnim", cancellable = true)
    private void attackAnim(float pPartialTick, CallbackInfoReturnable<Float> cir) {
        if (LivingEffectHelper.hasEffectSimple(fantazia$entity, FTZMobEffects.DISARM.value())) cir.setReturnValue(0f);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;setSpeed(F)V", args = {"damagesource"}), method = "handleDamageEvent")
    private void walkAnimation(WalkAnimationState instance, float speed, @Local(argsOnly = true) DamageSource damageSourceLocalRef) {
        if (!damageSourceLocalRef.is(FTZDamageTypes.REMOVAL)) instance.setSpeed(speed);
    }

    @Inject(at = @At("RETURN"), method = "getCurrentSwingDuration", cancellable = true)
    private void swingDuration(CallbackInfoReturnable<Integer> cir) {
        Integer value = cir.getReturnValue();
        int delta = 0;
        MobEffectInstance instance = getEffect(FTZMobEffects.FROZEN);
        if (instance != null) delta = Math.min(12, instance.getAmplifier() * 2);
        cir.setReturnValue(value + delta);
    }

    @Inject(at = @At(value = "HEAD"), method = "onClimbable", cancellable = true)
    private void climbWall(CallbackInfoReturnable<Boolean> cir) {
        if (fantazia$entity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing()) return;
        }
        if (fantazia$shouldBeClimbing()) cir.setReturnValue(true);
    }

    @Unique
    private boolean fantazia$shouldBeClimbing() {
        AABB bb = fantazia$entity.getBoundingBox().inflate(0.2,0,0.2);
        int mX = Mth.floor(bb.minX);
        int mY = Mth.floor(bb.minY);
        int mZ = Mth.floor(bb.minZ);
        if (fantazia$fitForCobwebClimbing(fantazia$entity)) return true;
        if (horizontalCollision && fantazia$entity.getData(FTZAttachmentTypes.WALL_CLIMBING_UNLOCKED)) {
            fantazia$wasClimbing = true;
            return true;
        } else if (fantazia$wasClimbing) {
            for (int y = mY; y <= bb.maxY - 1; ++y)
                for (int x = mX; x <= bb.maxX; ++x)
                    for (int z = mZ; z <= bb.maxZ; ++z)
                        if (fantazia$fitForClimbing(new BlockPos(x, y, z), bb)) return true;
        }
        fantazia$wasClimbing = false;
        return false;
    }

    @Unique
    private boolean fantazia$fitForClimbing(BlockPos pos, AABB aabb) {
        if (fantazia$entity instanceof Player player && FantazicCombat.isPhasing(player)) return false;
        Vector3f vec3 = fantazia$entity.position().toVector3f();
        BlockPos blockPos = new BlockPos(Math.round(vec3.x), Math.round(vec3.x), Math.round(vec3.x));
        if (pos.getX() == blockPos.getX() && pos.getZ() == blockPos.getZ() || isSupportedBy(pos)) return false;
        BlockState state = level().getBlockState(pos);

        return state.isSolid() && fantazia$entity.getData(FTZAttachmentTypes.WALL_CLIMBING_UNLOCKED);
    }

    @Unique
    private boolean fantazia$fitForCobwebClimbing(Entity entity) {
        if (!entity.getData(FTZAttachmentTypes.WALL_CLIMBING_COBWEB)) return false;
        AABB aabb = entity.getBoundingBox();
        BlockPos corner1 = BlockPos.containing(aabb.minX + 1.0E-7, aabb.minY + 1.0E-7, aabb.minZ + 1.0E-7);
        BlockPos corner2 = BlockPos.containing(aabb.maxX - 1.0E-7, aabb.maxY - 1.0E-7, aabb.maxZ - 1.0E-7);

        for(int i = corner1.getX(); i <= corner2.getX(); ++i)
            for(int j = corner1.getY(); j <= corner2.getY(); ++j)
                for(int k = corner1.getZ(); k <= corner2.getZ(); ++k)
                    if (level().getBlockState(new BlockPos(i, j, k)).is(Blocks.COBWEB)) return true;

        return false;
    }

    @Override
    public boolean dampensVibrations() {
        if (RuneHelper.hasRune(fantazia$entity, Runes.NOISELESS)) return true;
        return super.dampensVibrations();
    }
}
