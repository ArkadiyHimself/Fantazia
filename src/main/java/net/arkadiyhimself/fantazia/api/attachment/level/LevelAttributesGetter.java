package net.arkadiyhimself.fantazia.api.attachment.level;

import net.arkadiyhimself.fantazia.api.type.level.ILevelAttributeHolder;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class LevelAttributesGetter {

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

}
