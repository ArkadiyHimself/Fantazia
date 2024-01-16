package net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.Passive;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Registries.Items.ItemRegistry;
import net.arkadiyhimself.combatimprovement.util.Interfaces.IPassiveCaster;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PassiveCasters extends Item implements IPassiveCaster {
    protected final Component abilityName;
    private final SoundEvent castSound;
    public final float MANACOST;
    public final int RECHARGE;
    public PassiveCasters(Component abilityName, SoundEvent castSound, float manacost, int recharge) {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
        this.abilityName = abilityName;
        this.castSound = castSound;
        MANACOST = manacost;
        RECHARGE = recharge;
    }

    @Nullable
    @Override
    public SoundEvent getCastSound() {
        return this.castSound;
    }

    @Override
    public boolean hasCooldown(ServerPlayer player) {
        return player.getCooldowns().isOnCooldown(this);
    }

    @Override
    public boolean conditionNotMet(ServerPlayer player) {
        return false;
    }

    @Override
    public void passiveAbility(ServerPlayer player) {
        player.getCooldowns().addCooldown(this, RECHARGE);
        if (getCastSound() != null) {
            player.level.playSound(null, player.blockPosition(), getCastSound(), SoundSource.PLAYERS);
        }
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

        if (this == ItemRegistry.MYSTIC_MIRROR.get()) {
            WhereMagicHappens.Gui.addComponent(passive,"tooltip.combatimprovement.mystic_mirror.release.1", new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.DARK_RED}, null);
            WhereMagicHappens.Gui.addComponent(passive,"tooltip.combatimprovement.mystic_mirror.release.2", new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.DARK_RED}, null);

            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.passive", text, ability, abilityName);
            active.add(Component.translatable(" "));
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.mystic_mirror.press.1", text, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.mystic_mirror.press.2", text, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.mystic_mirror.press.3", text, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.mystic_mirror.press.4", text, null);
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.mystic_mirror.press.5", text, null);
            active.add(Component.translatable(" "));
            WhereMagicHappens.Gui.addComponent(active, "tooltip.combatimprovement.common.recharge", text, ability, abilityName, "10");
        }
        if (Screen.hasShiftDown()) {
            pTooltipComponents.addAll(active);
        } else { pTooltipComponents.addAll(passive); }
    }
}
