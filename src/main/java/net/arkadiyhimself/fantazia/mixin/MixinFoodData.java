package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.AdvancedHealing;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.HealingSource;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.HealingTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FoodData.class)
public abstract class MixinFoodData {
    @Shadow public abstract void addExhaustion(float pExhaustion);

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"), method = "tick")
    private void advancedHeal(Player player, float v) {
        HealingSource source = new HealingSource(HealingTypes.REGEN_NATURAL);
        AdvancedHealing.heal(player, source, v);
        addExhaustion(v);
    }
}
