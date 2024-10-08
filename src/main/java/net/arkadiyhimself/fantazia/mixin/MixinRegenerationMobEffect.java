package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.effect.RegenerationMobEffect")
public class MixinRegenerationMobEffect {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"), method = "applyEffectTick")
    private void advancedHeal(LivingEntity instance, float healAmount) {
        HealingSourcesHolder healingSources = LevelAttributesHelper.getHealingSources(instance.level());
        if (healingSources != null) AdvancedHealing.tryHeal(instance, healingSources.mobEffectRegen(), healAmount);
    }
}
