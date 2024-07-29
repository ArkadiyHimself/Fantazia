package net.arkadiyhimself.fantazia.advanced.aura;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSource;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BasicAura<T extends Entity, M extends Entity> {
    public static final HashMap<ResourceLocation, BasicAura<? extends Entity, ? extends Entity>> AURAS = Maps.newHashMap();
    public enum TYPE {
        POSITIVE, NEGATIVE, MIXED
    }
    public final TYPE type;
    private final Class<T> affectedType;
    private final float RANGE;
    private final ResourceLocation id;
    /**
     * Contains attributes and respective attribute modifiers that are
     * <br>
     * applied to all suitable entities within aura
     */
    private final Map<Attribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
    /**
     * Contains mob effects which are applied to all entities within aura.
     * <br>
     * Integer value is the level of effect
     */
    private final Map<MobEffect, Integer> mobEffects = Maps.newHashMap();
    /**
     * Primary Filter is supposed to check entity's «permanent» fields which
     * <br>
     * can not be changed or can only be changed once like its
     * <br>
     * {@linkplain Entity#getType() type}, {@link TamableAnimal#getOwner() owner}, {@link LivingEntity#fireImmune() built-in fire resistance}. etc.,
     */
    private BiPredicate<T, M> primaryFilter = (entity, owner) -> true;
    /**
     * Secondary Filter is supposed to check entity's «transient» fields which
     * <br>
     * change dynamically back and forth depending on the situation like its
     * <br>
     * {@link LivingEntity#getHealth() health}, {@link LivingEntity#getAttributes() attributes}, {@link LivingEntity#getTicksFrozen() freezing ticks}, {@link Player#getFoodData() food data} for players, etc.,
     */
    private BiPredicate<T, M> secondaryFilter = (entity, owner) -> true;
    /**
     * ownerConditions is supposed to check aura instance's owner's fields
     * <br>
     * to determine whether {@link BasicAura#onTickOwner} should be performed or not
     */
    private Predicate<M> ownerConditions = (owner) -> true;
    /**
     * OnTick is performed every tick on every entity inside the aura.
     * <br>
     * The owner is provided mostly for accessing its fields and creating {@link net.minecraft.world.damagesource.DamageSource damage sources}
     * <br>
     * or {@link HealingSource healing sources} with owner as their «causing entity», etc.
     * <br>
     * Although you can do anything with the owner of the aura in this BiConsumer,
     * <br>
     * I would advise you not to do that and use {@link BasicAura#onTickOwner} instead because not only
     * <br>
     * this interface isn't called when there are no entities inside the aura besides the owner,
     * <br>
     * the effects on owner will also be multiplied by the amount of entities inside if there are more than one
     */
    private BiConsumer<T, M> onTick = (entity, owner) -> {};
    /**
     * OnTickOwner is performed every tick on the owner of the aura
     */
    private Consumer<M> onTickOwner = (owner) -> {};
    /**
     * OnTickBlock is performed every tick on all blocks within aura
     */
    private BiConsumer<BlockPos, AuraInstance<T,M>> onTickBlock = ((blockPos, tmAuraInstance) -> {});
    /**
     * Contains all {@link DamageType types of damage} which suitable entities should
     * <br>
     * be immune to while within aura
     */
    private final List<ResourceKey<DamageType>> IMMUNITIES = Lists.newArrayList();
    /**
     * Whenever a suitable entity inside aura takes damage from a {@link net.minecraft.world.damagesource.DamageSource source},
     * <br>
     * whose {@link DamageType damage type} is inside this map, the damage is multiplied
     * <br>
     * by respective float value inside this map
     */
    private final Map<ResourceKey<DamageType>, Float> MULTIPLIERS = Maps.newHashMap();

    public BasicAura(float range, ResourceLocation id, Class<T> affectedType) {
        this.RANGE = range;
        this.type = TYPE.MIXED;
        this.id = id;

        this.affectedType = affectedType;
        AURAS.put(this.id, (this));
    }
    public BasicAura(float range, ResourceLocation id, TYPE type, Class<T> affectedType) {
        this.RANGE = range;
        this.type = type;
        this.id = id;

        this.affectedType = affectedType;
        AURAS.put(this.id, (BasicAura<Entity, Entity>) this);
    }
    public List<Component> iconTooltip() {
        List<Component> components = Lists.newArrayList();
        String lines = Component.translatable("aura.icon." + this.id.getNamespace() + "." + this.id.getPath() + ".lines").getString();
        int li = 0;
        try {
            li = Integer.parseInt(lines);
        } catch (NumberFormatException ignored) {}

        if (li > 0) {
            ChatFormatting[] head = switch (this.type) {
                case MIXED -> new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
                case NEGATIVE -> new ChatFormatting[]{ChatFormatting.RED};
                case POSITIVE -> new ChatFormatting[]{ChatFormatting.GREEN};
            };
            for (int i = 1; i <= li; i++) {
                components.add(Component.translatable("aura." + this.id.getNamespace() + "." + this.id.getPath() + "." + i).withStyle(head));
            }
        }
        return components;
    }
    public List<Component> itemTooltip() {
        List<Component> components = Lists.newArrayList();
        String basicPath = "aura." + this.id.getNamespace() + "." + this.id.getPath();
        int amo = 0;
        if (!Screen.hasShiftDown()) {
            components.add(Component.translatable(" "));
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                amo = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (amo > 0) {
                for (int i = 1; i <= amo; i++) {
                    GuiHelper.addComponent(components, basicPath + ".desc." + i, null, null);
                }
            }
            amo = 0;
            return components;
        }
        components.add(Component.translatable(" "));

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
        ChatFormatting[] head = switch (this.type) {
            case MIXED -> new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
            case NEGATIVE -> new ChatFormatting[]{ChatFormatting.RED};
            case POSITIVE -> new ChatFormatting[]{ChatFormatting.GREEN};
        };
        // spell name
        String namePath = basicPath + ".name";
        GuiHelper.addComponent(components, "tooltip.fantazia.common.aura", head, ability, Component.translatable(namePath).getString());
        // spell range
        String manacost = String.format("%.1f", this.getRadius());
        GuiHelper.addComponent(components, "tooltip.fantazia.common.aura_range", head, ability, manacost);
        components.add(Component.translatable(" "));

        String desc = Component.translatable(basicPath + ".lines").getString();
        try {
            amo = Integer.parseInt(desc);
        } catch (NumberFormatException ignored) {}

        if (amo > 0) for (int i = 1; i <= amo; i++) GuiHelper.addComponent(components, basicPath + "." + i, text, null);

        amo = 0;
        String pass = Component.translatable(basicPath + ".stats.lines").getString();
        try {
            amo = Integer.parseInt(pass);
        } catch (NumberFormatException ignored) {}

        if (amo > 0) {
            components.add(Component.translatable(" "));
            for (int i = 1; i <= amo; i++) GuiHelper.addComponent(components, basicPath + ".stats." + i, null, null);
        }

        return components;
    }
    @Nullable
    public ResourceLocation getMapKey() {
        return AURAS.containsValue(this) ? this.id : null;
    }
    public Class<T> getAffectedType() {
        return affectedType;
    }

    public ResourceLocation getId() {
        return id;
    }
    public Component getAuraComponent() {
        String s = "aura." + id.getNamespace() + "." + id.getPath();
        return Component.translatable(s);
    }
    public ResourceLocation getIcon() {
        return id.withPrefix("textures/aura/").withSuffix(".png");
    }
    public TYPE getType() {
        return type;
    }

    public float getRadius() {
        return RANGE;
    }
    public float getSecondaryRadius() {
        return getRadius();
    }
    public boolean couldAffect(T entity, M owner) {
        return primaryFilter.test(entity, owner) && entity != owner;
    }
    public boolean canAffect(T entity, M owner) {
        return primaryFilter.test(entity, owner) && secondaryFilter.test(entity, owner) && entity != owner;
    }
    public Map<MobEffect, Integer> getMobEffects() {
        return mobEffects;
    }
    public Map<Attribute, AttributeModifier> getAttributeModifiers() {
        return attributeModifiers;
    }
    public boolean primary(T entity, M owner) {
        return primaryFilter.test(entity, owner);
    }
    public boolean secondary(T entity, M owner) {
        return secondaryFilter.test(entity, owner);
    }
    public boolean ownerCond(M owner) {
        return ownerConditions.test(owner);
    }
    public void entityTick(T entity, M owner) {
        onTick.accept(entity, owner);
    }
    public void ownerTick(M owner) {
        onTickOwner.accept(owner);
    }
    public void blockTick(BlockPos blockPos, AuraInstance<T, M> auraInstance) {
        onTickBlock.accept(blockPos, auraInstance);
    }
    public BasicAura<T,M> addModifiers(Map<Attribute, AttributeModifier> attributeModifiers) {
        this.attributeModifiers.putAll(attributeModifiers);
        return this;
    }
    public BasicAura<T,M> addMobEffects(Map<MobEffect, Integer> mobEffects) {
        this.mobEffects.putAll(mobEffects);
        return this;
    }
    public BasicAura<T,M> addPrimaryFilter(BiPredicate<T, M> filter) {
        this.primaryFilter = filter;
        return this;
    }
    public BasicAura<T,M> addSecondaryFilter(BiPredicate<T, M> filter) {
        this.secondaryFilter = filter;
        return this;
    }
    public BasicAura<T,M> addOwnerConditions(Predicate<M> filter) {
        this.ownerConditions = filter;
        return this;
    }
    public BasicAura<T,M> tickingOnEntities(BiConsumer<T, M> onTick) {
        this.onTick = onTick;
        return this;
    }
    public BasicAura<T,M> tickingOnOwner(Consumer<M> onTick) {
        this.onTickOwner = onTick;
        return this;
    }
    public List<ResourceKey<DamageType>> immunities() {
        return ImmutableList.copyOf(IMMUNITIES);
    }
    public ImmutableMultimap<ResourceKey<DamageType>, Float> multipliers() {
        return ImmutableMultimap.copyOf(MULTIPLIERS.entrySet());
    }
    public BasicAura<T,M> addDamageImmunities(List<ResourceKey<DamageType>> damageImmunities) {
        this.IMMUNITIES.addAll(damageImmunities);
        return this;
    }
    public BasicAura<T,M> addDamageMultipliers(HashMap<ResourceKey<DamageType>, Float> damageMultipliers) {
        this.MULTIPLIERS.putAll(damageMultipliers);
        return this;
    }
    public BasicAura<T,M> tickingOnBlocks(BiConsumer<BlockPos, AuraInstance<T,M>> onTick) {
        this.onTickBlock = onTick;
        return this;
    }
}
