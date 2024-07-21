package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.client.gui.FTZGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class MixinEffectScreenRenderer extends AbstractContainerScreen {
    public MixinEffectScreenRenderer(AbstractContainerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    @Unique
    private int mouseX;
    @Unique
    private int mouseY;
    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("HEAD"))
    private void onRenderHead(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {

        // Cache the mouse position to avoid calculating it later.
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
    @Inject(method = "renderBackgrounds(Lnet/minecraft/client/gui/GuiGraphics;IILjava/lang/Iterable;Z)V", at = @At("RETURN"))
    private void renderBackground(GuiGraphics pGuiGraphics, int pRenderX, int pYOffset, Iterable<MobEffectInstance> pEffects, boolean pIsSmall, CallbackInfo ci) {
        if (!Screen.hasShiftDown()) return;
        int yPos = this.topPos;

        for (MobEffectInstance effect : pEffects) {

            final int length = pIsSmall ? 120 : 32;
            final int height = 32;

            // Check if the mouse is within the render area of the effect element.
            if (mouseX >= pRenderX && mouseY >= yPos && mouseX < pRenderX + length && mouseY < yPos + height) {
                FTZGui.renderEffectTooltip(pGuiGraphics, effect.getEffect(), mouseX, mouseY);
                break;
            }
            yPos += pYOffset;
        }
    }
}
