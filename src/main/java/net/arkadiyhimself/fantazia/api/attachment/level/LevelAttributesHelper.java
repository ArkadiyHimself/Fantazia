package net.arkadiyhimself.fantazia.api.attachment.level;

import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSource;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class LevelAttributesHelper {

    private LevelAttributesHelper() {}

    public static <T extends ILevelAttributeHolder> T takeHolder(Level level, Class<T> tClass) {
        LevelAttributes levelAttributes = getUnwrap(level);
        return levelAttributes.actualHolder(tClass);
    }

    public static <T extends ILevelAttributeHolder> void acceptConsumer(Level level, Class<T> tClass, Consumer<T> consumer) {
        LevelAttributes levelAttributes = getUnwrap(level);
        levelAttributes.optionalHolder(tClass).ifPresent(consumer);
    }

    public static LevelAttributes getUnwrap(Level level) {
        return level.getData(FTZAttachmentTypes.LEVEL_ATTRIBUTES);
    }

    public static @Nullable HealingSourcesHolder getHealingSources(Level level) {
        return takeHolder(level, HealingSourcesHolder.class);
    }

    public static @Nullable DamageSourcesHolder getDamageSources(Level level) {
        return takeHolder(level, DamageSourcesHolder.class);
    }

    public static boolean hurtEntity(Entity entity, float amount, Function<DamageSourcesHolder, DamageSource> sourceFunction) {
        DamageSourcesHolder holder = getDamageSources(entity.level());
        return holder != null && entity.hurt(sourceFunction.apply(holder), amount);
    }

    public static <T extends Entity> boolean hurtEntity(Entity entity, T directAttacker, float amount, BiFunction<DamageSourcesHolder, T, DamageSource> sourceFunction) {
        DamageSourcesHolder holder = getDamageSources(entity.level());
        return holder != null && entity.hurt(sourceFunction.apply(holder, directAttacker), amount);
    }

    public static boolean healEntity(LivingEntity entity, float amount, Function<HealingSourcesHolder, HealingSource> sourceFunction) {
        HealingSourcesHolder holder = getHealingSources(entity.level());
        return holder != null && AdvancedHealing.tryHeal(entity, sourceFunction.apply(holder), amount);
    }

    public static boolean healEntityByOther(LivingEntity entity, Entity healer, float amount, BiFunction<HealingSourcesHolder, Entity, HealingSource> sourceFunction) {
        HealingSourcesHolder holder = getHealingSources(entity.level());
        return holder != null && AdvancedHealing.tryHeal(entity, sourceFunction.apply(holder, healer), amount);
    }

    public static boolean healEntityByItself(LivingEntity entity, float amount, BiFunction<HealingSourcesHolder, Entity, HealingSource> sourceFunction) {
        HealingSourcesHolder holder = getHealingSources(entity.level());
        return holder != null && AdvancedHealing.tryHeal(entity, sourceFunction.apply(holder, entity), amount);
    }
}
