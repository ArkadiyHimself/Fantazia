package net.arkadiyhimself.fantazia.Items.weapons.Range;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.arkadiyhimself.fantazia.events.WhereMagicHappens;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.HatchetThrowC2S;
import net.arkadiyhimself.fantazia.client.models.PlayerAnimations;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class HatchetItem extends TieredItem {
    private final float attackDamage;
    private final float attackSpeed;
    private Multimap<Attribute, AttributeModifier> defaultModifiers;
    public HatchetItem(Tier pTier, float pAttackDamageModifier, float pAttackSpeedModifier, Item.Properties pProperties, String name) {
        super(pTier, pProperties);
        this.attackDamage = pAttackDamageModifier + pTier.getAttackDamageBonus();
        this.attackSpeed = pAttackSpeedModifier;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }
    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(2, pAttacker, (p_43296_) -> {
            p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }
    public float getThrowDamage() {
        return attackDamage + 1f;
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
        if (pLivingEntity instanceof LocalPlayer player) {
            int i = getUseDuration(pStack) - pRemainingUseDuration;
            if (i == 0) {
                WhereMagicHappens.Abilities.animatePlayer(player, PlayerAnimations.WINDUP_START());
            } else if (i == 15) {
                WhereMagicHappens.Abilities.animatePlayer(player, PlayerAnimations.WINDUP_CONTINUE);
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        super.releaseUsing(pStack, pLevel, pLivingEntity, pTimeCharged);
        if (pLivingEntity instanceof Player player) {
            int dur = getUseDuration(pStack) - pTimeCharged;
            float charge = getPowerForTime(dur);
            if (player instanceof LocalPlayer localPlayer) {
                IAnimation animation = WhereMagicHappens.Abilities.getAnimation(localPlayer);
                if (Objects.equals(animation, PlayerAnimations.WINDUP_START()) || animation == PlayerAnimations.WINDUP_CONTINUE) {
                    animation.tick();
                    WhereMagicHappens.Abilities.animatePlayer(localPlayer, (String) null);
                }
            }
            if (charge > 0.2 && pLevel.isClientSide) {
                Inventory inv = player.getInventory();
                List<NonNullList<ItemStack>> nonnulllist = ImmutableList.of(inv.items, inv.armor, inv.offhand);
                int j = -1;
                for(NonNullList<ItemStack> list : nonnulllist) {
                    for(int i = 0; i < list.size(); ++i) {
                        if (list.get(i) == pStack) {
                            j = i;
                            break;
                        }
                    }
                }
                NetworkHandler.sendToServer(new HatchetThrowC2S(pStack, j));
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
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
