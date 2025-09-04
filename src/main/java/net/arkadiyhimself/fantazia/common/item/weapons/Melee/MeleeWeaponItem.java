package net.arkadiyhimself.fantazia.common.item.weapons.Melee;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.item.ITooltipBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

public abstract class MeleeWeaponItem extends Item implements ITooltipBuilder {

    public final String defaultName;
    protected float attackDamage;
    protected float attackSpeedModifier;

    public MeleeWeaponItem(Properties pProperties, float pAttackSpeedModifier, float attackDamage, String name) {
        super(pProperties);
        this.attackDamage = attackDamage;
        this.attackSpeedModifier = pAttackSpeedModifier;
        this.defaultName = name;
    }

    public abstract void activeAbility(ServerPlayer player);

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Fantazia.location(this.defaultName), this.getDamage(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(Fantazia.location(this.defaultName), this.attackSpeedModifier, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        return builder.build();
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(itemAbility);
    }

    public float getDamage() {
        return this.attackDamage;
    }

    public boolean canAttackBlock(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, Player pPlayer) {
        return !pPlayer.isCreative();
    }

    public boolean mineBlock(@NotNull ItemStack pStack, @NotNull Level pLevel, BlockState pState, @NotNull BlockPos pPos, @NotNull LivingEntity pEntityLiving) {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) pStack.hurtAndBreak(2, pEntityLiving, EquipmentSlot.MAINHAND);
        return true;
    }

    public boolean hurtEnemy(ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, EquipmentSlot.MAINHAND);
        return true;
    }

    public boolean hasActive() {
        return false;
    }
}
