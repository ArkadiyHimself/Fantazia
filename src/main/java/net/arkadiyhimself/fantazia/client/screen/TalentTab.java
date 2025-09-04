package net.arkadiyhimself.fantazia.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.client.gui.RenderBuffers;
import net.arkadiyhimself.fantazia.data.talent.Talent;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.talent_tab.TalentTabBuilderHolder;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChainHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TalentTab {

    private static final int width = 32;
    private static final int height = 48;
    private static final ResourceLocation EMPTY_TAB = Fantazia.location("textures/gui/talent/talent_tab_empty.png");
    private static final ResourceLocation TALENT_ICON = Fantazia.location("textures/gui/talent/talent_icon_default.png");
    private final ResourceLocation background;
    private final List<IHierarchy<Talent>> HIERARCHIES = Lists.newArrayList();
    private final ResourceLocation icon;
    private final String title;
    private boolean redWisdomText = false;
    @Nullable
    private Talent selectedTalent = null;

    public TalentTab(ResourceLocation icon, String title, ResourceLocation background) {
        this.icon = icon;
        this.title = title;
        this.background = background;
    }

    public TalentTab hierarchies(List<IHierarchy<Talent>> hierarchies) {
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

    public void drawInsides(GuiGraphics guiGraphics, TalentsHolder talentsHolder, Font font, float partialTick, int scrollX, int scrollY, double mouseX, double mouseY, int bgX, int bgY) {
        float alphaPurchase = 0.7f + (float) FantazicMath.intoSin(talentsHolder.getPlayer().tickCount + partialTick, 24) * 0.15f;

        int x0 = bgX + scrollX + 12;
        int y0 = bgY + scrollY + 12;

        int X = x0;
        this.selectedTalent = null;
        this.redWisdomText = false;
        guiGraphics.enableScissor(bgX, bgY, bgX + TalentScreen.background, bgY + TalentScreen.background);
        for (IHierarchy<Talent> hierarchy : HIERARCHIES) {
            if (hierarchy instanceof ChainHierarchy<Talent> chainHierarchy) {
                int Y = y0;
                List<Talent> talentList = chainHierarchy.getElements();
                if (!(chainHierarchy instanceof ChaoticHierarchy<Talent>)) {
                    int length = talentList.size() * 32 - 56;
                    if (length > 0) {
                        guiGraphics.vLine(X + 10, y0 + 24, y0 + 24 + length, -16777216);
                        guiGraphics.vLine(X + 11, y0 + 24, y0 + 24 + length, -1);
                        guiGraphics.vLine(X + 12, y0 + 24, y0 + 24 + length, -1);
                        guiGraphics.vLine(X + 13, y0 + 24, y0 + 24 + length, -16777216);
                    }
                }
                for (Talent talent : talentList) {
                    boolean unlocked = talentsHolder.talentUnlocked(talent);
                    boolean disabled = talentsHolder.isDisabled(talent);
                    float color = talentsHolder.canBePurchased(talent) ? alphaPurchase : 0.5f;
                    if (!unlocked) guiGraphics.setColor(color, color, color,1f);
                    else if (disabled) guiGraphics.setColor(1f, 0.55f,0.55f,0.85f);
                    ResourceLocation icon = talent.background().isPresent() ? talent.background().get() : TALENT_ICON;
                    guiGraphics.blit(icon, X, Y,0,0,24,24,24,24);
                    ResourceLocation talentIcon = talent.icon();
                    if (!unlocked) guiGraphics.setColor(color, color, color,0.5f);
                    else if (disabled) guiGraphics.setColor(1f, 0.55f,0.55f,0.85f);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    guiGraphics.blit(talentIcon, X + 2, Y + 2, 0, 0, 20, 20, 20, 20);
                    RenderSystem.disableBlend();
                    guiGraphics.setColor(1f, 1f, 1f, 1f);
                    if (mouseHoversTalent(X, Y, mouseX, mouseY) && mouseInsideBG(bgX, bgY, mouseX, mouseY)) selectedTalent = talent;

                    Y += 32;
                }
            } else {
                Talent talent = hierarchy.getMainElement();
                boolean unlocked = talentsHolder.talentUnlocked(talent);
                boolean disabled = talentsHolder.isDisabled(talent);
                float color = talentsHolder.canBePurchased(talent) ? alphaPurchase : 0.5f;
                if (!unlocked) guiGraphics.setColor(color, color, color,1f);
                else if (disabled) guiGraphics.setColor(1f, 0.55f,0.55f,0.85f);
                ResourceLocation icon = talent.background().isPresent() ? talent.background().get() : TALENT_ICON;
                guiGraphics.blit(icon, X, y0, 0,0,24,24,24,24);
                ResourceLocation talentIcon = talent.icon();
                if (!unlocked) guiGraphics.setColor(color, color, color, 0.5f);
                else if (disabled) guiGraphics.setColor(1f, 0.55f,0.55f,0.85f);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                guiGraphics.blit(talentIcon, X + 2, y0 + 2,0,0,20,20,20,20);
                RenderSystem.disableBlend();
                guiGraphics.setColor(1f,1f,1f,1f);
                if (mouseHoversTalent(X, y0, mouseX, mouseY) && mouseInsideBG(bgX, bgY, mouseX, mouseY)) selectedTalent = talent;
            }
            X += 48;
        }
        this.redWisdomText = selectedTalent != null && selectedTalent.purchasable() && !talentsHolder.hasTalent(selectedTalent) && !talentsHolder.enoughWisdom(selectedTalent);
        guiGraphics.disableScissor();
        tryRenderTalentTooltip(talentsHolder, guiGraphics, font, (int) mouseX, (int) mouseY);
    }

    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, Font font) {
        if (!Screen.hasShiftDown()) guiGraphics.renderTooltip(font, Component.translatable(title), mouseX, mouseY);
        else {
            RenderBuffers.NO_TOOLTIP_GAP = true;
            int lines = 0;
            try {
                String amo = Component.translatable(title + ".lines").getString();
                lines = Integer.parseInt(amo);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) {
                List<Component> components = Lists.newArrayList();
                for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(title + "." + i, null, null));
                guiGraphics.renderComponentTooltip(font, components, mouseX, mouseY);
            }
        }
        guiGraphics.flush();
    }

    private boolean mouseHoversTalent(int x0, int y0, double mouseX, double mouseY) {
        return FantazicMath.within(x0, x0 + 24, mouseX) && FantazicMath.within(y0, y0 + 24, mouseY);
    }

    private boolean mouseInsideBG(int x0, int y0, double mouseX, double mouseY) {
        return FantazicMath.within(x0, x0 + TalentScreen.background, mouseX) && FantazicMath.within(y0, y0 + TalentScreen.background, mouseY);
    }

    @Nullable
    public Talent selectedTalent() {
        return selectedTalent;
    }

    private void tryRenderTalentTooltip(TalentsHolder holder, GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
        if (this.selectedTalent == null) return;
        List<Component> components = selectedTalent.buildTooltip();
        if (holder.isDisabled(selectedTalent)) components.add(Component.translatable("fantazia.gui.talent.disabled").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD, ChatFormatting.ITALIC));
        else if (holder.hasTalent(selectedTalent)) components.add(Component.translatable("fantazia.gui.talent.obtained").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD, ChatFormatting.ITALIC));
        guiGraphics.renderComponentTooltip(font, components, mouseX, mouseY);
    }

    private int calculateHierarchyHeight(IHierarchy<Talent> talentIHierarchy) {
        if (talentIHierarchy instanceof ChainHierarchy<Talent>) return 12 + talentIHierarchy.getElements().size() * 32;
        else return 0;
    }

    public int screenWidth() {
        return Math.max(160, HIERARCHIES.size() * 48);
    }

    public int screenHeight() {
        int finalHGT = 160;
        for (IHierarchy<Talent> talentIHierarchy : HIERARCHIES) {
            int height = calculateHierarchyHeight(talentIHierarchy);
            if (height > finalHGT) finalHGT = height;
        }
        return finalHGT;
    }

    public ResourceLocation getBackground() {
        return background;
    }

    public boolean redWisdomText() {
        return redWisdomText;
    }

    public record Builder(ResourceLocation icon, String title, Optional<ResourceLocation> background) {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("icon").forGetter(Builder::icon),
                    Codec.STRING.fieldOf("title").forGetter(Builder::title),
                    ResourceLocation.CODEC.optionalFieldOf("background").forGetter(Builder::background)
        ).apply(instance, Builder::new));

        public TalentTab build() {
                return new TalentTab(icon, title, background.orElse(Fantazia.location("textures/gui/talent/background_default.png")));
        }

        public TalentTabBuilderHolder holder(ResourceLocation id) {
            return new TalentTabBuilderHolder(id, this);
        }

        public void save(Consumer<TalentTabBuilderHolder> consumer, ResourceLocation id) {
            consumer.accept(holder(id));
        }
    }
}
