package net.arkadiyhimself.fantazia.common.item;

import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EnderPocketItem extends Item {

    public EnderPocketItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        PlayerEnderChestContainer chestContainer = player.getEnderChestInventory();
        player.openMenu(new SimpleMenuProvider((containerId, inventory, container) ->
                ChestMenu.threeRows(containerId, inventory, chestContainer), Component.translatable("container.enderchest")));
        if (player.level().isClientSide()) FantazicUtil.playSoundUI(SoundEvents.ENDER_CHEST_OPEN);
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
    }
}
