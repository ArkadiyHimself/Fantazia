package net.arkadiyhimself.fantazia.items.expendable;

import net.arkadiyhimself.fantazia.data.talent.TalentHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TalentProvidingItem extends Item {
    private final ResourceLocation talendID;
    public TalentProvidingItem(Rarity rarity, ResourceLocation talendID) {
        super(new Properties().fireResistant().rarity(rarity).stacksTo(1));
        this.talendID = talendID;
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            if (TalentHelper.hasTalent(serverPlayer, talendID)) return InteractionResultHolder.fail(itemstack);
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemstack);
            if (!serverPlayer.hasInfiniteMaterials()) itemstack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }
}
