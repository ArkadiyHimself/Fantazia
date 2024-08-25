package net.arkadiyhimself.fantazia.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.data.talents.TalentTreeData;
import net.arkadiyhimself.fantazia.data.talents.reload.TalentTabManager;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.capabilityupdate.TalentBuyingC2S;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.ChatFormatting;
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
import java.util.Map;

public class TalentsScreen extends Screen {
    private static final ResourceLocation BACKGROUND = Fantazia.res("textures/gui/talent/background.png");
    private static final ResourceLocation FRAME = Fantazia.res("textures/gui/talent/frame.png");
    private static final ResourceLocation WISDOM_WIDGET = Fantazia.res("textures/gui/talent/wisdom_widget.png");
    public static final int frame = 176;
    public static final int background = 160;
    private final List<TalentTab> TABS = Lists.newArrayList();
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
     //   TalentTab ABILITIES = new TalentTab(Fantazia.res("textures/gui/talent/tab/abilities.png"), Component.translatable("fantazia.talent_tabs.abilities"));

     //   TalentTab STATS_WISDOM = new TalentTab(Fantazia.res("textures/gui/talent/tab/stats_wisdom.png"), Component.translatable("fantazia.talent_tabs.stats_wisdom"));
        for (Map.Entry<ResourceLocation, TalentTab> entry : TalentTabManager.getTabs().entrySet()) {
            ResourceLocation tabID = entry.getKey();
            List<IHierarchy<BasicTalent>> iHierarchyList = TalentTreeData.getTabHierarchies().get(tabID);
            TalentTab talentTab = entry.getValue().hierarchies(iHierarchyList);
            TABS.add(talentTab);
        }
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
        this.renderWisdom(pGuiGraphics);

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
            for (int i = 0; i < TABS.size(); i++) {
                TalentTab talentTab = TABS.get(i);
                if (talentTab == selectedTab) continue;
                if (talentTab.isMouseOver(frX, frY, pMouseX, pMouseY, false, i)) {
                    selectedTab = talentTab;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.get(), 1f,1f));
                    scrollX = 0;
                    scrollY = 0;
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
        for (int i = 0; i < TABS.size(); i++) {
            TalentTab talentTab = TABS.get(i);
            boolean sel = talentTab == this.selectedTab;

            if (sel) talentTab.drawTab(frX, frY, pGuiGraphics, true, i);
            else {
                boolean mouseOver = talentTab.isMouseOver(frX, frY, mouseX, mouseY, false, i);
                float clr = mouseOver ? 0.75f : 0.5f;
                pGuiGraphics.setColor(clr, clr, clr, clr);
                talentTab.drawTab(frX, frY, pGuiGraphics, false, i);
                pGuiGraphics.setColor(1f,1f,1f,1f);
            }

            if (talentTab.isMouseOver(frX, frY, mouseX, mouseY, sel, i)) talentTab.renderTooltip(pGuiGraphics, mouseX, mouseY, font);
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
        this.selectedTab.drawInsides(guiGraphics, talentsHolder, font, x0, y0, mouseX, mouseY, bgX, bgY);
    }
    private void renderFrame(GuiGraphics pGuiGraphics) {
        pGuiGraphics.blit(FRAME, frX, frY, 0, 0, frame, frame, frame, frame);
    }
    private void renderWisdom(GuiGraphics guiGraphics) {
        int x0 = frX + frame + 10;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(WISDOM_WIDGET, x0, frY,0,0,100,30,100,30);
        RenderSystem.disableBlend();
        int wisdom = talentsHolder.getWisdom();
        Component component = GuiHelper.bakeComponent("fantazia.gui.talent.wisdom", null, new ChatFormatting[]{ChatFormatting.DARK_PURPLE}, wisdom);
        guiGraphics.drawString(font, component, x0 + 12, frY + 11, 0);

    }
    private void scroll(double pX, double pY) {
        this.scrollX = Mth.clamp(this.scrollX + pX, -this.maxX + background, this.minX);
        this.scrollY = Mth.clamp(this.scrollY + pY, -this.maxY + background, this.minY);
    }
}
