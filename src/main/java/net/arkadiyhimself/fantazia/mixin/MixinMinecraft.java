package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.client.ClientEvents;
import net.arkadiyhimself.fantazia.common.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.common.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.VibrationListenerHolder;
import net.arkadiyhimself.fantazia.common.entity.DashStone;
import net.arkadiyhimself.fantazia.common.entity.ThrownHatchet;
import net.arkadiyhimself.fantazia.common.entity.magic_projectile.AbstractMagicProjectile;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.common.registries.custom.Auras;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
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

    @Shadow @Nullable public ClientLevel level;

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
        if (player == null || pEntity == player) return;
        boolean lineOfSight = player.hasLineOfSight(pEntity);
        if (pEntity instanceof ThrownHatchet hatchet && hatchet.getOwner() == player) cir.setReturnValue(true);
        if (pEntity instanceof AbstractMagicProjectile projectile && projectile.getOwnerClient() == player) cir.setReturnValue(true);
        else if (pEntity instanceof LivingEntity livingEntity) {
            if (LivingEffectHelper.hasEffect(livingEntity, FTZMobEffects.FURY.value()) && lineOfSight) cir.setReturnValue(true);

            VibrationListenerHolder vibrationListenerHolder = PlayerAbilityHelper.takeHolder(player, VibrationListenerHolder.class);
            if (vibrationListenerHolder != null && vibrationListenerHolder.isRevealed(livingEntity)) cir.setReturnValue(true);

            if (pEntity == ClientEvents.suitableTarget && Screen.hasShiftDown()) cir.setReturnValue(true);
        } else if (pEntity instanceof ExperienceOrb && FantazicUtil.holdsDataComponent(player, FTZDataComponentTypes.WISDOM_TRANSFER.value())) {
            if (pEntity.distanceTo(player) <= 20) cir.setReturnValue(true);
        } else if (pEntity instanceof Arrow arrow) {
            if (arrow.getData(FTZAttachmentTypes.HAS_FURY) && lineOfSight) cir.setReturnValue(true);
        }
        AuraInstance uncover = AuraHelper.ownedAuraInstance(player, Auras.UNCOVER);
        if (uncover != null && uncover.isInside(pEntity) && uncover.matchesFilter(pEntity)) cir.setReturnValue(true);

        DashHolder holder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
        if (holder != null) {
            DashStone dashStone = holder.getDashstoneEntity(level);
            if (pEntity == dashStone) cir.setReturnValue(true);
            if (dashStone != null && dashStone.isProtectorClient(pEntity)) cir.setReturnValue(true);
        }
    }
}
