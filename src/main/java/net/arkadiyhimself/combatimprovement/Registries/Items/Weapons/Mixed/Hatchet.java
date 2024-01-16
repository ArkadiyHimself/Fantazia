package net.arkadiyhimself.combatimprovement.Registries.Items.Weapons.Mixed;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Networking.packets.HatchetThrowC2S;
import net.arkadiyhimself.combatimprovement.Registries.Entities.HatchetEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class Hatchet extends TieredItem {
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    public Hatchet(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Item.Properties pProperties, String name) {
        super(pTier, pProperties);
        this.attackDamage = (float) pAttackDamageModifier + pTier.getAttackDamageBonus();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)pAttackSpeedModifier, AttributeModifier.Operation.ADDITION));
        WhereMagicHappens.Gui.itemIcons.put(this, new ResourceLocation(CombatImprovement.MODID, "textures/item/iconformodels/" + name));
        this.defaultModifiers = builder.build();
    }
    public float getDamage() {
        return this.attackDamage;
    }
    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(2, pAttacker, (p_43296_) -> {
            p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        return super.useOn(pContext);
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
            if (charge > 0.2 && pLevel.isClientSide) {
                NetworkHandler.sendToServer(new HatchetThrowC2S(pStack));
                if (!player.getAbilities().instabuild) {
                    player.getInventory().removeItem(pStack);
                }
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
            return UseAnim.SPEAR;
    }

    public static float getPowerForTime(int pCharge) {
        float f = (float)pCharge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }
}
