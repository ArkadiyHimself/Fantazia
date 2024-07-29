package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.CleanseStrength;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class MixinEffectScreenRenderer extends AbstractContainerScreen {
    private static List<Component> buildTooltip(MobEffect effect) {
        List<Component> components = Lists.newArrayList();
        String lines = Component.translatable(effect.getDescriptionId() + ".lines").getString();
        int amo;
        try {
            amo = Integer.parseInt(lines);
        } catch (NumberFormatException e) {
            return components;
        }
        for (int i = 1; i <= amo; i++) {
            String line = effect.getDescriptionId() + ".tooltip." + i;
            ChatFormatting color;
            if (i == 1) {
                color = switch (effect.getCategory()) {
                    case HARMFUL -> ChatFormatting.DARK_RED;
                    case NEUTRAL -> ChatFormatting.DARK_GRAY;
                    case BENEFICIAL -> ChatFormatting.BLUE;
                };
            } else color = switch (effect.getCategory()) {
                case HARMFUL -> ChatFormatting.RED;
                case NEUTRAL -> ChatFormatting.GRAY;
                case BENEFICIAL -> ChatFormatting.AQUA;
            };
            Component actualLine = i == 1 ? Component.translatable(line).withStyle(color, ChatFormatting.BOLD) : Component.translatable(line).withStyle(color);
            components.add(actualLine);
        }
        return components;
    }
    private static void renderEffectTooltip(GuiGraphics guiGraphics, MobEffect effect, int mouseX, int mouseY) {
        Cleanse cleanse = CleanseStrength.getRequiredStrength(effect);
        Component clns = cleanse.getName();
        List<Component> components = Lists.newArrayList();
        components.addAll(buildTooltip(effect));
        GuiHelper.addComponent(components, "tooltip.fantazia.common.cleanse", new ChatFormatting[]{ChatFormatting.GOLD}, null, clns);
        guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, components, mouseX, mouseY);
    }

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
                renderEffectTooltip(pGuiGraphics, effect.getEffect(), mouseX, mouseY);
                break;
            }
            yPos += pYOffset;
        }
    }
}
