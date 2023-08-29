package net.arkadiyhimself.combatimprovement.mixin;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.UsefulMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
        if (UsefulMethods.Abilities.canNotDoActions(player)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void blockBreakCancel(boolean leftClick, CallbackInfo ci) {
        if (UsefulMethods.Abilities.canNotDoActions(player)) {
            ci.cancel();
        }
    }
    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void itemUseCancel(CallbackInfo ci) {
        if (UsefulMethods.Abilities.canNotDoActions(player)) {
            ci.cancel();
        }
    }
    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void keybindsCancel(CallbackInfo ci) {
        if (UsefulMethods.Abilities.canNotDoActions(player)) {
            ci.cancel();
        }
    }
}
