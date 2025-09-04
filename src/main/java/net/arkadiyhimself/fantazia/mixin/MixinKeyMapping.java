package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.api.FTZKeyMappings;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public class MixinKeyMapping {

    @Unique
    private final KeyMapping fantazia$mapping = (KeyMapping) (Object) this;

    @Inject(at = @At("HEAD"), method = "consumeClick", cancellable = true)
    private void cancelInput(CallbackInfoReturnable<Boolean> cir) {
        Player player = Minecraft.getInstance().player;
        if (player == null || fantazia$mapping == FTZKeyMappings.DASH) return;
        if (ActionsHelper.preventActions(player) && !(Minecraft.getInstance().screen instanceof PauseScreen)) cir.setReturnValue(false);
    }
    @Inject(at = @At("HEAD"), method = "isDown", cancellable = true)
    private void cancelHold(CallbackInfoReturnable<Boolean> cir) {
        Player player = Minecraft.getInstance().player;
        if (player == null || fantazia$mapping == FTZKeyMappings.DASH) return;
        if (ActionsHelper.preventActions(player) && !(Minecraft.getInstance().screen instanceof PauseScreen)) cir.setReturnValue(false);
    }
}
