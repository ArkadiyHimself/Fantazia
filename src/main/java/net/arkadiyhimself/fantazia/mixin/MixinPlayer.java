package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.DamageTypeRegistry;
import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.arkadiyhimself.fantazia.api.SoundRegistry;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.StaminaData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer {

    public float jumpSTcost() {
        return 0.55f;
    }
    Player player = (Player) (Object) this;
    @Inject(at = @At(value = "HEAD"), method = "getDigSpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F", cancellable = true, remap = false)
    protected void slowMiningFreeze(BlockState pState, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        Player player = (Player) (Object) this;
        if (player.hasEffect(MobEffectRegistry.FROZEN.get())) {
            cir.setReturnValue(cir.getReturnValueF() * 0.65F);
        }
    }
    @Inject(at = @At(value = "HEAD"), method = "jumpFromGround", cancellable = true)
    protected void jumpFromGround(CallbackInfo ci) {
        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null || player.isCreative()) return;
        abilityManager.getAbility(StaminaData.class).ifPresent(staminaData -> {
            boolean enough = staminaData.wasteStamina(jumpSTcost(), true);
            if (!enough) ci.cancel();
        });
    }
    @Inject(at = @At(value = "HEAD"), method = "getHurtSound", cancellable = true)
    private void hurtSound(DamageSource pDamageSource, CallbackInfoReturnable<SoundEvent> cir) {
        if (pDamageSource.is(DamageTypeRegistry.BLEEDING)) cir.setReturnValue(SoundRegistry.BLOODLOSS.get());
    }
}
