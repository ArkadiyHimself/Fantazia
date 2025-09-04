package net.arkadiyhimself.fantazia.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.FTZKeyMappings;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompts;
import net.arkadiyhimself.fantazia.client.gui.RenderBuffers;
import net.arkadiyhimself.fantazia.data.talent.Talent;
import net.arkadiyhimself.fantazia.data.talent.TalentTreeData;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentTabManager;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
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

public class TalentScreen extends Screen {

    private static final ResourceLocation DEFAULT_BACKGROUND = Fantazia.location("textures/gui/talent/background.png");
    private static final ResourceLocation FRAME = Fantazia.location("textures/gui/talent/frame.png");
    public static final ResourceLocation WISDOM_WIDGET = Fantazia.location("textures/gui/talent/wisdom_widget.png");
    public static final ResourceLocation WISDOM_ICON = Fantazia.location("textures/gui/talent/wisdom.png");

    private static final int minX = 0;
    private static final int minY = 0;
    private static final int maxX = 512;
    private static final int maxY = 512;
    public static final int frame = 176;
    public static final int background = 160;

    private final List<TalentTab> TABS = Lists.newArrayList();
    private final TalentsHolder talentsHolder;
    private static double scrollX = 0;
    private static double scrollY = 0;

    private static int tabIndex = -1;

    private int bgX;
    private int bgY;
    private int frX;
    private int frY;

    private @Nullable TalentTab selectedTab = null;
    private boolean scrolling = false;

    public TalentScreen(TalentsHolder talentsHolder) {
        super(GameNarrator.NO_TITLE);
        this.talentsHolder = talentsHolder;
        IPacket.usedPrompt(Prompts.OPEN_TALENT_SCREEN);

        for (Map.Entry<ResourceLocation, TalentTab> entry : ServerTalentTabManager.getTabs().entrySet()) {
            ResourceLocation tabID = entry.getKey();
            List<IHierarchy<Talent>> iHierarchyList = TalentTreeData.getTabToHierarchies().get(tabID);
            TalentTab talentTab = entry.getValue().hierarchies(iHierarchyList);
            TABS.add(talentTab);
        }
    }

    @Override
    protected void init() {
        try {
            TalentTab talentTab = TABS.get(tabIndex);
            if (talentTab != null) this.selectedTab = talentTab;
            return;
        } catch (IndexOutOfBoundsException ignored) {}
        if (this.selectedTab == null && !TABS.isEmpty()) this.selectedTab = TABS.getFirst();
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.bgX = (this.width - background) / 2;
        this.bgY = (this.height - background) / 2;
        this.frX = (this.width - frame) / 2;
        this.frY = (this.height - frame) / 2;

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        this.renderTabs(pGuiGraphics, pMouseX, pMouseY);
        this.renderBG(pGuiGraphics);
        this.renderTalents(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderFrame(pGuiGraphics);
        this.renderWisdomWidget(pGuiGraphics, pMouseX, pMouseY);
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
                    tabIndex = i;
                    selectedTab = talentTab;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1f,1f));
                    scrollX = 0;
                    scrollY = 0;
                    return super.mouseClicked(pMouseX, pMouseY, pButton);
                }
            }
            if (Screen.hasShiftDown()) {
                tryDisableTalent();
            } else tryPurchasing();

        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void tryDisableTalent() {
        if (selectedTab == null) return;
        Talent talent = selectedTab.selectedTalent();
        if (talent == null) return;
        talentsHolder.clickedTalent(talent);
    }

    private void tryPurchasing() {
        if (selectedTab == null) return;
        Talent talent = selectedTab.selectedTalent();
        if (talent == null) return;
        boolean flag = false;
        if (talentsHolder.hasTalent(talent)) {
            Talent child = talent.getChild();
            while (child != null) {
                if (talentsHolder.hasTalent(child)) {
                    child = child.getChild();
                    continue;
                }
                flag = talentsHolder.tryBuyTalent(child);
                break;
            }
        } else flag = talentsHolder.tryBuyTalent(talent);
        if (!flag) FantazicUtil.playSoundUI(FTZSoundEvents.DENIED.value());
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
        guiGraphics.blit(selectedTab == null ? DEFAULT_BACKGROUND : selectedTab.getBackground(), bgX + x0, bgY + y0, 0,0, 512, 512, 512, 512);
        guiGraphics.pose().popPose();
        guiGraphics.disableScissor();
    }

    private void renderTalents(GuiGraphics guiGraphics, double mouseX, double mouseY, float partialTick) {
        if (this.selectedTab == null) return;
        int x0 = (int) scrollX;
        int y0 = (int) scrollY;
        this.selectedTab.drawInsides(guiGraphics, talentsHolder, font, partialTick, x0, y0, mouseX, mouseY, bgX, bgY);
    }

    private void renderFrame(GuiGraphics pGuiGraphics) {
        pGuiGraphics.blit(FRAME, frX, frY, 0, 0, frame, frame, frame, frame);
    }

    private void renderWisdomWidget(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x0 = frX - 100 - 10;
        int y0 = frY + 10;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(WISDOM_WIDGET, x0, y0,0,0,100,30,100,30);
        guiGraphics.blit(WISDOM_ICON, x0 + 10, y0 + 10, 0,0,10,10,10,10);
        RenderSystem.disableBlend();
        int wisdom = talentsHolder.getWisdom();
        boolean redText = selectedTab != null && selectedTab.redWisdomText();
        Component component = Component.literal(String.valueOf(wisdom)).withStyle(redText ? ChatFormatting.RED : ChatFormatting.BLUE);
        guiGraphics.drawString(font, component, x0 + 24, y0 + 11, 0);
        if (FantazicMath.within(x0, x0 + 100, mouseX) && FantazicMath.within(y0, y0 + 30, mouseY)) {
            int lines = 0;
            String basicPath = "fantazia.gui.talent.wisdom";
            List<Component> components = Lists.newArrayList();
            if (Screen.hasShiftDown()) basicPath += ".desc";
            else components.add(Component.translatable(basicPath).withStyle(redText ? ChatFormatting.DARK_RED : ChatFormatting.DARK_BLUE, ChatFormatting.BOLD));
            String desc = Component.translatable(basicPath + ".lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) {
                for (int i = 1; i <= lines; i++) components.add(Component.translatable(basicPath + "." + i).withStyle(redText ? ChatFormatting.RED : ChatFormatting.BLUE));
                if (Screen.hasShiftDown()) RenderBuffers.NO_TOOLTIP_GAP = true;
                guiGraphics.renderComponentTooltip(font, components, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (FTZKeyMappings.TALENTS.isActiveAndMatches(key)) {
            this.onClose();
            return true;
        }
        return false;
    }

    private int bgWidth() {
        return selectedTab == null ? 160 : selectedTab.screenWidth();
    }

    private int bgHeight() {
        return selectedTab == null ? 160 : selectedTab.screenHeight();
    }

    private void scroll(double pX, double pY) {
        this.scrollX = Mth.clamp(this.scrollX + pX, -bgWidth() + background, minX);
        this.scrollY = Mth.clamp(this.scrollY + pY, -bgHeight() + background, minY);
    }
}
