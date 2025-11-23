package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.client.gui.RenderBuffers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen {

    @Inject(at = @At(value = "HEAD"), method = "renderSlotContents")
    private void renderItemDecorations(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, String countString, CallbackInfo ci) {
        if (!itemstack.isEmpty()) RenderBuffers.RENDER_RECHARGEABLE_TOOL_DATA = true;
    }
}
