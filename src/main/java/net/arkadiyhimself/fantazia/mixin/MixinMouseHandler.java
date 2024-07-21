package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.events.WhereMagicHappens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {
    @Inject(at = @At("HEAD"), method = "turnPlayer", cancellable = true)
    private void cancelTurn(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (WhereMagicHappens.Abilities.cancelMouseMoving(player)) ci.cancel();
    }
}
