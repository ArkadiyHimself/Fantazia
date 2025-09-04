package net.arkadiyhimself.fantazia.common.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.common.registries.custom.Runes;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public final class RuneWielderItem extends Item implements ITooltipBuilder, ICurioItem {

    public RuneWielderItem() {
        super(new Properties().stacksTo(1).component(FTZDataComponentTypes.RUNE, Runes.EMPTY));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        Holder<Rune> runeHolder = stack.get(FTZDataComponentTypes.RUNE);
        if (runeHolder == null || runeHolder.value().isEmpty()) return super.getName(stack);
        else return runeHolder.value().getNameComponent();
    }

    @Override
    public List<Component> itemTooltip(ItemStack stack) {
        List<Component> components = Lists.newArrayList();
        components.addAll(getRune(stack).value().buildTooltip());
        return components;
    }

    public static ItemStack rune(Holder<Rune> rune) {
        ItemStack stack = new ItemStack(FTZItems.RUNE_WIELDER.asItem());
        stack.set(FTZDataComponentTypes.RUNE, rune);
        return stack;
    }

    public static ItemStack emptyRune() {
        return rune(Runes.EMPTY);
    }

    public static boolean isEmptyRune(ItemStack stack) {
        Holder<Rune> runeHolder = stack.get(FTZDataComponentTypes.RUNE);
        return runeHolder != null && runeHolder.value().isEmpty();
    }

    @Override
    public int getLootingLevel(SlotContext slotContext, @Nullable LootContext lootContext, ItemStack stack) {
        return ICurioItem.super.getLootingLevel(slotContext, lootContext, stack) + getRune(stack).value().looting();
    }

    @Override
    public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack stack) {
        return ICurioItem.super.getFortuneLevel(slotContext, lootContext, stack) + getRune(stack).value().fortune();
    }

    public Holder<Rune> getRune(ItemStack stack) {
        Holder<Rune> runeHolder = stack.get(FTZDataComponentTypes.RUNE);
        return runeHolder == null ? Runes.EMPTY : runeHolder;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifierMultimap = ArrayListMultimap.create();
        Rune rune = getRune(stack).value();
        rune.getAttributeModifiers().forEach(modifierMultimap::put);
        return modifierMultimap;
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        return Lists.newArrayList();
    }
}
