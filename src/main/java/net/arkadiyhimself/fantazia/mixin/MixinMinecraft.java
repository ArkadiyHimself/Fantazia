package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.FuryEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.VibrationListenerHolder;
import net.arkadiyhimself.fantazia.entities.DashStoneEntity;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.events.ClientEvents;
import net.arkadiyhimself.fantazia.registries.custom.FTZAuras;
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
    private void keyBindsCancel(CallbackInfo ci) {
        if (ActionsHelper.preventActions(player)) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "shouldEntityAppearGlowing", cancellable = true)
    private void glowing(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        if (pEntity instanceof ThrownHatchet hatchet && hatchet.getOwner() == player) cir.setReturnValue(true);
        else if (pEntity instanceof LivingEntity livingEntity) {
            FuryEffect furyEffect = LivingEffectGetter.takeHolder(livingEntity, FuryEffect.class);
            if (furyEffect != null && furyEffect.isFurious() && player.hasLineOfSight(pEntity)) cir.setReturnValue(true);

            VibrationListenerHolder vibrationListenerHolder = PlayerAbilityGetter.takeHolder(player, VibrationListenerHolder.class);
            if (vibrationListenerHolder != null && vibrationListenerHolder.revealed().contains(livingEntity)) cir.setReturnValue(true);

            if (pEntity == ClientEvents.suitableTarget) cir.setReturnValue(true);
        } else if (pEntity instanceof DashStoneEntity) cir.setReturnValue(true);
        AuraInstance<? extends Entity> uncover = AuraHelper.ownedAuraInstance(player, FTZAuras.UNCOVER.value());
        if (uncover != null && uncover.isInside(pEntity)) cir.setReturnValue(true);
    }
}
