package net.arkadiyhimself.fantazia.advanced.aura;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.type.item.ITooltipBuilder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BasicAura<T extends Entity> implements ITooltipBuilder {
    public enum TYPE {
        POSITIVE, NEGATIVE, MIXED
    }
    private final TYPE type;
    private final Class<T> tClass;
    private final float range;
    /**
     * Contains attributes and respective attribute modifiers that are
     * <br>
     * applied to all suitable entities within aura
     */
    private final Map<Holder<Attribute>, AttributeModifier> attributeModifiers = Maps.newHashMap();
    /**
     * Contains DAMs which will be applied to all suitable entities within aura.
     * <br>
     * The percentage of each DAM depends on distance between affected entity
     * <br>
     * and the owner of aura: it goes from 0.0F when an entity is on the "edge" of
     * <br>
     * the aura to 1.0F when it is closest to the owner
     */
    private final Map<Holder<Attribute>, AttributeModifier> dynamicAttributeModifiers = Maps.newHashMap();
    /**
     * Contains the mob effects which will be applied to suitable entities
     * <br>
     * inside aura every tick for 2 ticks. The integer value is the level
     * <br>
     * of respective applied effect
     */
    private final Map<Holder<MobEffect>, Integer> mobEffects = Maps.newHashMap();
    /**
     * Primary Filter is supposed to check entity's «permanent» fields which
     * <br>
     * can not be changed or can only be changed once like entity's
     * <br>
     * {@linkplain Entity#getType() type}, {@link TamableAnimal#getOwner() owner}, {@link LivingEntity#fireImmune() built-in fire resistance}. etc.,
     */
    private BiPredicate<T, Entity> primaryFilter = (entity, owner) -> true;
    /**
     * Secondary Filter is supposed to check entity's «transient» fields which
     * <br>
     * change dynamically back and forth depending on the situation like its
     * <br>
     * {@link LivingEntity#getHealth() health}, {@link LivingEntity#getAttributes() attributes}, {@link LivingEntity#getTicksFrozen() freezing ticks}, {@link Player#getFoodData() food data} for players, etc.,
     */
    private BiPredicate<T, Entity> secondaryFilter = (entity, owner) -> true;
    /**
     * ownerConditions is supposed to check aura instance's owner's fields
     * <br>
     * to determine whether {@link BasicAura#onTickOwner} should be performed or not
     */
    private Predicate<Entity> ownerConditions = owner -> true;
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
    private BiConsumer<T, Entity> onTick = (entity, owner) -> {};
    /**
     * OnTickOwner is performed every tick on the owner of the aura
     */
    private Consumer<Entity> onTickOwner = owner -> {};
    /**
     * OnTickBlock is performed every tick on all blocks within aura
     */
    private BiConsumer<BlockPos, AuraInstance<T>> onTickBlock = ((blockPos, tmAuraInstance) -> {});
    /**
     * Contains all {@link DamageType types of damage} which suitable entities should
     * <br>
     * be immune to while within aura
     */
    private final List<ResourceKey<DamageType>> immunities = Lists.newArrayList();
    /**
     * Whenever a suitable entity inside aura takes damage from a {@link net.minecraft.world.damagesource.DamageSource source},
     * <br>
     * whose {@link DamageType damage type} is inside this map, the damage is multiplied
     * <br>
     * by respective float value inside this map
     */
    private final Map<ResourceKey<DamageType>, Float> multipliers = Maps.newHashMap();
    public BasicAura(float range, Class<T> affectedType) {
        this.range = range;
        this.type = TYPE.MIXED;
        this.tClass = affectedType;
    }
    public BasicAura(float range, TYPE type, Class<T> affectedType) {
        this.range = range;
        this.type = type;
        this.tClass = affectedType;
    }
    public BasicAura<T> addAttributeModifier(Holder<Attribute> attribute, AttributeModifier attributeModifier) {
        this.attributeModifiers.put(attribute, attributeModifier);
        return this;
    }
    public BasicAura<T> addDynamicAttributeModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
        this.dynamicAttributeModifiers.put(attribute, modifier);
        return this;
    }
    public BasicAura<T> addMobEffect(Holder<MobEffect> mobEffect, int level) {
        this.mobEffects.put(mobEffect, level);
        return this;
    }
    public BasicAura<T> addPrimaryFilter(BiPredicate<T, Entity> filter) {
        this.primaryFilter = filter;
        return this;
    }
    public BasicAura<T> addSecondaryFilter(BiPredicate<T, Entity> filter) {
        this.secondaryFilter = filter;
        return this;
    }
    public BasicAura<T> addOwnerConditions(Predicate<Entity> filter) {
        this.ownerConditions = filter;
        return this;
    }
    public BasicAura<T> tickingOnEntities(BiConsumer<T, Entity> onTick) {
        this.onTick = onTick;
        return this;
    }
    public BasicAura<T> tickingOnOwner(Consumer<Entity> onTick) {
        this.onTickOwner = onTick;
        return this;
    }
    public BasicAura<T> addDamageImmunities(ResourceKey<DamageType> immunity) {
        this.immunities.add(immunity);
        return this;
    }
    public BasicAura<T> addDamageMultipliers(Map.Entry<ResourceKey<DamageType>, Float> damageMultiplier) throws IllegalArgumentException {
        if (damageMultiplier.getValue() <= 0) throw new IllegalArgumentException("Use damage immunities list instead");
        this.multipliers.put(damageMultiplier.getKey(), damageMultiplier.getValue());
        return this;
    }
    public BasicAura<T> tickingOnBlocks(BiConsumer<BlockPos, AuraInstance<T>> onTick) {
        this.onTickBlock = onTick;
        return this;
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
        ChatFormatting[] head = switch (this.type) {
            case MIXED -> new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
            case NEGATIVE -> new ChatFormatting[]{ChatFormatting.RED};
            case POSITIVE -> new ChatFormatting[]{ChatFormatting.GREEN};
        };
        // spell name
        String namePath = basicPath + ".name";
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.aura", head, ability, Component.translatable(namePath).getString()));

        // spell range
        String manacost = String.format("%.1f", this.getRadius());

        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.aura_range", head, ability, manacost));
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
        if (!FantazicRegistry.AURAS.containsValue(this)) throw new IllegalStateException("Aura is not registered!");
        return FantazicRegistry.AURAS.getKey(this);
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
    public void entityTick(T entity, Entity owner) {
        onTick.accept(entity, owner);
    }
    public void ownerTick(Entity owner) {
        onTickOwner.accept(owner);
    }
    public void blockTick(BlockPos blockPos, AuraInstance<T> auraInstance) {
        onTickBlock.accept(blockPos, auraInstance);
    }
    public ImmutableList<ResourceKey<DamageType>> immunities() {
        return ImmutableList.copyOf(immunities);
    }
    public Map<ResourceKey<DamageType>, Float> multipliers() {
        return Collections.unmodifiableMap(multipliers);
    }
}
