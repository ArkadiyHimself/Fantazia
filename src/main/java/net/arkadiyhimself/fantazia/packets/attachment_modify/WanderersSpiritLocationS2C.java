package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record WanderersSpiritLocationS2C(CompoundTag tag, boolean sound) implements IPacket {

    public static final CustomPacketPayload.Type<WanderersSpiritLocationS2C> TYPE = new Type<>(Fantazia.res("data_attachment_modify.wanderers_spirit_location"));

    public static final StreamCodec<ByteBuf, WanderersSpiritLocationS2C> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, WanderersSpiritLocationS2C::tag,
            ByteBufCodecs.BOOL, WanderersSpiritLocationS2C::sound,
            WanderersSpiritLocationS2C::new);

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.wanderersSpiritLocation(tag, sound));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
