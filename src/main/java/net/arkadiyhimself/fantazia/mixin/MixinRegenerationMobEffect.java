package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.HealingSourcesHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.effect.RegenerationMobEffect")
public class MixinRegenerationMobEffect {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"), method = "applyEffectTick")
    private void advancedHeal(LivingEntity instance, float healAmount) {
        LevelAttributesHelper.healEntity(instance, healAmount, HealingSourcesHolder::mobEffectRegen);
    }
}
