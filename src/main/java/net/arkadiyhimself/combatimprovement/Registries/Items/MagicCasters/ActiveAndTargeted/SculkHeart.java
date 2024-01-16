package net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.ActiveAndTargeted;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SculkHeart extends SpellCaster {
    public SculkHeart() {
        super(200, SoundEvents.WARDEN_SONIC_BOOM, "heart_of_sculk", Ability.TARGETED, 12, true, 4.5f);
    }

    @Override
    public boolean targetConditions(ServerPlayer player, LivingEntity target) {
        return true;
    }
    @Override
    public boolean targetedAbility(LivingEntity caster, LivingEntity target, boolean wasDeflected) {
        boolean flag = super.targetedAbility(caster, target, wasDeflected);
        WhereMagicHappens.Abilities.rayOfParticles(caster, target, ParticleTypes.SONIC_BOOM);
        if (!flag) { return false; }
        target.hurt(new EntityDamageSource("sonic_boom", caster).bypassArmor().bypassEnchantments().setMagic(), 15f);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable(" "));
        List<Component> active = new ArrayList<>();
        List<Component> passive = new ArrayList<>();

        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
        ChatFormatting[] ability = new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.DARK_RED};
        ChatFormatting[] list = new ChatFormatting[]{ChatFormatting.BLUE};

        WhereMagicHappens.Gui.addComponent(passive,"tooltip.combatimprovement.heart_of_sculk.release.1", new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.DARK_RED}, null);
        passive.add(Component.translatable(" "));
        WhereMagicHappens.Gui.addComponent(passive,"tooltip.combatimprovement.common.passives", new ChatFormatting[]{ChatFormatting.DARK_BLUE}, null);
        WhereMagicHappens.Gui.addComponent(passive, "tooltip.combatimprovement.heart_of_sculk.buff", new ChatFormatting[]{ChatFormatting.BLUE}, null);

        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.targeted", text, ability, abilityName);
        active.add(Component.translatable(" "));
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.targeted.desc", text, null);
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.heart_of_sculk.press.1", text, null);
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.heart_of_sculk.press.2", text, null);
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.heart_of_sculk.press.3", text, null);
        active.add(Component.translatable(" "));
        WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.recharge", text, ability, abilityName, "10");

        if (Screen.hasShiftDown()) {
            pTooltipComponents.addAll(active);
        } else { pTooltipComponents.addAll(passive); }
    }
}
