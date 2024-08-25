package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.DisarmEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.HaemorrhageEffect;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
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
    private final LivingEntity entity = (LivingEntity) (Object) this;
    @Inject(at = @At(value = "HEAD"), method = "playHurtSound", cancellable = true)
    protected void cancelSound(DamageSource pSource, CallbackInfo ci) {
        if (pSource == null) return;
        if (pSource.is(FTZDamageTypeTags.NO_HURT_SOUND)) ci.cancel();
        if (pSource.is(FTZDamageTypes.BLEEDING)) EffectGetter.effectConsumer(entity, HaemorrhageEffect.class, HaemorrhageEffect::tryMakeSound);
        Collection<MobEffectInstance> instanceCollection = entity.getActiveEffects();
        for (MobEffectInstance instance : instanceCollection) if (FTZMobEffectTags.hasTag(instance.getEffect(), FTZMobEffectTags.BARRIER)) ci.cancel();
    }
    @Inject(at = @At(value = "HEAD"), method = "onItemPickup", cancellable = true)
    protected void pickUp(ItemEntity pItemEntity, CallbackInfo ci) {
        if (!FTZEvents.ForgeExtension.onLivingPickUpItem(entity, pItemEntity)) ci.cancel();
    }
    @Inject(at = @At(value = "HEAD"), method = "curePotionEffects", cancellable = true, remap = false)
    private void milkBucket(ItemStack curativeItem, CallbackInfoReturnable<Boolean> cir) {
        EffectCleansing.tryCleanseAll(entity, Cleanse.BASIC);
        cir.setReturnValue(false);
    }
    @Inject(at = @At("HEAD"), method = "doHurtTarget", cancellable = true)
    private void cancelAttack(Entity pTarget, CallbackInfoReturnable<Boolean> cir) {
        if (entity.hasEffect(FTZMobEffects.DISARM.get())) cir.setReturnValue(false);
    }
    @Inject(at = @At("HEAD"), method = "getAttackAnim", cancellable = true)
    private void attackAnim(float pPartialTick, CallbackInfoReturnable<Float> cir) {
        DisarmEffect disarmEffect = EffectGetter.takeEffectHolder(entity, DisarmEffect.class);
        if (disarmEffect != null && disarmEffect.renderDisarm()) cir.setReturnValue(0f);
    }
    @Inject(at = @At(value = "HEAD"), method = "onClimbable", cancellable = true)
    private void climbWall(CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof Player player)) return;
        Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
        if (dash != null && dash.isDashing()) return;
        if (TalentHelper.hasTalent(player, Fantazia.res("wall_climbing")) && horizontalCollision()) cir.setReturnValue(true);
    }
    private boolean horizontalCollision() {
        AABB bb = entity.getBoundingBox().inflate(0.2,0,0.2);
        int mX = Mth.floor(bb.minX);
        int mY = Mth.floor(bb.minY);
        int mZ = Mth.floor(bb.minZ);
        for (int y2 = mY; y2 < bb.maxY; y2++) for (int x2 = mX; x2 < bb.maxX; x2++) for (int z2 = mZ; z2 < bb.maxZ; z2++) if (!level().getBlockState(new BlockPos(x2, y2, z2)).isAir()) return true;
        return false;
    }
}
