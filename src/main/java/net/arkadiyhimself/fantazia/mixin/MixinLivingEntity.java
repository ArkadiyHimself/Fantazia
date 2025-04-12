package net.arkadiyhimself.fantazia.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.DisarmEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.data.talent.TalentHelper;
import net.arkadiyhimself.fantazia.events.FantazicHooks;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.common.EffectCure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    public MixinLivingEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Unique
    private final LivingEntity fantazia$entity = (LivingEntity) (Object) this;

    @Inject(at = @At(value = "HEAD"), method = "playHurtSound", cancellable = true)
    private void hurtSound(DamageSource source, CallbackInfo ci) {
        if (source == null) return;
        for (MobEffectInstance mobEffectInstance : fantazia$entity.getActiveEffects()) if (FTZMobEffectTags.hasTag(mobEffectInstance.getEffect(), FTZMobEffectTags.BARRIER)) ci.cancel();
        if (source.is(FTZDamageTypeTags.NO_HURT_SOUND)) ci.cancel();
        if (source.is(FTZDamageTypes.BLEEDING) && (fantazia$entity.tickCount & 10) == 0) fantazia$entity.level().playSound(null, fantazia$entity.blockPosition(), FTZSoundEvents.EFFECT_HAEMORRHAGE_BLOODLOSS.get(), SoundSource.HOSTILE);
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
        DisarmEffect disarmEffect = LivingEffectGetter.takeHolder(fantazia$entity, DisarmEffect.class);
        if (disarmEffect != null && disarmEffect.renderDisarm()) cir.setReturnValue(0f);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;setSpeed(F)V", args = {"damagesource"}), method = "handleDamageEvent")
    private void walkAnimation(WalkAnimationState instance, float speed, @Local(argsOnly = true) DamageSource damageSourceLocalRef) {
        if (!damageSourceLocalRef.is(FTZDamageTypes.REMOVAL)) instance.setSpeed(speed);
    }

    @Inject(at = @At(value = "HEAD"), method = "onClimbable", cancellable = true)
    private void climbWall(CallbackInfoReturnable<Boolean> cir) {
        if (!(fantazia$entity instanceof Player player)) return;
        DashHolder dashHolder = PlayerAbilityGetter.takeHolder(player, DashHolder.class);
        if (dashHolder != null && dashHolder.isDashing()) return;
        if (fantazia$shouldBeClimbing(player)) cir.setReturnValue(true);
    }

    @Unique
    private boolean fantazia$shouldBeClimbing(Player player) {
        AABB bb = fantazia$entity.getBoundingBox().inflate(0.2,0,0.2);
        int mX = Mth.floor(bb.minX);
        int mY = Mth.floor(bb.minY);
        int mZ = Mth.floor(bb.minZ);
        for (int y = mY; y < bb.maxY - 1; y++) for (int x = mX; x < bb.maxX; x++) for (int z = mZ; z < bb.maxZ; z++) {
            BlockPos blockPos = new BlockPos(x, y, z);

            if (fantazia$fitForClimbing(blockPos, player)) return true;
        }
        return false;
    }

    @Unique
    private boolean fantazia$fitForClimbing(BlockPos pos, Player player) {
        if (FantazicCombat.isPhasing(player)) return false;
        if (pos.getX() == player.getBlockX() && pos.getZ() == player.getBlockZ()) return false;
        BlockState state = level().getBlockState(pos);

        Block block = state.getBlock();
        boolean fullBlock = state.getShape(level(), pos) == Shapes.block();
        if (block instanceof BedBlock) return false;
        if (block instanceof SlabBlock && !fullBlock && pos.getY() - player.getBlockY() <= 0) return false;
        if (block instanceof StairBlock && pos.getY() - player.getBlockY() <= 0) return false;

        if (state.isSolid() && TalentHelper.hasTalent(player, Fantazia.res("spider_powers/wall_climbing"))) return true;
        else return (state.is(Blocks.COBWEB) && TalentHelper.hasTalent(player, Fantazia.res("spider_powers/cobweb_climbing")));
    }

}
