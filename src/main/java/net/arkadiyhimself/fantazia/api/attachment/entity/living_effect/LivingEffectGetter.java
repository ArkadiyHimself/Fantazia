package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect;

import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LivingEffectGetter {
    public static <T extends LivingEffectHolder> @Nullable T takeHolder(LivingEntity livingEntity, Class<T> tClass) {
        LivingEffectManager livingEffectManager = getUnwrap(livingEntity);
        return livingEffectManager.actualHolder(tClass);
    }
    public static <T extends LivingEffectHolder> void acceptConsumer(LivingEntity livingEntity, Class<T> tClass, Consumer<T> consumer) {
        LivingEffectManager livingEffectManager = getUnwrap(livingEntity);
        livingEffectManager.optionalHolder(tClass).ifPresent(consumer);
    }

    public static LivingEffectManager getUnwrap(LivingEntity entity) {
        return entity.getData(FTZAttachmentTypes.EFFECT_MANAGER);
    }
}
