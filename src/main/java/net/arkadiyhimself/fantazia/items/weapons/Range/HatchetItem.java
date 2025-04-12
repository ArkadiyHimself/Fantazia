package net.arkadiyhimself.fantazia.items.weapons.Range;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.renderers.PlayerAnimations;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HatchetItem extends TieredItem {
    private final float attackSpeedModifier;
    public HatchetItem(Tier pTier, float pAttackSpeedModifier) {
        super(pTier, new Properties());
        this.attackSpeedModifier = pAttackSpeedModifier;
    }
    @Override
    public boolean hurtEnemy(ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        pStack.hurtAndBreak(2, pAttacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        if (pLivingEntity instanceof LocalPlayer player) {
            int i = getUseDuration(pStack, pLivingEntity) - pRemainingUseDuration;
            if (i == 0) PlayerAnimations.animatePlayer(player, PlayerAnimations.WINDUP_START());
            else if (i == 14) PlayerAnimations.animatePlayer(player, PlayerAnimations.WINDUP_CONTINUE);
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, @NotNull LivingEntity livingEntity) {
        return 72000;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, int pTimeCharged) {
        super.releaseUsing(pStack, pLevel, pLivingEntity, pTimeCharged);
        if (pLivingEntity instanceof Player player) {

            int dur = getUseDuration(pStack, pLivingEntity) - pTimeCharged;
            float charge = getPowerForTime(dur);
            if (player instanceof LocalPlayer localPlayer) {
                IAnimation animation = PlayerAnimations.getAnimation(localPlayer);
                if (Objects.equals(animation, PlayerAnimations.WINDUP_START()) || animation == PlayerAnimations.WINDUP_CONTINUE) {
                    animation.tick();
                    PlayerAnimations.animatePlayer(localPlayer, (String) null);
                }
            }

            if (!pLevel.isClientSide() && charge > 0.2) {
                pStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                ThrownHatchet hatchetEnt = new ThrownHatchet(pLevel, player, pStack.copy(), charge);

                pLevel.addFreshEntity(hatchetEnt);
                pLevel.playSound(null, hatchetEnt, FTZSoundEvents.HATCHET_THROW.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                player.awardStat(Stats.ITEM_USED.get(pStack.getItem()));
                if (!player.hasInfiniteMaterials()) player.getInventory().removeItem(pStack);
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (pUsedHand == InteractionHand.OFF_HAND) return InteractionResultHolder.fail(itemstack);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            pPlayer.startUsingItem(pUsedHand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
            return UseAnim.CUSTOM;
    }

    public static float getPowerForTime(int pCharge) {
        float f = (float)pCharge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Fantazia.res("item.hatchet"), getTier().getAttackDamageBonus() + 2.5f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(Fantazia.res("item.hatchet"), attackSpeedModifier, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        return builder.build();
    }
}
