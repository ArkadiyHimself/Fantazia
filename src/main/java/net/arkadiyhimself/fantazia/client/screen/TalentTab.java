package net.arkadiyhimself.fantazia.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChainHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TalentTab {
    private static final int width = 32;
    private static final int height = 48;
    private static final ResourceLocation EMPTY_TAB = Fantazia.res("textures/gui/talent/talent_tab_empty.png");
    private static final ResourceLocation ICON_UNLOCKED = Fantazia.res("textures/gui/talent/talent_icon_unlocked.png");
    private static final ResourceLocation ICON_LOCKED = Fantazia.res("textures/gui/talent/talent_icon_locked.png");
    private final List<IHierarchy<BasicTalent>> HIERARCHIES = Lists.newArrayList();
    private final ResourceLocation icon;
    private final Component title;
    private boolean up = true;
    private float alpha = 0.5f;
    @Nullable
    private BasicTalent selectedTalent = null;
    public TalentTab(ResourceLocation icon, Component title) {
        this.icon = icon;
        this.title = title;
    }
    public TalentTab hierarchies(List<IHierarchy<BasicTalent>> hierarchies) {
        this.HIERARCHIES.clear();
        this.HIERARCHIES.addAll(hierarchies);
        return this;
    }
    public int cornerOffsetX(int index) {
        return 4 + (width + 2) * index;
    }
    public int cornerOffsetY(boolean selected) {
        return selected ? -40 : -24;
    }
    public boolean isMouseOver(int cornerX, int cornerY, double pMouseX, double pMouseY, boolean selected, int index) {
        int i = cornerX + cornerOffsetX(index);
        return FantazicMath.within(i, i + width, pMouseX) && FantazicMath.within(cornerY + cornerOffsetY(selected), cornerY, pMouseY);
    }
    public void drawTab(int cornerX, int cornerY, GuiGraphics guiGraphics, boolean selected, int index) {
        int x0 = cornerX + cornerOffsetX(index);
        int y0 = cornerY + cornerOffsetY(selected);
        guiGraphics.blit(EMPTY_TAB, x0, y0, 0,0,32,48,32,48);
        guiGraphics.blit(icon, x0 + 6, y0 + 6, 0,0,20,20,20,20);
    }
    public void drawInsides(GuiGraphics guiGraphics, TalentsHolder talentsHolder, Font font, int scrollX, int scrollY, double mouseX, double mouseY, int bgX, int bgY) {
        if (up) alpha += 0.01f;
        else alpha -= 0.01f;
        if (alpha > 1f) up = false;
        else if (alpha < 0.5f) up = true;

        int x0 = bgX + scrollX + 12;
        int y0 = bgY + scrollY + 12;

        int X = x0;
        this.selectedTalent = null;
        guiGraphics.enableScissor(bgX, bgY, bgX + TalentsScreen.background, bgY + TalentsScreen.background);
        for (IHierarchy<BasicTalent> hierarchy : HIERARCHIES) {
            if (hierarchy instanceof ChainHierarchy<BasicTalent> chainHierarchy) {
                int Y = y0;
                List<BasicTalent> talentList = chainHierarchy.getElements();
                if (!(chainHierarchy instanceof ChaoticHierarchy<BasicTalent>)) {
                    int length = talentList.size() * 32 - 56;
                    if (length > 0) {
                        guiGraphics.vLine(X + 10, y0 + 24, y0 + 24 + length, -16777216);
                        guiGraphics.vLine(X + 11, y0 + 24, y0 + 24 + length, -1);
                        guiGraphics.vLine(X + 12, y0 + 24, y0 + 24 + length, -1);
                        guiGraphics.vLine(X + 13, y0 + 24, y0 + 24 + length, -16777216);
                    }
                }
                for (BasicTalent talent : talentList) {
                    boolean unlocked = talentsHolder.talentUnlocked(talent);
                    ResourceLocation icon = unlocked ? ICON_UNLOCKED : ICON_LOCKED;
                    float color = talentsHolder.canBePurchased(talent) ? alpha : 0.5f;
                    if (!unlocked) guiGraphics.setColor(color, color, color,1f);
                    guiGraphics.blit(icon, X, Y,0,0,24,24,24,24);
                    ResourceLocation talentIcon = talent.getIconTexture();
                    if (!unlocked) guiGraphics.setColor(color, color, color,0.5f);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    guiGraphics.blit(talentIcon, X + 2, Y + 2, 0, 0, 20, 20, 20, 20);
                    RenderSystem.disableBlend();
                    guiGraphics.setColor(1f, 1f, 1f, 1f);
                    if (mouseHoversTalent(X, Y, mouseX, mouseY) && mouseInsideBG(bgX, bgY, mouseX, mouseY)) selectedTalent = talent;

                    Y += 32;
                }
            } else {
                BasicTalent talent = hierarchy.getMainElement();
                boolean unlocked = talentsHolder.talentUnlocked(talent);
                ResourceLocation icon = unlocked ? ICON_UNLOCKED : ICON_LOCKED;
                float color = talentsHolder.canBePurchased(talent) ? alpha : 0.5f;
                if (!unlocked) guiGraphics.setColor(color, color, color,1f);
                guiGraphics.blit(icon, X, y0, 0,0,24,24,24,24);
                ResourceLocation talentIcon = talent.getIconTexture();
                if (!unlocked) guiGraphics.setColor(color, color, color, 0.5f);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                guiGraphics.blit(talentIcon, X + 2, y0 + 2,0,0,20,20,20,20);
                RenderSystem.disableBlend();
                guiGraphics.setColor(1f,1f,1f,1f);
                if (mouseHoversTalent(X, y0, mouseX, mouseY) && mouseInsideBG(bgX, bgY, mouseX, mouseY)) selectedTalent = talent;
            }
            X += 48;
        }
        guiGraphics.disableScissor();
        if (selectedTalent != null) guiGraphics.renderComponentTooltip(font, selectedTalent.buildIconTooltip(), (int) mouseX, (int) mouseY);

    }
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, Font font) {
        guiGraphics.renderTooltip(font, title, mouseX, mouseY);
        guiGraphics.flush();
    }
    private boolean mouseHoversTalent(int x0, int y0, double mouseX, double mouseY) {
        return FantazicMath.within(x0, x0 + 24, mouseX) && FantazicMath.within(y0, y0 + 24, mouseY);
    }
    private boolean mouseInsideBG(int x0, int y0, double mouseX, double mouseY) {
        return FantazicMath.within(x0, x0 + TalentsScreen.background, mouseX) && FantazicMath.within(y0, y0 + TalentsScreen.background, mouseY);
    }
    @Nullable
    public BasicTalent selectedTalent() {
        return selectedTalent;
    }
    public static class Builder {
        private final ResourceLocation icon;
        private final String title;

        public Builder(ResourceLocation icon, String title) {
            this.icon = icon;
            this.title = title;
        }
        public TalentTab build() {
            return new TalentTab(icon, Component.translatable(title));
        }
    }
}
