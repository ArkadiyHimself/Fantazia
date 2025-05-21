package net.arkadiyhimself.fantazia.packets.fantazic_boss_event;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.FantazicBossEvent;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.BossEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public record FantazicBossEventPacket(UUID uuid, Operation operation) implements IPacket {

    public static final CustomPacketPayload.Type<FantazicBossEventPacket> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("fantazic_boss_event"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FantazicBossEventPacket> CODEC = CustomPacketPayload.codec(FantazicBossEventPacket::write, FantazicBossEventPacket::read);
    static final Operation REMOVE_OPERATION = new Operation() {

        public OperationType getType() {
            return OperationType.REMOVE;
        }

        public void dispatch(UUID uuid) {
            FantazicBossEventHandler.remove(uuid);
        }

        public void write(RegistryFriendlyByteBuf byteBuf) {}
    };

    public static FantazicBossEventPacket createAddPacket(FantazicBossEvent event) {
        return new FantazicBossEventPacket(event.getId(), new AddOperation(event));
    }

    public static FantazicBossEventPacket createRemovePacket(UUID id) {
        return new FantazicBossEventPacket(id, REMOVE_OPERATION);
    }

    public static FantazicBossEventPacket createUpdateProgressPacket(FantazicBossEvent event) {
        return new FantazicBossEventPacket(event.getId(), new UpdateProgressOperation(event.getProgress(), event.getBarrier()));
    }

    public static FantazicBossEventPacket createUpdateNamePacket(BossEvent event) {
        return new FantazicBossEventPacket(event.getId(), new UpdateNameOperation(event.getName()));
    }

    public static FantazicBossEventPacket createUpdateStylePacket(BossEvent event) {
        return new FantazicBossEventPacket(event.getId(), new UpdateStyleOperation(event.getColor(), event.getOverlay()));
    }

    public static FantazicBossEventPacket createUpdatePropertiesPacket(BossEvent event) {
        return new FantazicBossEventPacket(event.getId(), new UpdatePropertiesOperation(event.shouldDarkenScreen(), event.shouldPlayBossMusic(), event.shouldCreateWorldFog()));
    }

    private static FantazicBossEventPacket read(RegistryFriendlyByteBuf buffer) {
        UUID id = buffer.readUUID();
        OperationType operationType = buffer.readEnum(OperationType.class);
        Operation operation = operationType.reader.decode(buffer);
        return new FantazicBossEventPacket(id, operation);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUUID(this.uuid);
        buffer.writeEnum(this.operation.getType());
        this.operation.write(buffer);
    }

    @Override
    public void handle(IPayloadContext context) {
        operation.dispatch(uuid);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static int encodeProperties(boolean darkenScreen, boolean playMusic, boolean createWorldFog) {
        int i = 0;
        if (darkenScreen) {
            i |= 1;
        }

        if (playMusic) {
            i |= 2;
        }

        if (createWorldFog) {
            i |= 4;
        }

        return i;
    }

    public enum OperationType {
        ADD(AddOperation::new),
        REMOVE(byteBuf -> REMOVE_OPERATION),
        UPDATE_PROGRESS(UpdateProgressOperation::new),
        UPDATE_NAME(UpdateNameOperation::new),
        UPDATE_STYLE(UpdateStyleOperation::new),
        UPDATE_PROPERTIES(UpdatePropertiesOperation::new);

        final StreamDecoder<RegistryFriendlyByteBuf, Operation> reader;

        OperationType(StreamDecoder<RegistryFriendlyByteBuf, Operation> reader) {
            this.reader = reader;
        }
    }

    interface Operation {
        OperationType getType();

        void dispatch(UUID id);

        void write(RegistryFriendlyByteBuf buffer);
    }

}
