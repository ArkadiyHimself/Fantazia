package net.arkadiyhimself.fantazia.items.casters;

import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
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
    public final int level;
    public DashStoneItem(int level) {
        super(getDefaultProperties());
        this.level = level;
    }
    public static Properties getDefaultProperties() {
        Properties props = new Properties();

        props.stacksTo(1);
        props.rarity(Rarity.RARE);
        props.fireResistant();

        return props;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull Item.TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
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
        // dash name
        pTooltipComponents.add(GuiHelper.bakeComponent("tooltip.fantazia.common.active", list, ability, Component.translatable(basicPath + ".name").getString()));

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
}
