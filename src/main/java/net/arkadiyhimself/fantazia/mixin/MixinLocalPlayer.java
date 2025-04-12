package net.arkadiyhimself.fantazia.mixin;

import com.mojang.authlib.GameProfile;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.StaminaHolder;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends Player {

    public MixinLocalPlayer(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Unique
    final LocalPlayer fantazia$player = (LocalPlayer) (Object) this;
    @Inject(at = @At("HEAD"), method = "hasEnoughFoodToStartSprinting", cancellable = true)
    private void sprinting(CallbackInfoReturnable<Boolean> cir) {
        StaminaHolder staminaHolder = PlayerAbilityGetter.takeHolder(fantazia$player, StaminaHolder.class);
        if (staminaHolder != null && staminaHolder.getStamina() <= 0.1f) cir.setReturnValue(false);
    }
}
