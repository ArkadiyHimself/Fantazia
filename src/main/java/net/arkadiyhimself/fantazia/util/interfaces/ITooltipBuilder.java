package net.arkadiyhimself.fantazia.util.interfaces;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ITooltipBuilder {
    List<Component> buildTooltip(@Nullable ItemStack stack);
}
