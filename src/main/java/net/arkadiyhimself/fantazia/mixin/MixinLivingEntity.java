package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.DisarmEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.HaemorrhageEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.data.talents.TalentHelper;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.EffectCure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    public MixinLivingEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Unique
    private final LivingEntity neoForgeFantazia$entity = (LivingEntity) (Object) this;

    @Inject(at = @At(value = "HEAD"), method = "playHurtSound", cancellable = true)
    protected void cancelSound(DamageSource pSource, CallbackInfo ci) {
        if (pSource == null) return;
        if (pSource.is(FTZDamageTypeTags.NO_HURT_SOUND)) ci.cancel();
        if (pSource.is(FTZDamageTypes.BLEEDING)) LivingEffectGetter.acceptConsumer(neoForgeFantazia$entity, HaemorrhageEffect.class, HaemorrhageEffect::tryMakeSound);
        Collection<MobEffectInstance> instanceCollection = neoForgeFantazia$entity.getActiveEffects();
        for (MobEffectInstance instance : instanceCollection) if (FTZMobEffectTags.hasTag(instance.getEffect(), FTZMobEffectTags.BARRIER)) ci.cancel();
    }
    @Inject(at = @At(value = "HEAD"), method = "onItemPickup", cancellable = true)
    protected void pickUp(ItemEntity pItemEntity, CallbackInfo ci) {
        if (!FTZEvents.ForgeExtension.onLivingPickUpItem(neoForgeFantazia$entity, pItemEntity)) ci.cancel();
    }
    @Inject(at = @At(value = "HEAD"), method = "removeEffectsCuredBy", cancellable = true, remap = false)
    private void milkBucket(EffectCure cure, CallbackInfoReturnable<Boolean> cir) {
        EffectCleansing.tryCleanseAll(neoForgeFantazia$entity, Cleanse.BASIC);
        cir.setReturnValue(false);
    }
    @Inject(at = @At("HEAD"), method = "doHurtTarget", cancellable = true)
    private void cancelAttack(Entity pTarget, CallbackInfoReturnable<Boolean> cir) {
        if (neoForgeFantazia$entity.hasEffect(FTZMobEffects.DISARM)) cir.setReturnValue(false);
    }
    @Inject(at = @At("HEAD"), method = "getAttackAnim", cancellable = true)
    private void attackAnim(float pPartialTick, CallbackInfoReturnable<Float> cir) {
        DisarmEffect disarmEffect = LivingEffectGetter.takeHolder(neoForgeFantazia$entity, DisarmEffect.class);
        if (disarmEffect != null && disarmEffect.renderDisarm()) cir.setReturnValue(0f);
    }
    @Inject(at = @At(value = "HEAD"), method = "onClimbable", cancellable = true)
    private void climbWall(CallbackInfoReturnable<Boolean> cir) {
        if (!(neoForgeFantazia$entity instanceof Player player)) return;
        DashHolder dashHolder = PlayerAbilityGetter.takeHolder(player, DashHolder.class);
        if (dashHolder != null && dashHolder.isDashing()) return;
        if (TalentHelper.hasTalent(player, Fantazia.res("wall_climbing")) && neoForgeFantazia$horizontalCollision()) cir.setReturnValue(true);
    }
    @Unique
    private boolean neoForgeFantazia$horizontalCollision() {
        AABB bb = neoForgeFantazia$entity.getBoundingBox().inflate(0.2,0,0.2);
        int mX = Mth.floor(bb.minX);
          int mY = Mth.floor(bb.minY);
        int mZ = Mth.floor(bb.minZ);
        for (int y2 = mY; y2 < bb.maxY; y2++) for (int x2 = mX; x2 < bb.maxX; x2++) for (int z2 = mZ; z2 < bb.maxZ; z2++) if (level().getBlockState(new BlockPos(x2, y2, z2)).isSolid()) return true;
        return false;
    }
}
