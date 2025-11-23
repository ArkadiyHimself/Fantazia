package net.arkadiyhimself.fantazia.data.talent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.common.api.AttributeModifierBuilder;
import net.arkadiyhimself.fantazia.common.item.ITooltipBuilder;
import net.arkadiyhimself.fantazia.data.FTZCodecs;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.talent.TalentBuilderHolder;
import net.arkadiyhimself.fantazia.data.predicate.DamageTypePredicate;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public record Talent(ResourceLocation id, ResourceLocation icon, String title, int wisdom, ImmutableList<DamageTypePredicate> damageImmunities, List<Pair<Float, DamageTypePredicate>> damageMultipliers, ImmutableMap<Holder<Attribute>, AttributeModifier> attributeModifiers, ImmutableMap<String, Integer> curioModifiers, ImmutableList<TalentImpact> impacts, Optional<ResourceLocation> advancement, Optional<ResourceLocation> background) implements ITooltipBuilder {

    public void applyModifiers(@NotNull ServerPlayer player) {
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : attributeModifiers().entrySet()) {
            AttributeInstance instance = player.getAttribute(entry.getKey());
            AttributeModifier modifier = entry.getValue();
            if (instance == null) continue;
            if (Fantazia.DEVELOPER_MODE) Fantazia.LOGGER.info("Applying attribute modifier{}, {}", entry.getKey().value().getDescriptionId(), modifier);
            instance.removeModifier(modifier);
            instance.addPermanentModifier(modifier);
        }

        ICuriosItemHandler curiosItemHandler = CuriosApi.getCuriosInventory(player).orElse(null);
        if (curiosItemHandler != null) {
            for (Map.Entry<String, Integer> entry : curioModifiers().entrySet()) {
                Optional<ICurioStacksHandler> optional = curiosItemHandler.getStacksHandler(entry.getKey());
                if (optional.isEmpty()) return;
                ICurioStacksHandler handler = optional.get();

                handler.removeModifier(id);
                handler.addPermanentModifier(new AttributeModifier(id, entry.getValue(), AttributeModifier.Operation.ADD_VALUE));
                handler.update();
            }
        }
    }

    public void applyImpacts(@NotNull Player player) {
        for (TalentImpact impact : impacts) impact.apply(player);
    }

    public void removeModifiers(@NotNull ServerPlayer player) {
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : attributeModifiers().entrySet()) {
            AttributeInstance instance = player.getAttribute(entry.getKey());
            if (instance == null) continue;
            if (Fantazia.DEVELOPER_MODE) Fantazia.LOGGER.info("Removing attribute modifier{}, {}", entry.getKey().value().getDescriptionId(), instance.getValue());
            instance.removeModifier(entry.getValue());
        }

        ICuriosItemHandler curiosItemHandler = CuriosApi.getCuriosInventory(player).orElse(null);
        if (curiosItemHandler != null) {
            for (Map.Entry<String, Integer> entry : curioModifiers().entrySet()) {
                Optional<ICurioStacksHandler> optional = curiosItemHandler.getStacksHandler(entry.getKey());
                if (optional.isEmpty()) return;
                ICurioStacksHandler handler = optional.get();

                handler.removeModifier(id);
                handler.update();
            }
        }
    }

    public boolean canBeDisabled() {
        return !impacts.isEmpty();
    }

    public void removeImpacts(@NotNull Player player) {
        for (TalentImpact impact : impacts) impact.remove(player);
    }

    public void enableTalent(@NotNull Player player) {
        for (TalentImpact impact : impacts) impact.enable(player);
    }

    public void disableTalent(@NotNull Player player) {
        for (TalentImpact impact : impacts) impact.disable(player);
    }

    public Talent getParent() {
        return getHierarchy().getParent(this);
    }

    public Talent getChild() {
        return getHierarchy().getChild(this);
    }

    public boolean purchasable() {
        return wisdom > 0;
    }

    public boolean containsImmunity(Holder<DamageType> holder) {
        for (DamageTypePredicate predicate : damageImmunities) if (predicate.matches(holder)) return true;
        return false;
    }

    public float getMultiplier(Holder<DamageType> holder) {
        float multip = 1f;
        for (Pair<Float, DamageTypePredicate> entry : damageMultipliers) if (entry.getSecond().matches(holder)) multip *= entry.getFirst();
        return multip;
    }

    public IHierarchy<Talent> getHierarchy() {
        return TalentTreeData.getTalentToHierarchy().get(id());
    }

    @Override
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();
        int amo = 0;
        if (Screen.hasShiftDown()) {
            if (this.purchasable()) components.add(GuiHelper.bakeComponent("fantazia.gui.talent.wisdom_cost", new ChatFormatting[]{ChatFormatting.BLUE}, new ChatFormatting[]{ChatFormatting.DARK_PURPLE}, wisdom()));
            else {
                String criteria = Component.translatable(title() + ".criteria.lines").getString();
                try {
                    amo = Integer.parseInt(criteria);
                } catch (NumberFormatException ignored) {}
                if (amo > 0) {
                    components.add(GuiHelper.bakeComponent("fantazia.gui.talent.requirement", new ChatFormatting[]{ChatFormatting.DARK_PURPLE}, null));
                    for (int i = 1; i <= amo; i++) components.add(GuiHelper.bakeComponent(title() + ".criteria." + i, new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, null));
                }
            }
            if (Fantazia.DEVELOPER_MODE) {
                components.add(Component.literal(" "));
                components.add(Component.literal("*** DEVELOPER ***"));
                advancement().ifPresent(location -> components.add(Component.literal("Advancement: " + location).withStyle(ChatFormatting.RED)));
                components.add(Component.literal("Icon: " + icon));
                components.add(Component.literal("Id: " + id));
            }

            return components;
        }

        components.add(GuiHelper.bakeComponent(title() + ".name", null, null));
        String desc = Component.translatable(title() + ".desc.lines").getString();
        try {
            amo = Integer.parseInt(desc);
        } catch (NumberFormatException ignored) {}

        if (amo > 0) for (int i = 1; i <= amo; i++) components.add(GuiHelper.bakeComponent(title() + ".desc." + i, new ChatFormatting[]{ChatFormatting.GOLD},null));
        return components;
    }

    @Override
    public @NotNull String toString() {
        return id().toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Talent talent && talent.id.equals(id);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("icon").forGetter(builder -> builder.icon),
                Codec.STRING.fieldOf("title").forGetter(builder -> builder.title),
                Codec.INT.optionalFieldOf("wisdom", 0).forGetter(builder -> builder.wisdom),
                DamageTypePredicate.CODEC.listOf().optionalFieldOf("damage_immunities", ImmutableList.of()).forGetter(builder -> builder.damageImmunities),
                FTZCodecs.pairCodec("multiplier","damage_type", Codec.FLOAT, DamageTypePredicate.CODEC).listOf().optionalFieldOf("damage_multiplies", Lists.newArrayList()).forGetter(builder -> builder.damageMultipliers),
                Codec.unboundedMap(BuiltInRegistries.ATTRIBUTE.holderByNameCodec(), AttributeModifierBuilder.CODEC).optionalFieldOf("attribute_modifiers", ImmutableMap.of()).forGetter(builder -> builder.attributeModifiers),
                Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("curio_modifiers", ImmutableMap.of()).forGetter(builder -> builder.curioModifiers),
                TalentImpact.CODEC.listOf().optionalFieldOf("impacts", Lists.newArrayList()).forGetter(builder -> builder.effects),
                ResourceLocation.CODEC.optionalFieldOf("advancement").forGetter(builder -> Optional.ofNullable(builder.advancement)),
                ResourceLocation.CODEC.optionalFieldOf("background").forGetter(builder -> Optional.ofNullable(builder.background))
        ).apply(instance, Builder::decode));

        private static Builder decode(ResourceLocation icon, String title, int wisdom, List<DamageTypePredicate> damageImmunities, List<Pair<Float, DamageTypePredicate>> damageMultipliers, Map<Holder<Attribute>, AttributeModifierBuilder> attributeModifiers, Map<String, Integer> curioModifiers, List<TalentImpact> impact, Optional<ResourceLocation> advancement, Optional<ResourceLocation> background) {
            Builder builder = builder().icon(icon).title(title).wisdom(wisdom);
            damageImmunities.forEach(builder::addDamageImmunities);
            damageMultipliers.forEach(builder::addDamageMultiplier);
            attributeModifiers.forEach(builder::addAttributeModifier);
            curioModifiers.forEach(builder::addCurioModifiers);
            impact.forEach(builder::addImpact);
            advancement.ifPresent(builder::advancement);
            background.ifPresent(builder::background);
            return builder;
        }

        public ResourceLocation icon = null;
        public String title = null;

        public int wisdom = 0;
        public final List<DamageTypePredicate> damageImmunities = Lists.newArrayList();
        public final List<Pair<Float, DamageTypePredicate>> damageMultipliers = Lists.newArrayList();
        public final Map<Holder<Attribute>, AttributeModifierBuilder> attributeModifiers = Maps.newHashMap();
        public final Map<String, Integer> curioModifiers = Maps.newHashMap();

        public final List<TalentImpact> effects = Lists.newArrayList();
        public ResourceLocation advancement = null;
        public ResourceLocation background = null;

        public Builder icon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder wisdom(int wisdom) {
            this.wisdom = wisdom;
            return this;
        }

        public Builder addDamageImmunities(DamageTypePredicate... predicates) {
            this.damageImmunities.addAll(Arrays.stream(predicates).toList());
            return this;
        }

        @SafeVarargs
        public final Builder addDamageImmunities(ResourceKey<DamageType>... resourceKeys) {
            this.damageImmunities.add(DamageTypePredicate.builder().addDamageTypes(resourceKeys).build());
            return this;
        }

        @SafeVarargs
        public final Builder addDamageImmunities(TagKey<DamageType>... tagKeys) {
            this.damageImmunities.add(DamageTypePredicate.builder().addTagPredicates(tagKeys).or().build());
            return this;
        }

        public Builder addDamageMultiplier(float multiplier, DamageTypePredicate predicate) {
            this.damageMultipliers.add(new Pair<>(multiplier, predicate));
            return this;
        }

        @SafeVarargs
        public final Builder addDamageMultiplier(float multiplier, ResourceKey<DamageType>... damageTypes) {
            DamageTypePredicate.Builder builder = DamageTypePredicate.builder().addDamageTypes(damageTypes);
            this.damageMultipliers.add(new Pair<>(multiplier, builder.build()));
            return this;
        }

        @SafeVarargs
        public final Builder addDamageMultiplier(float multiplier, TagKey<DamageType>... damageTypes) {
            DamageTypePredicate.Builder builder = DamageTypePredicate.builder().addTagPredicates(damageTypes).or();
            this.damageMultipliers.add(new Pair<>(multiplier, builder.build()));
            return this;
        }

        public Builder addDamageMultiplier(Pair<Float, DamageTypePredicate> predicate) {
            this.damageMultipliers.add(predicate);
            return this;
        }

        public Builder addDamageMultiplier(float multiplier, TagKey<DamageType> tagKey) {
            DamageTypePredicate.Builder builder = DamageTypePredicate.builder();
            builder.addTagPredicates(TagPredicate.is(tagKey));
            this.damageMultipliers.add(new Pair<>(multiplier, builder.build()));
            return this;
        }

        public Builder addAttributeModifier(Holder<Attribute> attribute, AttributeModifierBuilder builder) {
            this.attributeModifiers.put(attribute, builder);
            return this;
        }

        public Builder addAttributeModifier(Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
            this.attributeModifiers.put(attribute, new AttributeModifierBuilder(amount, operation));
            return this;
        }

        public Builder addCurioModifiers(String curio, int amount) {
            this.curioModifiers.put(curio, amount);
            return this;
        }

        public Builder advancement(ResourceLocation advancement) {
            this.advancement = advancement;
            return this;
        }

        public Builder addImpact(TalentImpact impact) {
            this.effects.add(impact);
            return this;
        }

        public Builder background(ResourceLocation background) {
            this.background = background;
            return this;
        }

        public Talent build(ResourceLocation id) throws TalentDataException {
            if (icon == null) throw new TalentDataException("Build is not complete: icon is missing");
            if (title == null) throw new TalentDataException("Build is not complete: title is missing");

            ImmutableList<DamageTypePredicate> immunities = damageImmunities.isEmpty() ? ImmutableList.of() : ImmutableList.copyOf(damageImmunities);
            List<Pair<Float, DamageTypePredicate>> multipliers = damageMultipliers.isEmpty() ? ImmutableList.of() : ImmutableList.copyOf(damageMultipliers);
            ImmutableMap.Builder<Holder<Attribute>, AttributeModifier> mapBuilder = ImmutableMap.builder();
            attributeModifiers.forEach(((attribute, builder) -> mapBuilder.put(attribute, builder.build(id))));
            ImmutableMap<String, Integer> curios = curioModifiers.isEmpty() ? ImmutableMap.of() : ImmutableMap.copyOf(curioModifiers);
            ImmutableList<TalentImpact> impactList = effects.isEmpty() ? ImmutableList.of() : ImmutableList.copyOf(effects);
            return new Talent(id, icon, title, wisdom, immunities, multipliers, mapBuilder.build(), curios, impactList, Optional.ofNullable(advancement), Optional.ofNullable(background));
        }

        public TalentBuilderHolder holder(ResourceLocation id) {
            return new TalentBuilderHolder(id,this);
        }

        public void save(Consumer<TalentBuilderHolder> output, ResourceLocation id) {
            output.accept(holder(id));
        }
    }
}
