package net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.ActiveAndTargeted;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LifeDeathEntangler extends SpellCaster {
    public LifeDeathEntangler(SoundEvent castSound) {
        super(50, castSound, "entangler", Ability.SELF, 0, false, 0);
    }

    @Override
    public boolean conditionNotMet(ServerPlayer player) {
        return (!(player.getHealth() <= 2));
    }

    @Override
    public void activeAbility(@NotNull ServerPlayer player) {
        WhereMagicHappens.Abilities.addEffectWithoutParticles(player, MobEffectRegistry.ABSOLUTE_BARRIER.get(), 10);
        super.activeAbility(player);
    }



    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable(" "));
        List<Component> active = new ArrayList<>();
        List<Component> passive = new ArrayList<>();

        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
        ChatFormatting[] ability = new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.DARK_RED};
        ChatFormatting[] list = new ChatFormatting[]{ChatFormatting.BLUE};

        WhereMagicHappens.Gui.addComponent(passive, "tooltip.combatimprovement.entangler.release.1", new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.DARK_RED}, null);

        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.active", text, ability, abilityName);
        active.add(Component.translatable(" "));
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.entangler.press.1", text, null);
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.entangler.press.2", text, null);
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.entangler.press.3", text, null);
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.entangler.press.4", text, null);
        active.add(Component.translatable(" "));
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.recharge", text, ability, abilityName, (float) MAX_RECH / 20);
        active.add(Component.translatable(" "));
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.passives", new ChatFormatting[]{ChatFormatting.DARK_BLUE}, null);
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.entangler.passive.1", list, null);
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.entangler.passive.2", list, null);
        if (Screen.hasShiftDown()) {
            pTooltipComponents.addAll(active);
        } else { pTooltipComponents.addAll(passive); }
    }
}
