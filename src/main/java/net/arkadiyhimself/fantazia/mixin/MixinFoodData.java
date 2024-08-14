package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FoodData.class)
public abstract class MixinFoodData {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"), method = "tick")
    private void advancedHeal(Player player, float v) {
        HealingSources healingSources = LevelCapHelper.healingSources(player.level());
        if (healingSources != null) AdvancedHealing.heal(player, healingSources.naturalRegen(), v);
    }
}
