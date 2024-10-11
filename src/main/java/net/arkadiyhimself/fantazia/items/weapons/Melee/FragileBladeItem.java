package net.arkadiyhimself.fantazia.items.weapons.Melee;

import com.google.common.collect.Lists;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.data_component.HiddenPotentialHolder;
import net.arkadiyhimself.fantazia.api.type.item.ITooltipBuilder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.registries.FTZDataComponentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FragileBladeItem extends MeleeWeaponItem implements ITooltipBuilder {

    public FragileBladeItem() {
        super(new Properties().stacksTo(1).durability(1024).component(FTZDataComponentTypes.HIDDEN_POTENTIAL, new HiddenPotentialHolder()),-1.5f, 4, "fragile_blade");
    }

    @Override
    public void activeAbility(ServerPlayer player) {}

    @Override
    public List<Component> itemTooltip(@Nullable ItemStack stack) {
        List<Component> components = Lists.newArrayList();
        if (stack == null) return components;
        HiddenPotentialHolder hiddenPotentialHolder = stack.get(FTZDataComponentTypes.HIDDEN_POTENTIAL);
        if (hiddenPotentialHolder == null) return components;
        String basicPath = "weapon.fantazia.hidden_potential";
        int lines;

        if (!Screen.hasShiftDown()) {
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {
                return components;
            }

            ChatFormatting[] noShift = new ChatFormatting[]{ChatFormatting.RED};
            for (int i = 1; i <= lines; i++)
                components.add(GuiHelper.bakeComponent(basicPath + ".desc." + i, noShift, null));

            components.add(Component.literal(" "));
            components.add(GuiHelper.bakeComponent(basicPath + ".current_damage", noShift, hiddenPotentialHolder.getFormatting(), hiddenPotentialHolder.getDamage() + this.getDamage() + 1));
            return components;
        }

        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.weapon", new ChatFormatting[]{ChatFormatting.DARK_PURPLE}, new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD}, Component.translatable("weapon.fantazia.hidden_potential.name").getString()));
        components.add(Component.literal(" "));
        String text = Component.translatable(basicPath + ".lines").getString();

        try {
            lines = Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return components;
        }

        ChatFormatting[] main = new ChatFormatting[]{ChatFormatting.GOLD};
        for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + "." + i, main, null));

        components.add(Component.literal(" "));
        components.add(GuiHelper.bakeComponent(basicPath + ".minimal_damage", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD}, hiddenPotentialHolder.minDMG() + this.getDamage() + 1));
        components.add(GuiHelper.bakeComponent(basicPath + ".maximum_damage", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD}, hiddenPotentialHolder.maxDMG() + this.getDamage() + 1));

        return components;
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(Fantazia.res(this.defaultName), this.attackSpeedModifier, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);

        HiddenPotentialHolder hiddenPotentialHolder = stack.get(FTZDataComponentTypes.HIDDEN_POTENTIAL);
        if (hiddenPotentialHolder == null) return builder.build();

        float bonusDMG = getDamage() + hiddenPotentialHolder.getDamage();
        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Fantazia.res(this.defaultName), bonusDMG, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        return builder.build();
    }
}
