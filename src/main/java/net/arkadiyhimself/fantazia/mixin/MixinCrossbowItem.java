package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders.ArrowEnchantmentsHolder;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.registries.FTZEnchantments;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class MixinCrossbowItem {
    @Inject(at = @At("RETURN"), method = "createProjectile")
    private void applyEnchantment(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit, CallbackInfoReturnable<Projectile> cir) {
        if (!(cir.getReturnValue() instanceof AbstractArrow arrow)) return;
        Registry<Enchantment> enchantmentRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        ArrowEnchantmentsHolder arrowEnchantmentsHolder = arrow.getData(FTZAttachmentTypes.ARROW_ENCHANTMENTS);
        int duel = weapon.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(FTZEnchantments.DUELIST));
        arrowEnchantmentsHolder.setDuelist(duel);
        int ball = weapon.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(FTZEnchantments.BALLISTA));
        arrowEnchantmentsHolder.setBallista(ball);
    }
}
