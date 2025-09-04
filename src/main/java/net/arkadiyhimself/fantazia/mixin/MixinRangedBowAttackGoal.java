package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.data.tags.FTZItemTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.BowItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RangedBowAttackGoal.class)
public class MixinRangedBowAttackGoal <T extends Mob & RangedAttackMob> {

    @Shadow @Final private T mob;

    @Inject(at = @At("HEAD"), method = "canUse", cancellable = true)
    private void cancel(CallbackInfoReturnable<Boolean> cir) {
        boolean flag = mob.isHolding(item -> {
            boolean flag1 = item.getItem() instanceof BowItem;
            boolean flag2 = !item.is(FTZItemTags.DISABLED_BY_DISARM) || !mob.hasEffect(FTZMobEffects.DISARM);
            return flag1 && flag2;
        });
        cir.setReturnValue(flag);
    }
}
