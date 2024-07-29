package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MixinMob {
    Mob mob = (Mob) (Object) this;
    @Inject(at = @At(value = "HEAD"), method = "doHurtTarget", cancellable = true)
    protected void meleeAttack(Entity pTarget, CallbackInfoReturnable<Boolean> cir) {
        boolean attack = FTZEvents.ForgeExtenstion.onMobAttack(mob, pTarget);
        if (!attack) cir.setReturnValue(false);
    }
    @Inject(at = @At(value = "HEAD"), method = "pickUpItem", cancellable = true)
    protected void pickUp(ItemEntity pItemEntity, CallbackInfo ci) {
        boolean pickup = FTZEvents.ForgeExtenstion.onLivingPickUpItem(mob, pItemEntity);
        if (!pickup) ci.cancel();
    }
}
