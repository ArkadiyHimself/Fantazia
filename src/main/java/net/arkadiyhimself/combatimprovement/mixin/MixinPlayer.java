package net.arkadiyhimself.combatimprovement.mixin;

import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class MixinPlayer {
    public float jumpSTcost() {
        return 0.55f;
    }
    Player player = (Player) (Object) this;
    @Inject(at = @At(value = "HEAD"), method = "getDigSpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F", cancellable = true, remap = false)
    protected void slowMiningFreeze(BlockState pState, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        Player player = (Player) (Object) this;
        if (player.hasEffect(MobEffectRegistry.FROZEN.get())) {
            cir.setReturnValue(cir.getReturnValueF() * 0.175F);
        }
    }
    @Inject(at = @At(value = "HEAD"), method = "jumpFromGround", cancellable = true)
    protected void jumpFromGround(CallbackInfo ci) {
        AttachDataSync.get(player).ifPresent(dataSync -> {
            if (!player.isCreative()) {
                if (jumpSTcost() > dataSync.stamina) {
                    ci.cancel();
                } else {
                    dataSync.wasteStamina(jumpSTcost(), true);
                }
            }
        });
    }
}
