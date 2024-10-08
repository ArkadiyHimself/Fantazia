package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.effect.HealOrHarmMobEffect")
public class MixinHealOrHarmMobEffect {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"), method = "applyInstantenousEffect")
    private void instantHeal(LivingEntity instance, float healAmount) {
        HealingSourcesHolder healingSources = LevelAttributesHelper.getHealingSources(instance.level());
        if (healingSources != null) AdvancedHealing.tryHeal(instance, healingSources.mobEffect(), healAmount);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"), method = "applyEffectTick")
    private void notInstantHeal(LivingEntity instance, float healAmount) {
        HealingSourcesHolder healingSources = LevelAttributesHelper.getHealingSources(instance.level());
        if (healingSources != null) AdvancedHealing.tryHeal(instance, healingSources.mobEffect(), healAmount);
    }
}
