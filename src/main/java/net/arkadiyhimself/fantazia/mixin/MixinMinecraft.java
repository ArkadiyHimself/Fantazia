package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.VibrationListen;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectManager;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.FuryEffect;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow @Nullable public LocalPlayer player;

    @Inject(at = @At("HEAD"), method = "startAttack", cancellable = true)
    private void cancelAttack(CallbackInfoReturnable<Boolean> cir) {
        if (ActionsHelper.preventActions(player)) cir.setReturnValue(false);
    }
    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void blockBreakCancel(boolean leftClick, CallbackInfo ci) {
        if (ActionsHelper.preventActions(player)) ci.cancel();
    }
    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void itemUseCancel(CallbackInfo ci) {
        if (ActionsHelper.preventActions(player)) ci.cancel();
    }
    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void keybindsCancel(CallbackInfo ci) {
        if (ActionsHelper.preventActions(player)) ci.cancel();
    }
    @Inject(at = @At("HEAD"), method = "shouldEntityAppearGlowing", cancellable = true)
    private void glowing(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        if (pEntity instanceof ThrownHatchet hatchet && hatchet.getOwner() == player) cir.setReturnValue(true);

        if (pEntity instanceof LivingEntity livingEntity) {
            EffectManager effectManager = EffectGetter.getUnwrap(livingEntity);
            if (effectManager != null) {
                FuryEffect furyEffect = effectManager.takeEffect(FuryEffect.class);
                if (furyEffect != null && furyEffect.isFurious() && player.hasLineOfSight(pEntity)) cir.setReturnValue(true);
            }
        }

        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null) return;
        VibrationListen vibrationListen = abilityManager.takeAbility(VibrationListen.class);
        if (vibrationListen != null && pEntity instanceof LivingEntity && vibrationListen.revealed().contains(pEntity)) cir.setReturnValue(true);
    }
}
