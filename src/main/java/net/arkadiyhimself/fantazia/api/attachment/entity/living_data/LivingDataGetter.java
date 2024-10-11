package net.arkadiyhimself.fantazia.api.attachment.entity.living_data;

import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LivingDataGetter {

    public static <T extends LivingDataHolder> @Nullable T takeHolder(LivingEntity livingEntity, Class<T> tClass) {
        return livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).actualHolder(tClass);
    }

    public static <T extends LivingDataHolder> void acceptConsumer(LivingEntity livingEntity, Class<T> tClass, Consumer<T> consumer) {
        livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).optionalHolder(tClass).ifPresent(consumer);
    }
}
