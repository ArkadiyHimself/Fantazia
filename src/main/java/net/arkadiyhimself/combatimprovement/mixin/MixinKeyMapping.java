package net.arkadiyhimself.combatimprovement.mixin;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public class MixinKeyMapping {
    @Inject(at = @At("HEAD"), method = "consumeClick", cancellable = true)
    private void cancelInput(CallbackInfoReturnable<Boolean> cir) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (WhereMagicHappens.Abilities.canNotDoActions(player) && !(Minecraft.getInstance().screen instanceof PauseScreen)) {
                cir.setReturnValue(false);
            }
        }
    }
    @Inject(at = @At("HEAD"), method = "isDown", cancellable = true)
    private void cancelHold(CallbackInfoReturnable<Boolean> cir) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (WhereMagicHappens.Abilities.canNotDoActions(player) && !(Minecraft.getInstance().screen instanceof PauseScreen)) {
                cir.setReturnValue(false);
            }
        }
    }
}
