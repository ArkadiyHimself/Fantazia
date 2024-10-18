package net.arkadiyhimself.fantazia.packets.attachment_syncing;


import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.type.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PlayerAbilityUpdateS2C(CompoundTag compoundTag, int id) implements IPacket {

    public static final CustomPacketPayload.Type<PlayerAbilityUpdateS2C> TYPE = new Type<>(Fantazia.res("data_attachment_update.player_ability"));

    public static final StreamCodec<ByteBuf, PlayerAbilityUpdateS2C> CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, PlayerAbilityUpdateS2C::compoundTag, ByteBufCodecs.INT, PlayerAbilityUpdateS2C::id, PlayerAbilityUpdateS2C::new);

    public PlayerAbilityUpdateS2C(Player player) {
        this(player.getData(FTZAttachmentTypes.ABILITY_MANAGER).syncSerialize(), player.getId());
    }


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel == null) return;
            Entity entity = clientLevel.getEntity(id);
            if (!(entity instanceof Player player) || !entity.hasData(FTZAttachmentTypes.ABILITY_MANAGER)) return;
            player.getData(FTZAttachmentTypes.ABILITY_MANAGER).syncDeserialize(compoundTag);
        });
    }
}
