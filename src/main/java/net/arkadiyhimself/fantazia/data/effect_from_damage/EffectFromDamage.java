package net.arkadiyhimself.fantazia.data.effect_from_damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.data.predicate.DamageTypePredicate;
import net.arkadiyhimself.fantazia.datagen.effect_from_damage.EffectFromDamageHolder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.Consumer;

public record EffectFromDamage(DamageTypePredicate predicate, List<MobEffectInstance> mobEffectInstances) {

    public void tryApplyEffects(LivingEntity livingEntity, Holder<DamageType> damageType) {
        if (predicate.matches(damageType)) for (MobEffectInstance instance : mobEffectInstances) livingEntity.addEffect(new MobEffectInstance(instance));
    }

    public static Builder builder(DamageTypePredicate predicate) {
        return new Builder(predicate);
    }

    public static class Builder {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                DamageTypePredicate.CODEC.fieldOf("damage_type").forGetter(builder -> builder.predicate),
                MobEffectInstance.CODEC.listOf().fieldOf("mob_effect_instances").forGetter(builder -> builder.instances)
        ).apply(instance, Builder::decode));

        private static Builder decode(DamageTypePredicate predicate, List<MobEffectInstance> instances) {
            Builder builder = builder(predicate);
            for (MobEffectInstance instance : instances) builder.addMobEffect(instance);
            return builder;
        }

        public DamageTypePredicate predicate;
        public final List<MobEffectInstance> instances = Lists.newArrayList();

        public Builder(DamageTypePredicate predicate) {
            this.predicate = predicate;
        }

        public Builder predicate(DamageTypePredicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder addMobEffect(MobEffectInstance instance) {
            this.instances.add(instance);
            return this;
        }

        public Builder addMobEffect(Holder<MobEffect> mobEffect, int duration, int amplifier) {
            return addMobEffect(new MobEffectInstance(mobEffect, duration, amplifier));
        }

        public Builder addMobEffect(Holder<MobEffect> mobEffect, int duration) {
            return addMobEffect(mobEffect, duration, 0);
        }

        public EffectFromDamage build() {
            return new EffectFromDamage(predicate, instances);
        }

        public EffectFromDamageHolder holder(ResourceLocation id) {
            return new EffectFromDamageHolder(id, this);
        }

        public void save(Consumer<EffectFromDamageHolder> consumer, ResourceLocation id) {
            consumer.accept(holder(id));
        }
    }
}
