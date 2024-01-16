package net.arkadiyhimself.combatimprovement.mixin;

import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.minecraft.client.player.LocalPlayer;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {
    LocalPlayer player = (LocalPlayer) (Object) this;
    @Inject(at = @At("HEAD"), method = "hasEnoughFoodToStartSprinting", cancellable = true)
    private void sprinting(CallbackInfoReturnable<Boolean> cir) {
        AttachDataSync.get(player).ifPresent(dataSync -> {
            if (dataSync.stamina <= 0.1f) {
                cir.setReturnValue(false);
            }
        });
    }
}
