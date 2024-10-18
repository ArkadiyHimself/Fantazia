package net.arkadiyhimself.fantazia.data.talent.types;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.arkadiyhimself.fantazia.api.type.item.ITooltipBuilder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.data.talent.TalentDataException;
import net.arkadiyhimself.fantazia.data.talent.TalentTreeData;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface ITalent extends ITooltipBuilder {

    BasicProperties getProperties();

    default void applyModifiers(@NotNull Player player) {};

    default void removeModifiers(@NotNull Player player) {};

    default ResourceLocation getID() throws TalentDataException {
        return getProperties().identifier();
    }

    default IHierarchy<ITalent> getHierarchy() {
        return TalentTreeData.getTalentToHierarchy().get(getID());
    }

    default @Nullable ITalent getParent() {
        return this.getHierarchy().getParent(this);
    }

    default boolean toBePurchased() {
        return getProperties().wisdom() > 0;
    }

    @Override
    default List<Component> buildIconTooltip() {
        List<Component> components = Lists.newArrayList();
        int amo = 0;
        if (Screen.hasShiftDown()) {
            if (this.toBePurchased()) components.add(GuiHelper.bakeComponent("fantazia.gui.talent.wisdom_cost", new ChatFormatting[]{ChatFormatting.BLUE}, new ChatFormatting[]{ChatFormatting.DARK_PURPLE}, getProperties().wisdom()));
            else {
                String criteria = Component.translatable(getProperties().title() + ".criteria.lines").getString();
                try {
                    amo = Integer.parseInt(criteria);
                } catch (NumberFormatException ignored) {}
                if (amo > 0) {
                    components.add(GuiHelper.bakeComponent("fantazia.gui.talent.requirement", new ChatFormatting[]{ChatFormatting.DARK_PURPLE}, null));
                    for (int i = 1; i <= amo; i++) components.add(GuiHelper.bakeComponent(getProperties().title() + ".criteria." + i, new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, null));
                }
            }
            return components;
        }
        components.add(GuiHelper.bakeComponent(getProperties().title() + ".name", null, null));
        String desc = Component.translatable(getProperties().title() + ".desc.lines").getString();
        try {
            amo = Integer.parseInt(desc);
        } catch (NumberFormatException ignored) {}

        if (amo > 0) for (int i = 1; i <= amo; i++) components.add(GuiHelper.bakeComponent(getProperties().title() + ".desc." + i, new ChatFormatting[]{ChatFormatting.GOLD},null));
        return components;
    }

    record BasicProperties(ResourceLocation identifier, ResourceLocation iconTexture, String title, int wisdom, @Nullable ResourceLocation advancement, ImmutableList<ResourceKey<DamageType>> damageImmunities, ImmutableMap<ResourceKey<DamageType>, Float> damageMultipliers) {

        public BasicProperties(ResourceLocation identifier, ResourceLocation iconTexture, String title, int wisdom, @Nullable ResourceLocation advancement, List<ResourceKey<DamageType>> damageImmunities, Map<ResourceKey<DamageType>, Float> damageMultipliers) {
            this(identifier, iconTexture, title, wisdom, advancement, damageImmunities.isEmpty() ? ImmutableList.of() : ImmutableList.copyOf(damageImmunities), damageMultipliers.isEmpty() ? ImmutableMap.of() : ImmutableMap.copyOf(damageMultipliers));
        }

        public boolean containsImmunityTo(Holder<DamageType> holder) {
            return damageImmunities.contains(holder.getKey());
        }

        public float getDamageMultiplier(@NotNull Holder<DamageType> resourceKey) {
            Float multiplier = damageMultipliers.get(resourceKey.getKey());
            return multiplier == null ? 1f : multiplier;
        }
    }
}
