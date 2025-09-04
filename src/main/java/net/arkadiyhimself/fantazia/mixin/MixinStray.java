package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.projectile.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Stray.class)
public class MixinStray {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Arrow;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)V"), method = "getArrow")
    private void changeEffect(Arrow instance, MobEffectInstance effectInstance) {
        instance.addEffect(new MobEffectInstance(FTZMobEffects.FROZEN, 360));
    }
}
