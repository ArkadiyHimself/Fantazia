package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.registries.FTZEnchantments;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public class MixinArrowItem {
    @Inject(at = @At("RETURN"), method = "createArrow")
    private void applyEnchantments(Level level, ItemStack ammo, LivingEntity shooter, ItemStack weapon, CallbackInfoReturnable<AbstractArrow> cir) {
        Registry<Enchantment> enchantmentRegistry = shooter.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        AbstractArrow arrow = cir.getReturnValue();
        if (weapon.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(FTZEnchantments.FREEZE)) > 0) arrow.getData(FTZAttachmentTypes.ARROW_ENCHANTMENTS).freeze();
    }
}
