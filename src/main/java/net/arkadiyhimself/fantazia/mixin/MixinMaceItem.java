package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MaceItem.class)
public class MixinMaceItem {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;resetFallDistance()V"), method = "postHurtEnemy")
    private void reset(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfo ci) {
        if (attacker instanceof ServerPlayer serverPlayer) PlayerAbilityHelper.pogo(serverPlayer);
    }
}
