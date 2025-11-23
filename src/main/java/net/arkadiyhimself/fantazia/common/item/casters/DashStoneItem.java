package net.arkadiyhimself.fantazia.common.item.casters;

import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DashStoneItem extends Item {

    public DashStoneItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant().component(FTZDataComponentTypes.DASH_LEVEL,1));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull Item.TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        Integer level = pStack.get(FTZDataComponentTypes.DASH_LEVEL);
        if (level == null) return;

        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.literal(" "));

        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.GOLD};
        ChatFormatting[] ability = new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.DARK_RED};
        ChatFormatting[] list = new ChatFormatting[]{ChatFormatting.RED};

        int lines = 0;
        String basicPath = "dash.fantazia.dash" + level;

        if (!Screen.hasShiftDown()) {
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) for (int i = 1; i <= lines; i++) pTooltipComponents.add(GuiHelper.bakeComponent(basicPath + ".desc." + i, null, null));
            return;
        }
        // dash ident
        pTooltipComponents.add(GuiHelper.bakeComponent("tooltip.fantazia.common.dash.active", list, ability, Component.translatable(basicPath + ".name").getString()));

        String desc = Component.translatable(basicPath + ".lines").getString();
        try {
            lines = Integer.parseInt(desc);
        } catch (NumberFormatException ignored) {}
        if (lines > 0) {
            pTooltipComponents.add(Component.literal(" "));
            for (int i = 1; i <= lines; i++) pTooltipComponents.add(GuiHelper.bakeComponent(basicPath + "." + i, text, null));
        }

        String stats = Component.translatable(basicPath + ".stats.lines").getString();
        lines = 0;
        try {
            lines = Integer.parseInt(stats);
        } catch (NumberFormatException ignored) {}
        if (lines > 0) {
            pTooltipComponents.add(Component.literal(" "));
            for (int i = 1; i <= lines; i++) pTooltipComponents.add(GuiHelper.bakeComponent(basicPath + ".stats." + i, null, null));
        }
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
        Integer level = stack.get(FTZDataComponentTypes.DASH_LEVEL);
        return super.getDescriptionId() + (level == null ? "" : level);
    }

    public static ItemStack dashStone(int level) {
        ItemStack itemStack = new ItemStack(FTZItems.DASHSTONE.value());
        itemStack.update(FTZDataComponentTypes.DASH_LEVEL, 1, integer -> level);
        return itemStack;
    }
}
