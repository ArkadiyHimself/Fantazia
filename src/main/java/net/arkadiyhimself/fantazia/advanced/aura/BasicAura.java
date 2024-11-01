package net.arkadiyhimself.fantazia.advanced.aura;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.api.FantazicRegistries;
import net.arkadiyhimself.fantazia.api.type.item.ITooltipBuilder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * An Aura is an abstract object that has an owner and affects entities around its owner one way or another.
 * <br>
 * Aura can be one of three {@link TYPE types}: {@link TYPE#POSITIVE positive}, {@link TYPE#NEGATIVE negative} and {@link TYPE#MIXED mixed}.
 * <br>
 * Aura can have {@link #primaryFilter conditions} for an entity which have to be met in order to apply its effects to the entity
 * <br>
 * The effects of auras vary from {@link #attributeModifiers changing attributes} to {@link #affectedTick(Entity, Entity) influencing} an entity in certain ways while it is inside the aura
 *
 * @param <T> the class of affected entities
 */
public class BasicAura<T extends Entity> implements ITooltipBuilder {

    public enum TYPE {
        POSITIVE, NEGATIVE, MIXED
    }

    private final Class<T> tClass;
    private final TYPE type;
    private final float range;
    /**
     * Contains attributes and respective attribute modifiers that are
     * <br>
     * applied to all suitable entities within aura
     */
    private final ImmutableMap<Holder<Attribute>, AttributeModifier> attributeModifiers;
    /**
     * Contains DAMs which will be applied to all suitable entities within aura.
     * <br>
     * The percentage of each DAM depends on distance between affected entity
     * <br>
     * and the owner of aura: it goes from 0.0F when an entity is on the "edge" of
     * <br>
     * the aura to 1.0F when it is closest to the owner
     */
    private final ImmutableMap<Holder<Attribute>, AttributeModifier> dynamicAttributeModifiers;
    /**
     * Contains the mob effects which will be applied to suitable entities
     * <br>
     * inside aura every tick for 2 ticks. The integer value is the level
     * <br>
     * of respective applied effect
     */
    private final ImmutableMap<Holder<MobEffect>, Integer> mobEffects;
    /**
     * Primary Filter is supposed to check entity's «permanent» fields which
     * <br>
     * can not be changed or can only be changed once like entity's
     * <br>
     * {@link Entity#getType() type}, {@link TamableAnimal#getOwner() owner}, {@link LivingEntity#fireImmune() built-in fire resistance}. etc.,
     */
    private final BiPredicate<T, Entity> primaryFilter;
    /**
     * Secondary Filter is supposed to check entity's «transient» fields which
     * <br>
     * change dynamically back and forth depending on the situation like its
     * <br>
     * {@link LivingEntity#getHealth() health}, {@link LivingEntity#getAttributes() attributes}, {@link LivingEntity#getTicksFrozen() freezing ticks}, {@link Player#getFoodData() food data} for players, etc.,
     */
    private final BiPredicate<T, Entity> secondaryFilter;
    /**
     * ownerConditions is supposed to check aura instance's owner's fields
     * <br>
     * to determine whether {@link BasicAura#onTickOwner} should be performed or not
     */
    private final Predicate<Entity> ownerConditions;
    /**
     * OnTick is performed every tick on every entity inside the aura.
     * <br>
     * The owner is provided mostly for accessing its fields and creating {@link net.minecraft.world.damagesource.DamageSource damage sources}
     * <br>
     * or {@link net.arkadiyhimself.fantazia.advanced.healing.HealingSource healing sources} with owner as their «causing entity», etc.
     * <br>
     * Although you can do anything with the owner of the aura in this BiConsumer,
     * <br>
     * I would advise you not to do that and use {@link BasicAura#onTickOwner} instead because not only
     * <br>
     * this interface isn't called when there are no entities inside the aura besides the owner,
     * <br>
     * the effects on owner will also be multiplied by the amount of entities inside if there are more than one
     */
    private final BiConsumer<T, Entity> onTickAffected;
    /**
     * OnTickOwner is performed every tick on the owner of the aura
     */
    private final Consumer<Entity> onTickOwner;
    /**
     * OnTickBlock is performed every tick on all blocks within aura
     */
    private final BiConsumer<BlockPos, AuraInstance<T>> onTickBlock;
    /**
     * Contains all {@link DamageType types of damage} which suitable entities should
     * <br>
     * be immune to while within aura
     */
    private final ImmutableList<ResourceKey<DamageType>> damageImmunities;
    private final ImmutableList<TagKey<DamageType>> tagDamageImmunities;
    /**
     * Whenever a suitable entity inside aura takes damage from a {@link net.minecraft.world.damagesource.DamageSource source},
     * <br>
     * whose {@link DamageType damage type} is inside this map, the damage is multiplied
     * <br>
     * by respective float value inside this map
     */
    private final ImmutableMap<ResourceKey<DamageType>, Float> damageMultipliers;
    private final ImmutableMap<TagKey<DamageType>, Float> tagDamageMultipliers;

    protected BasicAura(Class<T> affectedType, TYPE type, float range,
                     Map<Holder<Attribute>, AttributeModifier> attributeModifiers,
                     Map<Holder<Attribute>, AttributeModifier> dynamicAttributeModifiers,
                     Map<Holder<MobEffect>, Integer> mobEffects,
                     BiPredicate<T, Entity> primaryFilter,
                     BiPredicate<T, Entity> secondaryFilter,
                     Predicate<Entity> ownerConditions,
                     BiConsumer<T, Entity> onTickAffected,
                     Consumer<Entity> onTickOwner,
                     BiConsumer<BlockPos, AuraInstance<T>> onTickBlock,
                     List<ResourceKey<DamageType>> damageImmunities,
                     List<TagKey<DamageType>> tagDamageImmunities,
                     Map<ResourceKey<DamageType>, Float> damageMultipliers,
                     Map<TagKey<DamageType>, Float> tagDamageMultipliers
    ) {
        this.range = range;
        this.type = type;
        this.tClass = affectedType;

        this.attributeModifiers = ImmutableMap.copyOf(attributeModifiers);
        this.dynamicAttributeModifiers = ImmutableMap.copyOf(dynamicAttributeModifiers);
        this.mobEffects = ImmutableMap.copyOf(mobEffects);

        this.primaryFilter = primaryFilter;
        this.secondaryFilter = secondaryFilter;
        this.ownerConditions = ownerConditions;

        this.onTickAffected = onTickAffected;
        this.onTickOwner = onTickOwner;
        this.onTickBlock = onTickBlock;

        this.damageImmunities = ImmutableList.copyOf(damageImmunities);
        this.tagDamageImmunities = ImmutableList.copyOf(tagDamageImmunities);
        this.damageMultipliers = ImmutableMap.copyOf(damageMultipliers);
        this.tagDamageMultipliers = ImmutableMap.copyOf(tagDamageMultipliers);
    }

    @Override
    public List<Component> buildIconTooltip() {
        List<Component> components = Lists.newArrayList();
        if (getID() == null) return components;
        String lines = Component.translatable("aura.icon." + this.getID().getNamespace() + "." + this.getID().getPath() + ".lines").getString();
        int li = 0;
        try {
            li = Integer.parseInt(lines);
        } catch (NumberFormatException ignored){}

        if (li > 0) {
            ChatFormatting[] head = switch (this.type) {
                case MIXED -> new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
                case NEGATIVE -> new ChatFormatting[]{ChatFormatting.RED};
                case POSITIVE -> new ChatFormatting[]{ChatFormatting.GREEN};
            };
            for (int i = 1; i <= li; i++) components.add(Component.translatable("aura.icon." + this.getID().getNamespace() + "." + this.getID().getPath() + "." + i).withStyle(head));

        }
        return components;
    }

    @Override
    public List<Component> itemTooltip(@Nullable ItemStack stack) {
        List<Component> components = Lists.newArrayList();
        if (this.getID() == null) return components;
        String basicPath = "aura." + this.getID().getNamespace() + "." + this.getID().getPath();
        int amo = 0;
        if (!Screen.hasShiftDown()) {
            components.add(Component.literal(" "));
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                amo = Integer.parseInt(desc);
            } catch (NumberFormatException ignored){}

            if (amo > 0) for (int i = 1; i <= amo; i++) components.add(GuiHelper.bakeComponent(basicPath + ".desc." + i, null, null));
            return components;
        }
        components.add(Component.literal(" "));

        ChatFormatting[] text = switch (this.type) {
            case MIXED -> new ChatFormatting[]{ChatFormatting.GOLD};
            case NEGATIVE -> new ChatFormatting[]{ChatFormatting.DARK_PURPLE};
            case POSITIVE -> new ChatFormatting[]{ChatFormatting.AQUA};
        };
        ChatFormatting[] ability = switch (this.type) {
            case MIXED -> new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD};
            case NEGATIVE -> new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD};
            case POSITIVE -> new ChatFormatting[]{ChatFormatting.DARK_GREEN, ChatFormatting.BOLD};
        };
        ChatFormatting[] heading = switch (this.type) {
            case MIXED -> new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
            case NEGATIVE -> new ChatFormatting[]{ChatFormatting.RED};
            case POSITIVE -> new ChatFormatting[]{ChatFormatting.GREEN};
        };
        // spell name
        String namePath = basicPath + ".name";
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.aura", heading, ability, Component.translatable(namePath).getString()));

        // spell range
        Component addRangeComponent = bakeRangeComponent();
        String range = String.format("%.1f", this.getRadius());
        Component basicRange = Component.literal(range).withStyle(ability);

        Component rangeComponent;
        if (addRangeComponent != null) rangeComponent = Component.translatable("tooltip.fantazia.common.aura_range_modified", basicRange, addRangeComponent).withStyle(heading);
        else rangeComponent = GuiHelper.bakeComponent("tooltip.fantazia.common.aura_range", heading, ability, basicRange);

        components.add(rangeComponent);
        components.add(Component.literal(" "));

        String desc = Component.translatable(basicPath + ".lines").getString();
        try {
            amo = Integer.parseInt(desc);
        } catch (NumberFormatException ignored){}

        if (amo > 0) for (int i = 1; i <= amo; i++) components.add(GuiHelper.bakeComponent(basicPath + "." + i, text, null));

        amo = 0;
        String pass = Component.translatable(basicPath + ".stats.lines").getString();
        try {
            amo = Integer.parseInt(pass);
        } catch (NumberFormatException ignored){}

        if (amo > 0) {
            components.add(Component.literal(" "));
            for (int i = 1; i <= amo; i++) components.add(GuiHelper.bakeComponent(basicPath + ".stats." + i, null, null));
        }

        return components;
    }

    public Class<T> affectedClass() {
        return tClass;
    }

    public ResourceLocation getID() {
        return FantazicRegistries.AURAS.getKey(this);
    }

    public Component getAuraComponent() {
        if (getID() == null) return null;
        String s = "aura." + getID().getNamespace() + "." + getID().getPath();
        return Component.translatable(s);
    }

    public ResourceLocation getIcon() {
        if (getID() == null) return getID();
        return getID().withPrefix("textures/aura/").withSuffix(".png");
    }

    public TYPE getType() {
        return type;
    }

    public float getRadius() {
        return range;
    }

    public boolean couldAffect(T entity, Entity owner) {
        return primaryFilter.test(entity, owner) && entity != owner;
    }

    public boolean canAffect(T entity, Entity owner) {
        return primaryFilter.test(entity, owner) && secondaryFilter.test(entity, owner) && entity != owner;
    }

    public Map<Holder<MobEffect>, Integer> getMobEffects() {
        return mobEffects;
    }

    public Map<Holder<Attribute>, AttributeModifier> getAttributeModifiers() {
        return attributeModifiers;
    }

    public Map<Holder<Attribute>, AttributeModifier> getDynamicAttributeModifiers() {
        return dynamicAttributeModifiers;
    }

    public boolean primary(T entity, Entity owner) {
        return primaryFilter.test(entity, owner);
    }

    public boolean secondary(T entity, Entity owner) {
        return secondaryFilter.test(entity, owner);
    }

    public boolean ownerCond(Entity owner) {
        return ownerConditions.test(owner);
    }

    public void affectedTick(T entity, Entity owner) {
        onTickAffected.accept(entity, owner);
    }

    public void ownerTick(Entity owner) {
        onTickOwner.accept(owner);
    }

    public void blockTick(BlockPos blockPos, AuraInstance<T> auraInstance) {
        onTickBlock.accept(blockPos, auraInstance);
    }

    public ImmutableList<ResourceKey<DamageType>> immunities() {
        return damageImmunities;
    }

    public boolean immunityTo(Holder<DamageType> damageTypeHolder) {
        if (damageImmunities.contains(damageTypeHolder.getKey())) return true;
        else for (TagKey<DamageType> tagKey : tagDamageImmunities) if (damageTypeHolder.is(tagKey)) return true;
        return false;
    }

    public Map<ResourceKey<DamageType>, Float> multipliers() {
        return damageMultipliers;
    }

    public float multiplierFor(Holder<DamageType> damageTypeHolder) {
        Float d0 = damageMultipliers.get(damageTypeHolder.getKey());
        float d1 = d0 == null ? 1f : d0;
        for (Map.Entry<TagKey<DamageType>, Float> entry : tagDamageMultipliers.entrySet()) if (damageTypeHolder.is(entry.getKey())) d1 *= entry.getValue();
        return d1;
    }

    private @Nullable Component bakeRangeComponent() {
        AttributeInstance instance = Minecraft.getInstance().player == null ? null : Minecraft.getInstance().player.getAttribute(FTZAttributes.AURA_RANGE_ADDITION);
        if (instance == null) return null;
        double value = instance.getValue();
        if (value == 0) return null;
        if (value > 0) return Component.literal("+ " + value).withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD, ChatFormatting.ITALIC);
        else return Component.literal("- " + Math.min(this.getRadius(), Math.abs(value))).withStyle(ChatFormatting.RED, ChatFormatting.BOLD, ChatFormatting.ITALIC);
    }

    public static class Builder<T extends Entity> {

        private final Class<T> affectedClass;
        private final TYPE type;
        private final float range;

        private final Map<Holder<Attribute>, AttributeModifier> attributeModifiers = Maps.newHashMap();
        private final Map<Holder<Attribute>, AttributeModifier> dynamicAttributeModifiers = Maps.newHashMap();
        private final Map<Holder<MobEffect>, Integer> mobEffects = Maps.newHashMap();

        private BiPredicate<T, Entity> primaryFilter = (entity, owner) -> true;
        private BiPredicate<T, Entity> secondaryFilter = (entity, owner) -> true;
        private Predicate<Entity> ownerConditions = owner -> true;

        private BiConsumer<T, Entity> onTickAffected = (entity, owner) -> {};
        private Consumer<Entity> onTickOwner = owner -> {};
        private BiConsumer<BlockPos, AuraInstance<T>> onTickBlock = ((blockPos, tmAuraInstance) -> {});

        private final List<ResourceKey<DamageType>> damageImmunities = Lists.newArrayList();
        private final List<TagKey<DamageType>> tagDamageImmunities = Lists.newArrayList();
        private final Map<ResourceKey<DamageType>, Float> damageMultipliers = Maps.newHashMap();
        private final Map<TagKey<DamageType>, Float> tagDamageMultipliers = Maps.newHashMap();

        public Builder(Class<T> affectedClass, TYPE type, float range) {
            this.affectedClass = affectedClass;
            this.type = type;
            this.range = range;
        }

        public Builder<T> addAttributeModifier(Holder<Attribute> attribute, AttributeModifier attributeModifier) {
            this.attributeModifiers.put(attribute, attributeModifier);
            return this;
        }

        public Builder<T> addDynamicAttributeModifier(Holder<Attribute> attribute, AttributeModifier attributeModifier) {
            this.dynamicAttributeModifiers.put(attribute, attributeModifier);
            return this;
        }

        public Builder<T> addMobEffect(Holder<MobEffect> mobEffect, int amplifier) {
            this.mobEffects.put(mobEffect, amplifier);
            return this;
        }

        public Builder<T> primaryFilter(BiPredicate<T, Entity> value) {
            this.primaryFilter = value;
            return this;
        }

        public Builder<T> secondaryFilter(BiPredicate<T, Entity> value) {
            this.secondaryFilter = value;
            return this;
        }

        public Builder<T> ownerConditions(Predicate<Entity> value) {
            this.ownerConditions = value;
            return this;
        }

        public Builder<T> onTickAffected(BiConsumer<T, Entity> value) {
            this.onTickAffected = value;
            return this;
        }

        public Builder<T> onTickOwner(Consumer<Entity> value) {
            this.onTickOwner = value;
            return this;
        }

        public Builder<T> onTickBlock(BiConsumer<BlockPos, AuraInstance<T>> value) {
            this.onTickBlock = value;
            return this;
        }

        public Builder<T> addDamageImmunity(ResourceKey<DamageType> damageType) {
            this.damageImmunities.add(damageType);
            return this;
        }

        public Builder<T> addDamageImmunity(TagKey<DamageType> damageType) {
            this.tagDamageImmunities.add(damageType);
            return this;
        }

        public Builder<T> putDamageMultiplier(ResourceKey<DamageType> damageType, float multiplier) {
            this.damageMultipliers.put(damageType, multiplier);
            return this;
        }

        public Builder<T> putDamageMultiplier(TagKey<DamageType> damageType, float multiplier) {
            this.tagDamageMultipliers.put(damageType, multiplier);
            return this;
        }

        public BasicAura<T> build() {
            return new BasicAura<>(affectedClass, type, range,
                    attributeModifiers,
                    dynamicAttributeModifiers,
                    mobEffects,
                    primaryFilter,
                    secondaryFilter,
                    ownerConditions,
                    onTickAffected,
                    onTickOwner,
                    onTickBlock,
                    damageImmunities,
                    tagDamageImmunities,
                    damageMultipliers,
                    tagDamageMultipliers
            );
        }
    }
}
