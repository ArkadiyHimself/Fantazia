package net.arkadiyhimself.fantazia.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface ITooltipBuilder {

    default List<Component> itemTooltip(ItemStack stack) {
        return Lists.newArrayList();
    }

    default List<Component> buildTooltip() {
        return Lists.newArrayList();
    }
}
