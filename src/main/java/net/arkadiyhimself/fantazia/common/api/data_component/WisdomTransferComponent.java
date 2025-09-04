package net.arkadiyhimself.fantazia.common.api.data_component;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public enum WisdomTransferComponent implements StringRepresentable {

    ABSORB("absorb", FTZSoundEvents.WISDOM_ABSORB, ChatFormatting.BLUE),
    RELEASE("release", FTZSoundEvents.WISDOM_RELEASE, ChatFormatting.RED);

    public static final Codec<WisdomTransferComponent> CODEC = StringRepresentable.fromEnum(
            WisdomTransferComponent::values
    );
    public static final StreamCodec<FriendlyByteBuf, WisdomTransferComponent> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(
            WisdomTransferComponent.class);

    private final String name;
    private final Holder<SoundEvent> soundEvent;
    private final ChatFormatting[] formattings;

    WisdomTransferComponent(String name, DeferredHolder<SoundEvent, SoundEvent> soundEvent, ChatFormatting... formattings) {
        this.name = name;
        this.soundEvent = soundEvent;
        this.formattings = formattings;
    }

    public WisdomTransferComponent nextOne() {
        if (this == ABSORB) return RELEASE;
        else return ABSORB;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }

    public ChatFormatting[] getFormattings() {
        return formattings;
    }

    public Holder<SoundEvent> getSoundEvent() {
        return soundEvent;
    }
}
