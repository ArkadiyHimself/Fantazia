package net.arkadiyhimself.combatimprovement.mixin;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents.NewEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MixinMob {
    Mob mob = (Mob) (Object) this;
    @Inject(at = @At(value = "HEAD"), method = "doHurtTarget", cancellable = true)
    protected void meleeAttack(Entity pTarget, CallbackInfoReturnable<Boolean> cir) {
        boolean attack = NewEvents.ForgeExtenstion.onMobAttack(mob, pTarget);
        if (!attack) { cir.setReturnValue(false); }
    }
}
