package net.arkadiyhimself.fantazia.advanced.dynamicattributemodifying;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;
import java.util.function.Function;

public class DynamicModifier {
    private final LivingEntity entity;
    private final Attribute attribute;
    private final UUID id;
    private final String name;
    private final double amount;
    private final AttributeModifier.Operation operation;
    private final Function<LivingEntity, Float> percentGetter;
    private boolean removed = false;
    public DynamicModifier(LivingEntity entity, Attribute attribute, UUID id, String name, double amount, AttributeModifier.Operation operation, Function<LivingEntity, Float> percentGetter) {
        this.entity = entity;
        this.attribute = attribute;
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.operation = operation;
        this.percentGetter = percentGetter;
    }
    public DynamicModifier(LivingEntity entity, Attribute attribute, String name, double amount, AttributeModifier.Operation operation, Function<LivingEntity, Float> percentGetter) {
        this(entity, attribute, Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()), name, amount, operation, percentGetter);
    }
    public void tick() {
        if (removed) return;
        tryRemove();
        tryAdd();
    }
    private void tryRemove() {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        if (instance.getModifier(id) != null) instance.removeModifier(id);

    }
    private void tryAdd() {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        float percent = Mth.clamp(percentGetter.apply(entity), 0f, 1f);
        double amo = amount * percent;
        instance.addTransientModifier(new AttributeModifier(id, name, amo, operation));
    }
    public void removeModifier() {
        tryRemove();
        removed = true;
    }
}
