package net.arkadiyhimself.fantazia.api.attachment.entity.living_data;

import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LivingDataGetter {
    public static <T extends LivingDataHolder> @Nullable T takeHolder(LivingEntity livingEntity, Class<T> tClass) {
        LivingDataManager livingDataManager = getUnwrap(livingEntity);
        return livingDataManager.actualHolder(tClass);
    }
    public static <T extends LivingDataHolder> void acceptConsumer(LivingEntity livingEntity, Class<T> tClass, Consumer<T> consumer) {
        LivingDataManager livingDataManager = getUnwrap(livingEntity);
        livingDataManager.optionalHolder(tClass).ifPresent(consumer);
    }

    public static LivingDataManager getUnwrap(LivingEntity entity) {
        return entity.getData(FTZAttachmentTypes.DATA_MANAGER);
    }

}
