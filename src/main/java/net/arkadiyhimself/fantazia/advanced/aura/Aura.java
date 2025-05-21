package net.arkadiyhimself.fantazia.advanced.aura;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.items.ITooltipBuilder;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
 * The effects of auras vary from {@link #attributeModifiers changing attributes} to {@link #affectedTick(Entity, AuraInstance) influencing} an entity in certain ways while it is inside the aura
 */
public class Aura implements ITooltipBuilder {

    public enum TYPE {
        POSITIVE, NEGATIVE, MIXED
    }

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
    private final BiPredicate<Entity, Entity> primaryFilter;
    /**
     * Secondary Filter is supposed to check entity's «transient» fields which
     * <br>
     * change dynamically back and forth depending on the situation like its
     * <br>
     * {@link LivingEntity#getHealth() health}, {@link LivingEntity#getAttributes() attributes}, {@link LivingEntity#getTicksFrozen() freezing ticks}, {@link Player#getFoodData() food data} for players, etc.,
     */
    private final BiPredicate<Entity, Entity> secondaryFilter;
    /**
     * ownerConditions is supposed to check aura instance's owner's fields
     * <br>
     * to determine whether {@link Aura#onTickOwner} should be performed or not
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
     * I would advise you not to do that and use {@link Aura#onTickOwner} instead because not only
     * <br>
     * this interface isn't called when there are no entities inside the aura besides the owner,
     * <br>
     * the effects on owner will also be multiplied by the amount of entities inside if there are more than one
     */
    private final BiConsumer<Entity, AuraInstance> onTickAffected;
    /**
     * OnTickOwner is performed every tick on the owner of the aura
     */
    private final Consumer<Entity> onTickOwner;
    /**
     * OnTickBlock is performed every tick on all blocks within aura
     */
    private final BiConsumer<BlockPos, AuraInstance> onTickBlock;
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

    private final ChatFormatting[] tooltipFormatting;
    private @Nullable String descriptionId;
    private final boolean amplifiable;

    protected Aura(TYPE type, float range,
                   Map<Holder<Attribute>, AttributeModifier> attributeModifiers,
                   Map<Holder<Attribute>, AttributeModifier> dynamicAttributeModifiers,
                   Map<Holder<MobEffect>, Integer> mobEffects,
                   BiPredicate<Entity, Entity> primaryFilter,
                   BiPredicate<Entity, Entity> secondaryFilter,
                   Predicate<Entity> ownerConditions,
                   BiConsumer<Entity, AuraInstance> onTickAffected,
                   Consumer<Entity> onTickOwner,
                   BiConsumer<BlockPos, AuraInstance> onTickBlock,
                   List<ResourceKey<DamageType>> damageImmunities,
                   List<TagKey<DamageType>> tagDamageImmunities,
                   Map<ResourceKey<DamageType>, Float> damageMultipliers,
                   Map<TagKey<DamageType>, Float> tagDamageMultipliers,
                   @Nullable ChatFormatting[] tooltipFormatting,
                   boolean amplifiable
    ) {
        this.range = range;
        this.type = type;

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

        this.tooltipFormatting = tooltipFormatting != null ? tooltipFormatting :
                switch (type) {
                    case POSITIVE -> new ChatFormatting[]{ChatFormatting.GREEN};
                    case NEGATIVE -> new ChatFormatting[]{ChatFormatting.RED};
                    case MIXED -> new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
                };

        this.amplifiable = amplifiable;
    }

    public List<Component> buildIconTooltip() {
        List<Component> components = Lists.newArrayList();
        String lines = Component.translatable("aura_tooltip." + getID().getNamespace() + "." + getID().getPath() + ".lines").getString();
        int li = 0;
        try {
            li = Integer.parseInt(lines);
        } catch (NumberFormatException ignored){}

        if (li > 0) {
            ChatFormatting[] head = switch (this.getType()) {
                case MIXED -> new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
                case NEGATIVE -> new ChatFormatting[]{ChatFormatting.RED};
                case POSITIVE -> new ChatFormatting[]{ChatFormatting.GREEN};
            };
            for (int i = 1; i <= li; i++) components.add(Component.translatable("aura_tooltip." + getID().getNamespace() + "." + getID().getPath() + "." + i).withStyle(head));

        }
        return components;
    }

    @Override
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();
        String basicPath = getDescriptionId();

        components.add(Component.literal(" "));

        ChatFormatting[] text = switch (this.getType()) {
            case MIXED -> new ChatFormatting[]{ChatFormatting.GOLD};
            case NEGATIVE -> new ChatFormatting[]{ChatFormatting.DARK_PURPLE};
            case POSITIVE -> new ChatFormatting[]{ChatFormatting.AQUA};
        };
        ChatFormatting[] ability = switch (this.getType()) {
            case MIXED -> new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD};
            case NEGATIVE -> new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD};
            case POSITIVE -> new ChatFormatting[]{ChatFormatting.DARK_GREEN, ChatFormatting.BOLD};
        };
        ChatFormatting[] heading = switch (this.getType()) {
            case MIXED -> new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
            case NEGATIVE -> new ChatFormatting[]{ChatFormatting.RED};
            case POSITIVE -> new ChatFormatting[]{ChatFormatting.GREEN};
        };
        // spell name
        String namePath = basicPath + ".name";
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.aura", heading, ability, Component.translatable(namePath)));

        // spell range
        Component addRangeComponent = bakeRangeComponent();
        String range = String.format("%.1f", getRadius());
        Component basicRange = Component.literal(range).withStyle(ability);

        Component rangeComponent;
        if (addRangeComponent != null) rangeComponent = Component.translatable("tooltip.fantazia.common.aura.radius_modified", basicRange, addRangeComponent).withStyle(heading);
        else rangeComponent = GuiHelper.bakeComponent("tooltip.fantazia.common.aura.radius", heading, ability, basicRange);

        components.add(rangeComponent);
        components.add(Component.literal(" "));

        String desc = Component.translatable(basicPath + ".lines").getString();

        int lines = 0;
        try {
            lines = Integer.parseInt(desc);
        } catch (NumberFormatException ignored){}

        if (lines > 0) for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + "." + i, text, null));

        lines = 0;
        String pass = Component.translatable(basicPath + ".stats.lines").getString();
        try {
            lines = Integer.parseInt(pass);
        } catch (NumberFormatException ignored){}

        if (lines > 0) {
            components.add(Component.literal(" "));
            for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + ".stats." + i, null, null));
        }

        return components;
    }

    public ResourceLocation getID() {
        return FantazicRegistries.AURAS.getKey(this);
    }

    public MutableComponent getAuraComponent() {
        String s = "aura.icon." + getID().getNamespace() + "." + getID().getPath();
        return Component.translatable(s);
    }

    public static ResourceLocation getIcon(Holder<Aura> holder) {
        ResourceKey<Aura> key = holder.getKey();
        if (key == null) return ResourceLocation.parse("");
        return key.location().withPrefix("textures/aura/").withSuffix(".png");
    }

    public TYPE getType() {
        return type;
    }

    public float getRadius() {
        return range;
    }

    public boolean affects(Entity entity, Entity owner) {
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

    public boolean primary(Entity entity, Entity owner) {
        return primaryFilter.test(entity, owner);
    }

    public boolean secondary(Entity entity, Entity owner) {
        return secondaryFilter.test(entity, owner);
    }

    public boolean ownerCond(Entity owner) {
        return ownerConditions.test(owner);
    }

    public void affectedTick(Entity entity, AuraInstance owner) {
        onTickAffected.accept(entity, owner);
    }

    public void ownerTick(Entity owner) {
        onTickOwner.accept(owner);
    }

    public void blockTick(BlockPos blockPos, AuraInstance auraInstance) {
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

    public ChatFormatting[] tooltipFormatting() {
        return tooltipFormatting;
    }

    protected @Nullable Component bakeRangeComponent() {
        AttributeInstance instance = Minecraft.getInstance().player == null ? null : Minecraft.getInstance().player.getAttribute(FTZAttributes.AURA_RANGE_ADDITION);
        if (instance == null) return null;
        double value = instance.getValue();
        if (value == 0) return null;
        if (value > 0) return Component.literal("+ " + value).withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD, ChatFormatting.ITALIC);
        else return Component.literal("- " + Math.min(this.getRadius(), Math.abs(value))).withStyle(ChatFormatting.RED, ChatFormatting.BOLD, ChatFormatting.ITALIC);
    }

    public String getDescriptionId() {
        if (descriptionId == null) descriptionId = Util.makeDescriptionId("aura", FantazicRegistries.AURAS.getKey(this));
        return descriptionId;
    }

    public boolean amplifiable() {
        return amplifiable;
    }

    public static Builder builder(TYPE type, float range) {
        return new Builder(type, range);
    }

    public static class Builder {

        private final TYPE type;
        private final float range;

        private final Map<Holder<Attribute>, AttributeModifier> attributeModifiers = Maps.newHashMap();
        private final Map<Holder<Attribute>, AttributeModifier> dynamicAttributeModifiers = Maps.newHashMap();
        private final Map<Holder<MobEffect>, Integer> mobEffects = Maps.newHashMap();

        private BiPredicate<Entity, Entity> primaryFilter = (entity, owner) -> true;
        private BiPredicate<Entity, Entity> secondaryFilter = (entity, owner) -> true;
        private Predicate<Entity> ownerConditions = owner -> true;

        private BiConsumer<Entity, AuraInstance> onTickAffected = (entity, owner) -> {};
        private Consumer<Entity> onTickOwner = owner -> {};
        private BiConsumer<BlockPos, AuraInstance> onTickBlock = ((blockPos, tmAuraInstance) -> {});

        private final List<ResourceKey<DamageType>> damageImmunities = Lists.newArrayList();
        private final List<TagKey<DamageType>> tagDamageImmunities = Lists.newArrayList();
        private final Map<ResourceKey<DamageType>, Float> damageMultipliers = Maps.newHashMap();
        private final Map<TagKey<DamageType>, Float> tagDamageMultipliers = Maps.newHashMap();

        private @Nullable ChatFormatting[] tooltipFormatting = null;
        private boolean amplifiable = false;

        private Builder(TYPE type, float range) {
            this.type = type;
            this.range = range;
        }

        public Builder addAttributeModifier(Holder<Attribute> attribute, AttributeModifier attributeModifier) {
            this.attributeModifiers.put(attribute, attributeModifier);
            return this;
        }

        public Builder addDynamicAttributeModifier(Holder<Attribute> attribute, AttributeModifier attributeModifier) {
            this.dynamicAttributeModifiers.put(attribute, attributeModifier);
            return this;
        }

        public Builder addMobEffect(Holder<MobEffect> mobEffect, int amplifier) {
            this.mobEffects.put(mobEffect, amplifier);
            return this;
        }

        public Builder primaryFilter(BiPredicate<Entity, Entity> primaryFilter) {
            this.primaryFilter = primaryFilter;
            return this;
        }

        public Builder primaryFilter(Predicate<Entity> primaryFilter) {
            this.primaryFilter = (entity, entity2) -> primaryFilter.test(entity);
            return this;
        }

        public Builder secondaryFilter(BiPredicate<Entity, Entity> secondaryFilter) {
            this.secondaryFilter = secondaryFilter;
            return this;
        }

        public Builder secondaryFilter(Predicate<Entity> secondaryFilter) {
            this.secondaryFilter = ((entity, entity2) -> secondaryFilter.test(entity));
            return this;
        }

        public Builder ownerConditions(Predicate<Entity> value) {
            this.ownerConditions = value;
            return this;
        }

        public Builder onTickAffected(BiConsumer<Entity, AuraInstance> onTickAffected) {
            this.onTickAffected = onTickAffected;
            return this;
        }

        public Builder onTickAffected(Consumer<Entity> onTickAffected) {
            this.onTickAffected = ((entity, auraInstance) -> onTickAffected.accept(entity));
            return this;
        }

        public Builder onTickOwner(Consumer<Entity> value) {
            this.onTickOwner = value;
            return this;
        }

        public Builder onTickBlock(BiConsumer<BlockPos, AuraInstance> value) {
            this.onTickBlock = value;
            return this;
        }

        public Builder addDamageImmunity(ResourceKey<DamageType> damageType) {
            this.damageImmunities.add(damageType);
            return this;
        }

        public Builder addDamageImmunity(TagKey<DamageType> damageType) {
            this.tagDamageImmunities.add(damageType);
            return this;
        }

        public Builder putDamageMultiplier(ResourceKey<DamageType> damageType, float multiplier) {
            this.damageMultipliers.put(damageType, multiplier);
            return this;
        }

        public Builder putDamageMultiplier(TagKey<DamageType> damageType, float multiplier) {
            this.tagDamageMultipliers.put(damageType, multiplier);
            return this;
        }

        public Builder putTooltipFormating(ChatFormatting... chatFormattings) {
            this.tooltipFormatting = chatFormattings;
            return this;
        }

        public Builder upgradable() {
            this.amplifiable = true;
            return this;
        }

        public Aura build() {
            return new Aura(type, range,
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
                    tagDamageMultipliers,
                    tooltipFormatting,
                    amplifiable
            );
        }
    }
}
