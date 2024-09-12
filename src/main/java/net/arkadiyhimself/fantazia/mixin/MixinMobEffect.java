package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEffect.class)
public class MixinMobEffect {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"), method = "applyEffectTick")
    private void advancedHeal(LivingEntity entity, float pHealAmount) {
        HealingSources healingSources = LevelCapHelper.getHealingSources(entity.level());
        if (healingSources != null) AdvancedHealing.tryHeal(entity, healingSources.mobEffectRegen(), pHealAmount);
    }
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"), method = "applyInstantenousEffect")
    private void advancedInstantHeal(LivingEntity entity, float pHealAmount) {
        HealingSources healingSources = LevelCapHelper.getHealingSources(entity.level());
        if (healingSources != null) AdvancedHealing.tryHeal(entity, healingSources.mobEffect(), pHealAmount);
    }
}
