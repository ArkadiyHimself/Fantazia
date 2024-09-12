package net.arkadiyhimself.fantazia.api.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ITooltipBuilder {
    default List<Component> itemTooltip(@Nullable ItemStack stack) {
        return Lists.newArrayList();
    }
    default List<Component> buildIconTooltip() {
        return Lists.newArrayList();
    }
}
