package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.StaminaData;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
    public float jumpStamina() {
        return 0.35f;
    }
    Player player = (Player) (Object) this;
    @Shadow public abstract void tick();
    public MixinPlayer(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(at = @At(value = "HEAD"), method = "getDigSpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F", cancellable = true, remap = false)
    protected void slowMiningFreeze(BlockState pState, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (player.hasEffect(FTZMobEffects.FROZEN.get())) cir.setReturnValue(cir.getReturnValueF() * 0.65F);
    }
    @Inject(at = @At(value = "HEAD"), method = "jumpFromGround", cancellable = true)
    protected void jumpFromGround(CallbackInfo ci) {
        if (player.getAbilities().instabuild) return;

        StaminaData staminaData = AbilityGetter.takeAbilityHolder(player, StaminaData.class);
        if (staminaData != null && !staminaData.wasteStamina(jumpStamina(), true)) ci.cancel();
    }
    @Inject(at = @At(value = "HEAD"), method = "getHurtSound", cancellable = true)
    private void hurtSound(DamageSource pDamageSource, CallbackInfoReturnable<SoundEvent> cir) {
        if (pDamageSource.is(FTZDamageTypes.BLEEDING)) cir.setReturnValue(FTZSoundEvents.BLOODLOSS.get());
        if (pDamageSource.is(FTZDamageTypeTags.NO_HURT_SOUND)) cir.setReturnValue(null);
    }
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z", shift = At.Shift.AFTER), method = "tick")
    private void phasing(CallbackInfo ci) {
        if (FantazicCombat.isPhasing(this)) noPhysics = true;
    }
}
