package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.StaminaData;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {
    LocalPlayer player = (LocalPlayer) (Object) this;
    @Inject(at = @At("HEAD"), method = "hasEnoughFoodToStartSprinting", cancellable = true)
    private void sprinting(CallbackInfoReturnable<Boolean> cir) {
        StaminaData staminaData = AbilityGetter.takeAbilityHolder(player, StaminaData.class);
        if (staminaData != null && staminaData.getStamina() <= 0.1f) cir.setReturnValue(false);
    }
}
