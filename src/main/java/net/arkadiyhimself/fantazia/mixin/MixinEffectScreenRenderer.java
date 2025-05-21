package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.Holder;
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
public abstract class MixinEffectScreenRenderer<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    @Unique
    private static List<Component> fantazia$buildTooltip(Holder<MobEffect> effect) {
        List<Component> components = Lists.newArrayList();
        String lines = Component.translatable(effect.value().getDescriptionId() + ".lines").getString();
        int amo;
        try {
            amo = Integer.parseInt(lines);
        } catch (NumberFormatException e) {
            return components;
        }
        for (int i = 1; i <= amo; i++) {
            String line = effect.value().getDescriptionId() + ".tooltip." + i;
            ChatFormatting color;
            if (i == 1) {
                color = switch (effect.value().getCategory()) {
                    case HARMFUL -> ChatFormatting.DARK_RED;
                    case NEUTRAL -> ChatFormatting.DARK_GRAY;
                    case BENEFICIAL -> ChatFormatting.BLUE;
                };
            } else color = switch (effect.value().getCategory()) {
                case HARMFUL -> ChatFormatting.RED;
                case NEUTRAL -> ChatFormatting.GRAY;
                case BENEFICIAL -> ChatFormatting.AQUA;
            };
            Component actualLine = i == 1 ? Component.translatable(line).withStyle(color, ChatFormatting.BOLD) : Component.translatable(line).withStyle(color);
            components.add(actualLine);
        }
        return components;
    }

    @Unique
    private static void fantazia$renderEffectTooltip(GuiGraphics guiGraphics, Holder<MobEffect> effect, int mouseX, int mouseY) {
        Cleanse cleanse = Cleanse.requiredCleanse(effect);
        Component clns = cleanse.getName();
        List<Component> components = Lists.newArrayList();
        components.addAll(fantazia$buildTooltip(effect));
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.mob_effect.cleanse_required", new ChatFormatting[]{ChatFormatting.GOLD}, null, clns));
        guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, components, mouseX, mouseY);
    }

    protected MixinEffectScreenRenderer(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Unique
    private int fantazia$mouseX;

    @Unique
    private int fantazia$mouseY;

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("HEAD"))
    private void onRenderHead(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {

        // Cache the mouse position to avoid calculating it later.
        this.fantazia$mouseX = mouseX;
        this.fantazia$mouseY = mouseY;
    }
    @Inject(method = "renderBackgrounds(Lnet/minecraft/client/gui/GuiGraphics;IILjava/lang/Iterable;Z)V", at = @At("RETURN"))
    private void renderBackground(GuiGraphics pGuiGraphics, int pRenderX, int pYOffset, Iterable<MobEffectInstance> pEffects, boolean pIsSmall, CallbackInfo ci) {
        if (!Screen.hasShiftDown()) return;
        int yPos = this.topPos;

        for (MobEffectInstance effect : pEffects) {

            final int length = pIsSmall ? 120 : 32;
            final int height = 32;

            // Check if the mouse is within the render area of the effect element.
            if (fantazia$mouseX >= pRenderX && fantazia$mouseY >= yPos && fantazia$mouseX < pRenderX + length && fantazia$mouseY < yPos + height) {
                fantazia$renderEffectTooltip(pGuiGraphics, effect.getEffect(), fantazia$mouseX, fantazia$mouseY);
                break;
            }
            yPos += pYOffset;
        }
    }
}
