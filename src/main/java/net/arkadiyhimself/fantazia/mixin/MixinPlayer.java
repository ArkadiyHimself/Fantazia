package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.StaminaHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
    @Unique
    final Player neoForgeFantazia$player = (Player) (Object) this;

    @Shadow public abstract void tick();
    public MixinPlayer(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At(value = "HEAD"), method = "jumpFromGround", cancellable = true)
    protected void jumpFromGround(CallbackInfo ci) {
        StaminaHolder staminaHolder = PlayerAbilityHelper.takeHolder(neoForgeFantazia$player, StaminaHolder.class);
        if (staminaHolder != null && !staminaHolder.wasteStamina(0.35f, true)) ci.cancel();
    }

    @Inject(at = @At(value = "HEAD"), method = "getHurtSound", cancellable = true)
    private void hurtSound(DamageSource pDamageSource, CallbackInfoReturnable<SoundEvent> cir) {
        if (pDamageSource.is(FTZDamageTypeTags.NO_HURT_SOUND)) cir.setReturnValue(null);
        if (pDamageSource.is(FTZDamageTypes.BLEEDING)) cir.setReturnValue((neoForgeFantazia$player.tickCount & 15) == 0 ? FTZSoundEvents.EFFECT_HAEMORRHAGE_BLOODLOSS.get() : null);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z", shift = At.Shift.AFTER), method = "tick")
    private void phasing(CallbackInfo ci) {
        if (FantazicCombat.isPhasing(this)) noPhysics = true;
    }
}
