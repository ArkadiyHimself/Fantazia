package net.arkadiyhimself.fantazia.items.expendable;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TalentGivingItem extends ExpendableItem {
    private final ResourceLocation talendID;
    public TalentGivingItem(Rarity rarity, ResourceLocation talendID) {
        super(rarity, 1);
        this.talendID = talendID;
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            ResourceLocation advID = talendID.withPrefix("ftz_talents/");
            Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(advID);
            if (advancement != null && serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) return InteractionResultHolder.fail(itemstack);
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemstack);
            if (!serverPlayer.getAbilities().instabuild) itemstack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }
}
