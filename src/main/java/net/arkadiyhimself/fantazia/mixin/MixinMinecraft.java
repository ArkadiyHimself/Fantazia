package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.Entities.HatchetEntity;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.RenderingValues;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
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
        if (WhereMagicHappens.Abilities.canNotDoActions(player)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void blockBreakCancel(boolean leftClick, CallbackInfo ci) {
        if (WhereMagicHappens.Abilities.canNotDoActions(player)) {
            ci.cancel();
        }
    }
    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void itemUseCancel(CallbackInfo ci) {
        if (WhereMagicHappens.Abilities.canNotDoActions(player)) {
            ci.cancel();
        }
    }
    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void keybindsCancel(CallbackInfo ci) {
        if (WhereMagicHappens.Abilities.canNotDoActions(player)) {
            ci.cancel();
        }
    }
    @Inject(at = @At("HEAD"), method = "shouldEntityAppearGlowing", cancellable = true)
    private void glowing(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        if (pEntity instanceof HatchetEntity hatchet && hatchet.getOwner() == player) {
            cir.setReturnValue(true);
        }

        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null) return;
        RenderingValues renderingValues = abilityManager.takeAbility(RenderingValues.class);
        if (renderingValues != null && pEntity instanceof LivingEntity && renderingValues.emittedSound().contains(pEntity)) cir.setReturnValue(true);


    }
}
