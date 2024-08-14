package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;

public interface FTZSoundEventTags {
    TagKey<SoundEvent> NOT_MUTED = create("not_muted");
    private static TagKey<SoundEvent> create(String pName) {
        return TagKey.create(Registries.SOUND_EVENT, Fantazia.res(pName));
    }
}
