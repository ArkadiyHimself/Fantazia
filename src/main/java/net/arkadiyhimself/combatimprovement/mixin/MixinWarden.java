package net.arkadiyhimself.combatimprovement.mixin;

import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public class MixinWarden {
    @Inject(at = @At("HEAD"), method = "shouldListen", cancellable = true)
    private void setFalse(ServerLevel pLevel, GameEventListener pListener, BlockPos pPos, GameEvent pGameEvent, GameEvent.Context pContext, CallbackInfoReturnable<Boolean> cir) {
        Warden warden = (Warden) (Object) this;
        if (warden.hasEffect(MobEffectRegistry.DEAFENING.get())) { cir.setReturnValue(false); }
    }
}
