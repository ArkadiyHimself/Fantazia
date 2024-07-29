package net.arkadiyhimself.fantazia.client.screen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.capability.entity.talent.TalentData;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TalentsScreen extends Screen {
    private static final ResourceLocation BACKGROUND = Fantazia.res("textures/gui/talent/background.png");
    private static final ResourceLocation FRAME = Fantazia.res("textures/gui/talent/frame.png");
    private final TalentData data;
    private double scrollX = 0;
    private double scrollY = 0;
    private final int minX = 0;
    private final int minY = 0;
    private final int maxX = 512;
    private final int maxY = 512;
    private boolean scrolling = false;
    public TalentsScreen(TalentData data) {
        super(GameNarrator.NO_TITLE);
        this.data = data;
    }
    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int pX = (this.width - 160) / 2;
        int pY = (this.height - 160) / 2;
        this.renderBackground(pGuiGraphics);
        this.renderBackground(pGuiGraphics, pX, pY);
        this.renderFrame(pGuiGraphics, pX, pY);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton != 0) {
            this.scrolling = false;
            return false;
        } else {
            if (!this.scrolling) this.scrolling = true;
            else {
                scroll(pDragX, pDragY);
            }
            return true;
        }
    }

    private void renderFrame(GuiGraphics pGuiGraphics, int pX, int pY) {
        pGuiGraphics.blit(FRAME, pX - 8, pY - 8, 0, 0, 176, 176, 176, 176);
    }
    private void renderBackground(GuiGraphics guiGraphics, int pX, int pY) {
        guiGraphics.enableScissor(pX, pY, pX + 160, pY + 160);

        guiGraphics.pose().pushPose();
        int x0 = (int) scrollX;
        int y0 = (int) scrollY;
        guiGraphics.blit(BACKGROUND, pX + x0, pY + y0, 0f,0f, 512, 512, 512, 512);
        buildContents(guiGraphics, pX, pY);

        guiGraphics.pose().popPose();
        guiGraphics.disableScissor();
    }
    private void scroll(double pX, double pY) {
        this.scrollX = Mth.clamp(this.scrollX + pX, -this.maxX + 160, this.minX);
        this.scrollY = Mth.clamp(this.scrollY + pY, -this.maxY + 160, this.minY);
    }
    private void buildContents(GuiGraphics guiGraphics, int pX, int pY) {

    }
}
