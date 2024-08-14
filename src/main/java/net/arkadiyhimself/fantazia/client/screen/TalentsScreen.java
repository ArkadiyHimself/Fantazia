package net.arkadiyhimself.fantazia.client.screen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.data.talents.TalentTreeData;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.capabilityupdate.TalentBuyingC2S;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TalentsScreen extends Screen {
    private static final ResourceLocation BACKGROUND = Fantazia.res("textures/gui/talent/background.png");
    private static final ResourceLocation FRAME = Fantazia.res("textures/gui/talent/frame.png");
    public static final int frame = 176;
    public static final int background = 160;
    private final List<TalentTab> TABS = Lists.newArrayList();
    private final TalentTab ABILITIES;
    private final TalentTab STATS_WISDOM;
    private final TalentsHolder talentsHolder;
    private double scrollX = 0;
    private double scrollY = 0;
    private final int minX = 0;
    private final int minY = 0;
    private final int maxX = 512;
    private final int maxY = 512;
    private int bgX;
    private int bgY;
    private int frX;
    private int frY;
    @Nullable
    private TalentTab selectedTab = null;
    private boolean scrolling = false;
    public TalentsScreen(TalentsHolder talentsHolder) {
        super(GameNarrator.NO_TITLE);
        this.talentsHolder = talentsHolder;
        this.ABILITIES = new TalentTab(this, 0, Fantazia.res("textures/gui/talent/tab/abilities.png"), Component.translatable("fantazia.talent_tabs.abilities"), "abilities", TalentTreeData.abilities().values().stream().toList(), talentsHolder);
        this.STATS_WISDOM = new TalentTab(this, 1, Fantazia.res("textures/gui/talent/tab/stats_wisdom.png"), Component.translatable("fantazia.talent_tabs.stats_wisdom"), "stats_wisdom", TalentTreeData.statsWisdom().values().stream().toList(), talentsHolder);
        TABS.add(ABILITIES);
        TABS.add(STATS_WISDOM);
    }
    @Override
    protected void init() {
        if (this.selectedTab == null && !TABS.isEmpty()) this.selectedTab = TABS.get(0);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.bgX = (this.width - background) / 2;
        this.bgY = (this.height - background) / 2;
        this.frX = (this.width - frame) / 2;
        this.frY = (this.height - frame) / 2;

        this.renderBackground(pGuiGraphics);

        this.renderTabs(pGuiGraphics, pMouseX, pMouseY);
        this.renderBG(pGuiGraphics);
        this.renderTalents(pGuiGraphics, pMouseX, pMouseY);
        this.renderFrame(pGuiGraphics);
        if (selectedTab == STATS_WISDOM) {
            int wisdom = talentsHolder.getWisdom();
            pGuiGraphics.drawString(font, String.valueOf(wisdom), frX + frame + 32, frY + 88, 4693243);
        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton != 0) {
            this.scrolling = false;
            return false;
        } else {
            if (!this.scrolling) this.scrolling = true;
            else scroll(pDragX, pDragY);
            return true;
        }
    }
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            for (TalentTab talentTab : TABS) {
                if (talentTab == selectedTab) continue;
                if (talentTab.isMouseOver(frX, frY, pMouseX, pMouseY, false)) {
                    selectedTab = talentTab;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.get(), 1f,1f));
                    return super.mouseClicked(pMouseX, pMouseY, pButton);
                }
            }
            if (selectedTab != null) {
                BasicTalent basicTalent = selectedTab.selectedTalent();
                if (basicTalent != null) NetworkHandler.sendToServer(new TalentBuyingC2S(basicTalent));
            }

        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void renderTabs(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {
        for (TalentTab talentTab : this.TABS) {
            boolean sel = talentTab == this.selectedTab;
            if (sel) talentTab.drawTab(frX, frY, pGuiGraphics, true);
            else {
                boolean mouseOver = talentTab.isMouseOver(frX, frY, mouseX, mouseY, false);
                float clr = mouseOver ? 0.75f : 0.5f;
                pGuiGraphics.setColor(clr, clr, clr, clr);
                talentTab.drawTab(frX, frY, pGuiGraphics, false);
                pGuiGraphics.setColor(1f,1f,1f,1f);
            }
            if (talentTab.isMouseOver(frX, frY, mouseX, mouseY, sel)) talentTab.renderTooltip(pGuiGraphics, mouseX, mouseY, font);
        }
    }
    private void renderBG(GuiGraphics guiGraphics) {
        guiGraphics.enableScissor(bgX, bgY, bgX + background, bgY + background);
        guiGraphics.pose().pushPose();
        int x0 = (int) scrollX;
        int y0 = (int) scrollY;
        guiGraphics.blit(BACKGROUND, bgX + x0, bgY + y0, 0f,0f, 512, 512, 512, 512);
        guiGraphics.pose().popPose();
        guiGraphics.disableScissor();
    }
    private void renderTalents(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (this.selectedTab == null) return;
        int x0 = (int) scrollX;
        int y0 = (int) scrollY;
        this.selectedTab.drawInsides(guiGraphics, font, x0, y0, mouseX, mouseY, bgX, bgY);
    }
    private void renderFrame(GuiGraphics pGuiGraphics) {
        pGuiGraphics.blit(FRAME, frX, frY, 0, 0, frame, frame, frame, frame);
    }
    private void scroll(double pX, double pY) {
        this.scrollX = Mth.clamp(this.scrollX + pX, -this.maxX + background, this.minX);
        this.scrollY = Mth.clamp(this.scrollY + pY, -this.maxY + background, this.minY);
    }
}
