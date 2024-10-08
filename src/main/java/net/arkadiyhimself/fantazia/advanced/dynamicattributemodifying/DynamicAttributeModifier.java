package net.arkadiyhimself.fantazia.advanced.dynamicattributemodifying;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.function.Function;

/**
 * Dynamic Attribute Modifier (DAM) is a special kind of attribute modifiers whose value,
 * as the name suggests, dynamically changes every tick depending on the situation.
 * The way it works is by calculating the amount by which the attribute will be modified,
 * then trying to remove the already existing modifier applied by this DAM if it is present,
 * and then applying a new modifier with changed value every in-game tick
 * <br>
 * <br>
 * {@link #attribute} is the attribute that is being modified
 * <br>
 * {@link #id} is the unique id of the modifier to separate DAMs from each other properly. It used by both {@link AttributeModifier vanilla Modifier} which is reapplied by DAM every tick, and by DAM itself to separate it from other instances
 * <br>
 * <br>
 * {@link #amount} is the maximum amount of value by which attribute will be modified in respective operation
 * <br>
 * {@link #operation} is the operation for attribute modification
 * <br>
 * {@link #percentGetter} is the function which is called to calculate the percentage of amount that will be applied for attribute modifier every tick. Keep in mind that this function is supposed to return a floating value between 0.0 and 1.0, and even if it returns a negative value or a value greater than one, the value will be {@link Mth#clamp(int, int, int) clamped} in {@link #tryAdd(LivingEntity) applying method}
 */
public class DynamicAttributeModifier {
    private final Holder<Attribute> attribute;
    private final ResourceLocation id;
    private final double amount;
    private final AttributeModifier.Operation operation;
    private final Function<LivingEntity, Float> percentGetter;
    public DynamicAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation, Function<LivingEntity, Float> percentGetter) {
        this.attribute = attribute;
        this.id = id;
        this.amount = amount;
        this.operation = operation;
        this.percentGetter = percentGetter;
    }
    public DynamicAttributeModifier(Holder<Attribute> attribute, AttributeModifier modifier, Function<LivingEntity, Float> percentGetter) {
        this(attribute, modifier.id(), modifier.amount(), modifier.operation(), percentGetter);
    }
    public AttributeModifier maximumModifier() {
        return new AttributeModifier(id, amount, operation);
    }
    public void tick(LivingEntity entity) {
        tryRemove(entity);
        tryAdd(entity);
    }
    public void tryRemove(LivingEntity entity) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        if (instance.getModifier(id) != null) instance.removeModifier(id);
    }
    public void tryAdd(LivingEntity entity) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        float percent = Mth.clamp(percentGetter.apply(entity), 0f, 1f);
        double amo = amount * percent;
        instance.addTransientModifier(new AttributeModifier(id, amo, operation));
    }
    public ResourceLocation getId() {
        return id;
    }
}
