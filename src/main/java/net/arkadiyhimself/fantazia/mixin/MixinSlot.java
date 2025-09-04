package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class MixinSlot {

    @Shadow @Final public Container container;

    @Inject(at = @At("HEAD"), method = "mayPlace", cancellable = true)
    private void cancelPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (container instanceof PlayerEnderChestContainer && stack.is(FTZItems.ENDER_POCKET))
            cir.setReturnValue(false);
    }

}
