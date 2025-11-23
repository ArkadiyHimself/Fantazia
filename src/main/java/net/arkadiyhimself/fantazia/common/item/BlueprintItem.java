package net.arkadiyhimself.fantazia.common.item;

import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.common.registries.custom.Blueprints;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlueprintItem extends Item implements ITooltipBuilder {

    public BlueprintItem() {
        super(new Properties().stacksTo(1).component(FTZDataComponentTypes.BLUEPRINT, Blueprints.EMPTY));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        Holder<Blueprint> blueprint = stack.get(FTZDataComponentTypes.BLUEPRINT);
        if (blueprint == null || blueprint.value().isEmpty()) return super.getName(stack);
        else return blueprint.value().getNameComponent();
    }

    @Override
    public List<Component> itemTooltip(ItemStack stack) {
        List<Component> components = Lists.newArrayList();
        components.addAll(getBlueprint(stack).value().buildTooltip());
        return components;
    }

    public Holder<Blueprint> getBlueprint(ItemStack stack) {
        Holder<Blueprint> blueprintHolder = stack.get(FTZDataComponentTypes.BLUEPRINT);
        return blueprintHolder == null ? Blueprints.EMPTY : blueprintHolder;
    }

    public static ItemStack blueprint(Holder<Blueprint> blueprint) {
        ItemStack stack = new ItemStack(FTZItems.BLUEPRINT.asItem());
        stack.set(FTZDataComponentTypes.BLUEPRINT, blueprint);
        return stack;
    }

    public static ItemStack emptyBlueprint() {
        return blueprint(Blueprints.EMPTY);
    }

    public static boolean isEmptyBlueprint(ItemStack stack) {
        Holder<Blueprint> blueprint = stack.get(FTZDataComponentTypes.BLUEPRINT);
        return blueprint != null && blueprint.value().isEmpty();
    }
}
