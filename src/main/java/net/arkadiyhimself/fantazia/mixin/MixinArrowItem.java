package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.advanced.capability.entity.feature.FeatureGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.feature.FeatureManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.feature.features.ArrowEnchant;
import net.arkadiyhimself.fantazia.registries.FTZEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public class MixinArrowItem {
    @Inject(at = @At("RETURN"), method = "createArrow")
    private void applyEnchantments(Level pLevel, ItemStack pStack, LivingEntity pShooter, CallbackInfoReturnable<AbstractArrow> cir) {
        AbstractArrow arrow = cir.getReturnValue();
        boolean frozen = pShooter.getMainHandItem().getEnchantmentLevel(FTZEnchantments.FREEZE) > 0;
        FeatureManager featureManager = FeatureGetter.getUnwrap(arrow);
        if (featureManager == null || !frozen) return;
        featureManager.getFeature(ArrowEnchant.class).ifPresent(ArrowEnchant::freeze);
    }
}
