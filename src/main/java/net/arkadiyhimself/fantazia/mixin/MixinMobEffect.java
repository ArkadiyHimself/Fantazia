package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.AdvancedHealing;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.HealingSource;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.HealingTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEffect.class)
public class MixinMobEffect {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"), method = "applyEffectTick")
    private void advancedHeal(LivingEntity entity, float pHealAmount) {
        HealingSource source = new HealingSource(HealingTypes.REGEN_MOBEFFECT);
        AdvancedHealing.heal(entity, source, pHealAmount);
    }
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"), method = "applyInstantenousEffect")
    private void advancedInstantHeal(LivingEntity entity, float pHealAmount) {
        HealingSource source = new HealingSource(HealingTypes.GENERIC_MOBEFFECT);
        AdvancedHealing.heal(entity, source, pHealAmount);
    }
}
