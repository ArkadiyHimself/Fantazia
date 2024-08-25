package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureManager;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.features.ArrowEnchant;
import net.arkadiyhimself.fantazia.registries.FTZEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class MixinCrossbowItem {
    @Inject(at = @At("RETURN"), method = "getArrow")
    private static void applyEnchantment(Level pLevel, LivingEntity pLivingEntity, ItemStack pCrossbowStack, ItemStack pAmmoStack, CallbackInfoReturnable<AbstractArrow> cir) {
        AbstractArrow arrow = cir.getReturnValue();
        FeatureManager featureManager = FeatureGetter.getUnwrap(arrow);
        if (featureManager == null) return;
        int duel = pCrossbowStack.getEnchantmentLevel(FTZEnchantments.DUELIST.get());
        featureManager.getFeature(ArrowEnchant.class).ifPresent(arrowEnchant -> arrowEnchant.setDuelist(duel));
        int ball = pCrossbowStack.getEnchantmentLevel(FTZEnchantments.BALLISTA.get());
        featureManager.getFeature(ArrowEnchant.class).ifPresent(arrowEnchant -> arrowEnchant.setBallista(ball));
    }
}
