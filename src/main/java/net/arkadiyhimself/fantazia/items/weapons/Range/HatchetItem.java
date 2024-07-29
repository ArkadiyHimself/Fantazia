package net.arkadiyhimself.fantazia.items.weapons.Range;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.arkadiyhimself.fantazia.client.models.PlayerAnimations;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HatchetItem extends TieredItem {
    private final float attackDamage;
    private final float attackSpeed;
    private Multimap<Attribute, AttributeModifier> defaultModifiers;
    public HatchetItem(Tier pTier, float pAttackSpeedModifier, Item.Properties pProperties) {
        super(pTier, pProperties);
        this.attackDamage = 2.5f + pTier.getAttackDamageBonus();
        this.attackSpeed = pAttackSpeedModifier;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }
    @Override
    public boolean hurtEnemy(ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        pStack.hurtAndBreak(2, pAttacker, (p_43296_) -> p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }
    public float getThrowDamage() {
        return attackDamage + 1f;
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
        if (pLivingEntity instanceof LocalPlayer player) {
            int i = getUseDuration(pStack) - pRemainingUseDuration;
            if (i == 0) PlayerAnimations.animatePlayer(player, PlayerAnimations.WINDUP_START());
            else if (i == 14) PlayerAnimations.animatePlayer(player, PlayerAnimations.WINDUP_CONTINUE);
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, int pTimeCharged) {
        super.releaseUsing(pStack, pLevel, pLivingEntity, pTimeCharged);
        if (pLivingEntity instanceof Player player) {
            int dur = getUseDuration(pStack) - pTimeCharged;
            float charge = getPowerForTime(dur);
            if (player instanceof LocalPlayer localPlayer) {
                IAnimation animation = PlayerAnimations.getAnimation(localPlayer);
                if (Objects.equals(animation, PlayerAnimations.WINDUP_START()) || animation == PlayerAnimations.WINDUP_CONTINUE) {
                    animation.tick();
                    PlayerAnimations.animatePlayer(localPlayer, (String) null);
                }
            }
            if (!pLevel.isClientSide() && charge > 0.2) {
                pStack.hurtAndBreak(1, player, (player1) -> player1.broadcastBreakEvent(player.getUsedItemHand()));
                ThrownHatchet hatchetEnt = new ThrownHatchet(pLevel, player, pStack.copy(), charge);

                pLevel.addFreshEntity(hatchetEnt);
                pLevel.playSound(null, hatchetEnt, FTZSoundEvents.HATCHET_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.awardStat(Stats.ITEM_USED.get(pStack.getItem()));
                if (!player.getAbilities().instabuild) player.getInventory().removeItem(pStack);
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
    public UseAnim getUseAnimation(ItemStack pStack) {
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
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getAttributeModifiers(slot, stack);
    }

}
