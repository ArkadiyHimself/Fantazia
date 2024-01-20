package net.arkadiyhimself.combatimprovement.Items.MagicCasters;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.util.KeyBinding;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

public class DashStone extends Item implements ICurioItem, Vanishable {
    private final KeyMapping keyBind = KeyBinding.DASH;
    public final int level;
    public DashStone(int level) {
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

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return ICurioItem.super.canEquip(slotContext, stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable(" "));
        List<Component> active = new ArrayList<>();
        List<Component> passive = new ArrayList<>();

        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.GOLD};
        ChatFormatting[] button = new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.RED};
        ChatFormatting[] ability = new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.DARK_RED};
        ChatFormatting[] list = new ChatFormatting[]{ChatFormatting.RED};

        if (this.level == 1) {
            WhereMagicHappens.Gui.addComponent(passive, "tooltip.combatimprovement.dashstone1.release.1", new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.WHITE}, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.active", text, ability, "Dash");
            active.add(Component.translatable(" "));
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone1.press.1", text, button, keyBind.getKey().getDisplayName().getString().toUpperCase());
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone1.press.2", text, null);
            active.add(Component.translatable(" "));
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone1.press.3", list, ability, "Dash");
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone1.press.4", list, ability, "Dash");
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone1.press.5", list, ability, "Dash");
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.recharge", list, ability, "Dash", "5");
        } else if (this.level == 2) {
            WhereMagicHappens.Gui.addComponent(passive, "tooltip.combatimprovement.dashstone2.release.1", new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.BLUE}, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.active", text, ability, "Ethereal Blink");
            active.add(Component.translatable(" "));
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone2.press.1", text, button, keyBind.getKey().getDisplayName().getString().toUpperCase());
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone2.press.2", text, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone2.press.3", text, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone2.press.4", text, null);
            active.add(Component.translatable(" "));
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone2.press.5", list, ability, "Blink");
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone2.press.6", list, ability, "Blink");
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone2.press.7", list, ability, "Blink");
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone2.press.8", list, ability, "Blink");
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.recharge", list, ability, "Blink", "5");
        } else if (this.level == 3) {
            WhereMagicHappens.Gui.addComponent(passive, "tooltip.combatimprovement.dashstone3.release.1", new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.LIGHT_PURPLE}, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.active", text, ability, "Inter-dimensional Leap");
            active.add(Component.translatable(" "));
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone3.press.1", text, button, keyBind.getKey().getDisplayName().getString().toUpperCase());
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone3.press.2", text, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone3.press.3", text, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone3.press.4", text, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone3.press.5", text, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone3.press.6", text, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone3.press.7", text, null);
            active.add(Component.translatable(" "));
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone3.press.8", list, ability, "Leap");
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone3.press.9", list, ability, "Leap");
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone3.press.10", list, ability, "Leap");
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.dashstone3.press.11", list, ability, "Leap");
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.recharge", list, ability, "Leap", "5");
        }
        if (Screen.hasShiftDown()) {
            pTooltipComponents.addAll(active);
        } else { pTooltipComponents.addAll(passive); }
    }
}
