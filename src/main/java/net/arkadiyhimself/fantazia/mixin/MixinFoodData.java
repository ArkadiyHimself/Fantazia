package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.HealingSourcesHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FoodData.class)
public abstract class MixinFoodData {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"), method = "tick")
    private void advancedHeal(Player player, float v) {
        LevelAttributesHelper.healEntity(player, v, HealingSourcesHolder::naturalRegen);
    }
}
