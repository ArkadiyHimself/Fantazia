package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect;

import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LivingEffectGetter {
    public static <T extends LivingEffectHolder> @Nullable T takeHolder(LivingEntity livingEntity, Class<T> tClass) {
        return livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).actualHolder(tClass);
    }
    public static <T extends LivingEffectHolder> void acceptConsumer(LivingEntity livingEntity, Class<T> tClass, Consumer<T> consumer) {
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).optionalHolder(tClass).ifPresent(consumer);
    }
}
