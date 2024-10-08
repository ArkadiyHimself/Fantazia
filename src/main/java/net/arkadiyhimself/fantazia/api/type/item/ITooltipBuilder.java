package net.arkadiyhimself.fantazia.api.type.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface ITooltipBuilder {
    default List<Component> itemTooltip(@Nullable ItemStack stack) {
        return Lists.newArrayList();
    }
    default List<Component> buildIconTooltip() {
        return Lists.newArrayList();
    }
}
