package net.arkadiyhimself.fantazia.common.item;

import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.common.api.data_component.WisdomTransferComponent;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class WisdomCatcherItem extends Item {

    public WisdomCatcherItem() {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC).component(FTZDataComponentTypes.WISDOM_TRANSFER, WisdomTransferComponent.ABSORB));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return stack.get(FTZDataComponentTypes.WISDOM_TRANSFER) == null ? super.getUseAnimation(stack) : UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        WisdomTransferComponent component = stack.get(FTZDataComponentTypes.WISDOM_TRANSFER);
        if (component == null || !(livingEntity instanceof ServerPlayer player)) return;
        int i = this.getUseDuration(stack, livingEntity) - remainingUseDuration;
        if (i <= 2) return;
        if (component == WisdomTransferComponent.ABSORB) {
            int finalRate = getFinalRateAbsorb(i);
            PlayerAbilityHelper.acceptConsumer(player, TalentsHolder.class, talentsHolder -> talentsHolder.convertExpIntoWisdom(finalRate));
        }
        else {
            int finalRate = getFinalRateRelease(i);
            PlayerAbilityHelper.acceptConsumer(player, TalentsHolder.class, talentsHolder -> talentsHolder.convertWisdomIntoExp(finalRate));
        }
    }

    private static int getFinalRateAbsorb(int i) {
        int rate;
        if (i <= 10) rate = 1;
        else if (i <= 25) {
            rate = 2;
        } else if (i <= 40) {
            rate = 4;
        } else if (i <= 60) {
            rate = 6;
        } else if (i <= 80) {
            rate = 8;
        } else if (i <= 100) {
            rate = 12;
        } else if (i <= 150) {
            rate = 16;
        } else if (i <= 175) {
            rate = 20;
        } else if (i <= 200) {
            rate = 25;
        } else if (i <= 225) {
            rate = 30;
        } else if (i <= 250) {
            rate = 40;
        } else rate = 50;

        return rate;
    }

    private static int getFinalRateRelease(int i) {
        int rate;
        if (i <= 10) rate = 1;
        else if (i <= 40) {
            rate = 2;
        } else if (i <= 80) {
            rate = 4;
        } else if (i <= 160) {
            rate = 6;
        } else rate = 8;

        return rate;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        WisdomTransferComponent component = stack.get(FTZDataComponentTypes.WISDOM_TRANSFER);
        if (component == null) return InteractionResultHolder.fail(stack);
        if (pPlayer.isCrouching()) {
            WisdomTransferComponent next = component.nextOne();
            stack.set(FTZDataComponentTypes.WISDOM_TRANSFER, next);
            if (pPlayer instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, next.getSoundEvent().value());

            return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide());
        }
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        WisdomTransferComponent component = stack.get(FTZDataComponentTypes.WISDOM_TRANSFER);
        if (component == null) return;

        String basicString = "component.fantazia.wisdom_transfer.";
        String toggleString = "component.fantazia.wisdom_transfer.toggle.";

        if (!Screen.hasShiftDown()) {
            int toggleLines = 0;
            try {
                String string = Component.translatable(toggleString + "lines").getString();
                toggleLines = Integer.parseInt(string);
            } catch (NumberFormatException ignored) {
            }

            if (toggleLines > 0) {
                tooltipComponents.add(Component.literal(" "));

                for (int i = 1; i <= toggleLines; i++) {
                    tooltipComponents.add(Component.translatable(toggleString + i).withStyle(ChatFormatting.BLUE));
                }
            }
            return;
        }

        tooltipComponents.add(Component.literal(" "));
        String currentMode = basicString + component.getSerializedName();
        tooltipComponents.add(GuiHelper.bakeComponent(basicString + "current_mode", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, component.getFormattings(), Component.translatable(currentMode)));

        int descLines = 0;

        try {
            String string = Component.translatable(currentMode + ".lines").getString();
            descLines = Integer.parseInt(string);
        } catch (NumberFormatException ignored) {}

        if (descLines > 0) {
            tooltipComponents.add(Component.literal(" "));

            for (int i = 1; i <= descLines; i++) {
                tooltipComponents.add(Component.translatable(currentMode + "." + i).withStyle(ChatFormatting.GOLD));
            }
        }
    }

}
