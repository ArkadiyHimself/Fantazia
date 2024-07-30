package net.arkadiyhimself.fantazia.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.BarrierEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.StunEffect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FTZGui {
    public static final ResourceLocation BARS = Fantazia.res("textures/gui/bars.png");
    public static void fireVertex(PoseStack.Pose pMatrixEntry, VertexConsumer pBuffer, float pX, float pY, float pZ, float pTexU, float pTexV) {
        pBuffer.vertex(pMatrixEntry.pose(), pX, pY, pZ).color(255, 255, 255, 255).uv(pTexU, pTexV).overlayCoords(0, 10).uv2(240).normal(pMatrixEntry.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }
    public static void renderStunBar(@NotNull StunEffect stunEffect, GuiGraphics guiGraphics, int x, int y) {
        if (stunEffect.stunned()) {
            int stunPercent = (int) ((float) stunEffect.getDur() / (float) stunEffect.getInitDur() * 182);
            guiGraphics.blit(FTZGui.BARS, x, y, 0, 10f, 182, 5, 182, 182);
            guiGraphics.blit(FTZGui.BARS, x, y, 0, 0, 15F, stunPercent, 5, 182, 182);
        } else if (stunEffect.hasPoints()) {
            int stunPercent = (int) ((float) stunEffect.getPoints() / (float) stunEffect.getMaxPoints() * 182);
            guiGraphics.blit(FTZGui.BARS, x, y, 0, 0F, 182, 5, 182, 182);
            guiGraphics.blit(FTZGui.BARS, x, y, 0, 0, 5F, stunPercent, 5, 182, 182);
        }
    }
    public static void renderBarrierBar(@NotNull BarrierEffect barrierEffect, GuiGraphics guiGraphics, int x, int y) {
        int percent = (int) (barrierEffect.getHealth() / barrierEffect.getInitial() * 182);
        guiGraphics.blit(FTZGui.BARS, x, y, 0, 40F, 182, 5, 182, 182);
        guiGraphics.blit(FTZGui.BARS, x, y, 0, 0, 45F, percent, 5, 182, 182);
    }
}
