package net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.UsefulMethods;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SoulEater extends SpellCasters {
    public SoulEater(SoundEvent castSound) {
        super(300, castSound, Component.translatable("Devour").withStyle(ChatFormatting.BOLD, ChatFormatting.LIGHT_PURPLE), Ability.TARGETED, 6);
    }
    @Override
    public boolean targetConditions(ServerPlayer player, LivingEntity target) {
        return target != null && target.getHealth() <= 100 && !(target instanceof Player) ;
    }
    @Override
    public void targetedAbility(@Nullable ServerPlayer player, LivingEntity target) {
        if (!targetConditions(player, target) || (player != null && hasCooldown(player))) { return; }
        if (player != null) {
            float healing = target.getMobType() == MobType.UNDEAD ? target.getHealth() / 8 : target.getHealth() / 4;
            player.heal(healing);
            int devour = (int) (target.getHealth() / 4);
            int hunger = 20 - player.getFoodData().getFoodLevel();
            int food;
            int saturation;
            if (hunger >= devour) {
                food = devour;
                saturation = 0;
            } else {
                food = hunger;
                saturation = devour - hunger;
            }
            player.getFoodData().eat(food, saturation);
            UsefulMethods.Abilities.addEffectWithoutParticles(player, MobEffectRegistry.BARRIER.get(),  (int) target.getHealth() * 10, (int) target.getHealth() / 4 - 1);
            UsefulMethods.Abilities.addEffectWithoutParticles(player, MobEffectRegistry.MIGTH.get(), (int) target.getHealth() * 10, (int) target.getHealth() / 4 - 1);
        }
        UsefulMethods.Abilities.dropExperience(target, 5);
        this.MAX_RECH = (int) target.getHealth() * 40;
        int particles = switch (Minecraft.getInstance().options.particles().get()) {
            case MINIMAL -> 15;
            case DECREASED -> 30;
            case ALL -> 45;
        };
        for (int i = 0; i < particles; ++i) {
            UsefulMethods.Abilities.createRandomParticleOnHumanoid(target, ParticleTypes.SMOKE, UsefulMethods.Abilities.ParticleMovement.REGULAR);
        }
        int flameParts = particles / 2;
        for (int i = 0; i < flameParts; ++i) {
            UsefulMethods.Abilities.createRandomParticleOnHumanoid(target, ParticleTypes.FLAME, UsefulMethods.Abilities.ParticleMovement.REGULAR);
        }
        target.remove(Entity.RemovalReason.KILLED);
        super.targetedAbility(player, target);
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

        UsefulMethods.Gui.addComponent(passive,"tooltip.combatimprovement.soul_eater.release.1", new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.DARK_RED}, null);
        passive.add(Component.translatable(" "));
        UsefulMethods.Gui.addComponent(passive,"tooltip.combatimprovement.common.passives", new ChatFormatting[]{ChatFormatting.DARK_BLUE}, null);
        UsefulMethods.Gui.addComponent(passive, "tooltip.combatimprovement.soul_eater.debuff", new ChatFormatting[]{ChatFormatting.RED}, null);

        UsefulMethods.Gui.addComponent(active, "tooltip.combatimprovement.common.targeted", text, ability, abilityName);
        active.add(Component.translatable(" "));
        UsefulMethods.Gui.addComponent(active, "tooltip.combatimprovement.soul_eater.press.1", text, null);
        UsefulMethods.Gui.addComponent(active, "tooltip.combatimprovement.soul_eater.press.2", text, null);
        UsefulMethods.Gui.addComponent(active, "tooltip.combatimprovement.soul_eater.press.3", text, null);
        active.add(Component.translatable(" "));
        UsefulMethods.Gui.addComponent(active,  "tooltip.combatimprovement.soul_eater.buffs.1", list, null);
        UsefulMethods.Gui.addComponent(active,  "tooltip.combatimprovement.soul_eater.buffs.2", list, null);
        UsefulMethods.Gui.addComponent(active,  "tooltip.combatimprovement.soul_eater.buffs.3", list, null);
        UsefulMethods.Gui.addComponent(active,  "tooltip.combatimprovement.soul_eater.buffs.4", list, null);
        UsefulMethods.Gui.addComponent(active,  "tooltip.combatimprovement.soul_eater.buffs.5", list, null);
        UsefulMethods.Gui.addComponent(active,  "tooltip.combatimprovement.soul_eater.nerfs.1", new ChatFormatting[]{ChatFormatting.RED}, null);
        active.add(Component.translatable(" "));
        UsefulMethods.Gui.addComponent(active, "tooltip.combatimprovement.soul_eater.press.4", text, ability, abilityName);
        UsefulMethods.Gui.addComponent(active, "tooltip.combatimprovement.soul_eater.press.5", text, ability, "200");
        if (Screen.hasShiftDown()) {
            pTooltipComponents.addAll(active);
        } else { pTooltipComponents.addAll(passive); }
    }
}
