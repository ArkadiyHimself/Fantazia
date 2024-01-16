package net.arkadiyhimself.combatimprovement.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.ActiveAndTargeted.SpellCaster;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen {
    @Inject(at = @At("TAIL"), method = "renderSlot")
    private void renderManaCost(PoseStack pPoseStack, Slot pSlot, CallbackInfo ci) {
        if (pSlot.getItem().getItem() instanceof SpellCaster spellCaster) {
            Component component = Component.translatable(String.format("%.1f", spellCaster.MANACOST)).withStyle(ChatFormatting.BOLD);
            pPoseStack.pushPose();
            pPoseStack.translate(pSlot.x, pSlot.y, 500);
            pPoseStack.translate(0.75,0.75,0.75);
            Minecraft.getInstance().font.drawShadow(pPoseStack, component,0,0,3381759);
            pPoseStack.popPose();
        }
    }
}
