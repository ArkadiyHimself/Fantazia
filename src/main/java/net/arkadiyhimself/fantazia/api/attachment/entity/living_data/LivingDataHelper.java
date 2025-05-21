package net.arkadiyhimself.fantazia.api.attachment.entity.living_data;

import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LivingDataHelper {

    public static <T extends LivingDataHolder> @Nullable T takeHolder(@Nullable LivingEntity livingEntity, Class<T> tClass) {
        return livingEntity == null ? null : livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).actualHolder(tClass);
    }

    public static <T extends LivingDataHolder> void acceptConsumer(@Nullable LivingEntity livingEntity, Class<T> tClass, Consumer<T> consumer) {
        if (livingEntity == null) return;
        livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).optionalHolder(tClass).ifPresent(consumer);
    }

}
