package net.arkadiyhimself.combatimprovement.mixin;

import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Blocking.AttachBlocking;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.StunEffect.StunEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {
    @Inject(at = @At("HEAD"), method = "turnPlayer", cancellable = true)
    private void cancelTurn(CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null && AttachDash.getUnwrap(player) != null && AttachBlocking.getUnwrap(player) != null) {
            if (AttachDash.getUnwrap(player).isDashing() || AttachBlocking.getUnwrap(player).isInAnim()) {
                ci.cancel();
                player.setXRot(0);
            }
        }
        if (player != null && StunEffect.getUnwrap(player) != null) {
            if (StunEffect.getUnwrap(player).isStunned()) {
                ci.cancel();
            }
        }
    }
}
