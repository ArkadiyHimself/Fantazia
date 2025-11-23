package net.arkadiyhimself.fantazia.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.RenderBuffers;
import net.arkadiyhimself.fantazia.client.gui.TextComponents;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.common.world.inventory.AmplificationMenu;
import net.arkadiyhimself.fantazia.common.world.inventory.AmplifyInitialContainer;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AmplificationScreen extends AbstractContainerScreen<AmplificationMenu> {

    private static final ResourceLocation PLAYER_INVENTORY = Fantazia.location("textures/gui/container/player_inventory.png");

    private static AmplificationTab hovered = null;

    public AmplificationScreen(AmplificationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelY += 2;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderWisdomWidget(guiGraphics, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
        maybeRenderSubstanceSlotTooltip(guiGraphics, mouseX, mouseY);
        renderTabs(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(PLAYER_INVENTORY, i, j, 0, 0, this.imageWidth, this.imageHeight);
        guiGraphics.blit(menu.selected().screen(), i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component title = menu.selected().titleComponent();
        int width = font.width(title);
        this.titleLabelX = 27 - width / 2;
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY,11141290,false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hovered != null && hovered != menu.selected()) {
            FantazicUtil.playSoundUI(SoundEvents.UI_BUTTON_CLICK.value());
            menu.setTab(hovered);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderTabs(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        PoseStack poseStack = guiGraphics.pose();
        hovered = null;
        poseStack.pushPose();
        poseStack.translate(0,0, 200);
        for (int i = 0; i < AmplificationTab.TABS.size(); i++) {

            AmplificationTab tab = AmplificationTab.TABS.get(i);
            int x = leftPos + 5 + i * 28;
            int y = topPos - 26;
            boolean flag = tab == menu.selected();
            if (flag) y -= 2;
            ResourceLocation texture = flag ? tab.getSelectedTab() : tab.getUnselectedTab();
            if (FantazicMath.isWithin(x, x + 26, (int) mouseX) && FantazicMath.isWithin(y, topPos, (int) mouseY)) {
                hovered = tab;
                VisualHelper.renderTooltipNoHeading(guiGraphics, hovered.buildTooltip(), font, (int) mouseX, (int) mouseY);
                if (!flag) texture = tab.getSelectableTab();
            }

            guiGraphics.blitSprite(texture, 26, 32, 0, 0, x, y, 26, flag ? 31 : 26);
            guiGraphics.renderItem(tab.stack(), x + 5, topPos - 18);
        }
        poseStack.popPose();
    }

    private void renderWisdomWidget(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x0 = leftPos - 100 - 10;
        int y0 = topPos + 10;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(TalentScreen.WISDOM_WIDGET, x0, y0,0,0,100,30,100,30);
        guiGraphics.blit(TalentScreen.WISDOM_ICON, x0 + 10, y0 + 10, 0,0,10,10,10,10);
        RenderSystem.disableBlend();
        int wisdom = menu.getTalents().getWisdom();
        AmplifyResource resource = menu.wisdomResource();
        boolean redText = resource.notEnough();
        boolean greenText = resource.isEnough();

        ChatFormatting number = ChatFormatting.BLUE;
        if (greenText) number = ChatFormatting.GREEN;
        else if (redText) number = ChatFormatting.RED;
        Component component = Component.literal(String.valueOf(wisdom)).withStyle(number);
        if (FantazicMath.isWithin(x0, x0 + 100, mouseX) && FantazicMath.isWithin(y0, y0 + 30, mouseY)) {
            int lines = 0;
            String basicPath = "fantazia.gui.talent.wisdom";
            List<Component> components = Lists.newArrayList();

            boolean shift = Screen.hasShiftDown();
            if (redText) basicPath += ".not_enough";
            else if (shift) basicPath += ".desc";
            else components.add(Component.translatable(basicPath).withStyle(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD));

            String desc = Component.translatable(basicPath + ".lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) {
                for (int i = 1; i <= lines; i++) components.add(Component.translatable(basicPath + "." + i).withStyle(redText ? ChatFormatting.RED : ChatFormatting.BLUE));
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0,0,500);
                if (shift || redText) {
                    if (Fantazia.loadedJEI() && !redText) components.add(TextComponents.JEI_PRESS_TO_SEE_WISDOM_REWARDS);
                    VisualHelper.renderTooltipNoHeading(guiGraphics, components, font, mouseX, mouseY);
                } else {
                    components.add(TextComponents.HOLD_SHIFT_TO_SEE_MORE_COMPONENT);
                    guiGraphics.renderComponentTooltip(font, components, mouseX, mouseY);
                }
                guiGraphics.pose().popPose();
            }
        }

        guiGraphics.drawString(font, component, x0 + 24, y0 + 11, 0);
    }

    @Override
    protected void renderSlotContents(@NotNull GuiGraphics guiGraphics, @NotNull ItemStack itemstack, @NotNull Slot slot, @Nullable String countString) {
        if (slot.getSlotIndex() == 1 && slot.container instanceof AmplifyInitialContainer) RenderBuffers.AMPLIFY_ITEM_STACK_COUNT = menu.substanceResource();
        super.renderSlotContents(guiGraphics, itemstack, slot, countString);
    }

    @Override
    protected @NotNull List<Component> getTooltipFromContainerItem(@NotNull ItemStack stack) {
        if (hoveredSlot != null && hoveredSlot.container instanceof AmplifyInitialContainer && hoveredSlot.getSlotIndex() == 1 && menu.substanceResource().notEnough()) {
            return List.of(Component.translatable("container.fantazia.amplification.not_enough_substance").withStyle(ChatFormatting.RED));
        } else return super.getTooltipFromContainerItem(stack);
    }

    private void maybeRenderSubstanceSlotTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (hoveredSlot != null && hoveredSlot.container instanceof AmplifyInitialContainer
                && hoveredSlot.getSlotIndex() == 1 && hoveredSlot.getItem().isEmpty() && menu.substanceResource().notEnough())
            guiGraphics.renderTooltip(font, Component.translatable("container.fantazia.amplification.substance_required").withStyle(ChatFormatting.RED), x, y);
    }
}
