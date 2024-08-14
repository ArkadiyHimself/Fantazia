package net.arkadiyhimself.fantazia.advanced.aura;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.items.ITooltipBuilder;
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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BasicAura<T extends Entity, M extends Entity> implements ITooltipBuilder {
    public enum TYPE {
        POSITIVE, NEGATIVE, MIXED
    }
    private final TYPE type;
    private final Class<T> tClass;
    private final float RANGE;
    /**
     * Contains attributes and respective attribute modifiers that are
     * <br>
     * applied to all suitable entities within aura
     */
    private final Map<Attribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
    /**
     * Contains DAMs which will be applied to all suitable entities within aura.
     * <br>
     * The percentage of each DAM depends on distance between affected entity
     * <br>
     * and the owner of aura: it goes from 0.0F when an entity is on the "edge" of
     * <br>
     * the aura to 1.0F when it is closest to the owner
     */
    private final Map<Attribute, AttributeModifier> dynamicAttributeModifiers = Maps.newHashMap();
    /**
     * Contains the mob effects which will be applied to suitable entities
     * <br>
     * inside aura every tick for 2 ticks. The integer value is the level
     * <br>
     * of respective applied effect
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
    public BasicAura(float range, Class<T> affectedType) {
        this.RANGE = range;
        this.type = TYPE.MIXED;
        this.tClass = affectedType;
    }
    public BasicAura(float range, TYPE type, Class<T> affectedType) {
        this.RANGE = range;
        this.type = type;
        this.tClass = affectedType;
    }
    public BasicAura<T,M> addAttributeModifier(Attribute attribute, AttributeModifier attributeModifier) {
        this.attributeModifiers.put(attribute, attributeModifier);
        return this;
    }
    public BasicAura<T,M> addDynamicAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        this.dynamicAttributeModifiers.put(attribute, modifier);
        return this;
    }
    public BasicAura<T,M> addMobEffect(MobEffect mobEffect, int level) {
        this.mobEffects.put(mobEffect, level);
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
    public BasicAura<T,M> addDamageImmunities(ResourceKey<DamageType> immunity) {
        this.IMMUNITIES.add(immunity);
        return this;
    }
    public BasicAura<T,M> addDamageMultipliers(Map.Entry<ResourceKey<DamageType>, Float> damageMultiplier) throws IllegalArgumentException {
        if (damageMultiplier.getValue() <= 0) throw new IllegalArgumentException("Use damage immunities list instead");
        this.MULTIPLIERS.put(damageMultiplier.getKey(), damageMultiplier.getValue());
        return this;
    }
    public BasicAura<T,M> tickingOnBlocks(BiConsumer<BlockPos, AuraInstance<T,M>> onTick) {
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
        } catch (NumberFormatException ignored) {}

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
    public List<Component> buildItemTooltip(@Nullable ItemStack stack) {
        List<Component> components = Lists.newArrayList();
        if (this.getID() == null) return components;
        String basicPath = "aura." + this.getID().getNamespace() + "." + this.getID().getPath();
        int amo = 0;
        if (!Screen.hasShiftDown()) {
            components.add(Component.translatable(" "));
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                amo = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}

            if (amo > 0) for (int i = 1; i <= amo; i++) GuiHelper.addComponent(components, basicPath + ".desc." + i, null, null);
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
    public Class<T> affectedClass() {
        return tClass;
    }
    @Nullable
    public ResourceLocation getID() {
        List<RegistryObject<BasicAura<?,?>>> registryObjects = FantazicRegistry.AURAS.getEntries().stream().toList();
        for (RegistryObject<BasicAura<?,?>> basicAuraRegistryObject : registryObjects) if (basicAuraRegistryObject.get() == this) return basicAuraRegistryObject.getId();
        return null;
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
        return RANGE;
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
    public Map<Attribute, AttributeModifier> getDynamicAttributeModifiers() {
        return dynamicAttributeModifiers;
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
    public ImmutableList<ResourceKey<DamageType>> immunities() {
        return ImmutableList.copyOf(IMMUNITIES);
    }
    public Map<ResourceKey<DamageType>, Float> multipliers() {
        return Collections.unmodifiableMap(MULTIPLIERS);
    }
}
