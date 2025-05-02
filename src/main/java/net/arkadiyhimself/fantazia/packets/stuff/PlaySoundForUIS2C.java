package net.arkadiyhimself.fantazia.packets.stuff;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PlaySoundForUIS2C(SoundEvent soundEvent, float pitch, float volume) implements IPacket {

    public static final CustomPacketPayload.Type<PlaySoundForUIS2C> TYPE = new Type<>(Fantazia.res("stuff.play_sound_for_ui"));

    public static final StreamCodec<ByteBuf, PlaySoundForUIS2C> CODEC = StreamCodec.composite(
            SoundEvent.DIRECT_STREAM_CODEC, PlaySoundForUIS2C::soundEvent,
            ByteBufCodecs.FLOAT, PlaySoundForUIS2C::pitch,
            ByteBufCodecs.FLOAT, PlaySoundForUIS2C::volume,
            PlaySoundForUIS2C::new);

    public PlaySoundForUIS2C(SoundEvent soundEvent) {
        this(soundEvent,1f,1f);
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> StuffHandlers.playSoundForUI(soundEvent, pitch, volume));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
