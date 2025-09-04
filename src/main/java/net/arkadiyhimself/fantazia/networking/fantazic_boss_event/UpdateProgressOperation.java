package net.arkadiyhimself.fantazia.networking.fantazic_boss_event;

import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.UUID;

public record UpdateProgressOperation(float progress, float barrier) implements FantazicBossEventPacket.Operation {

    public UpdateProgressOperation(RegistryFriendlyByteBuf byteBuf) {
        this(byteBuf.readFloat(), byteBuf.readFloat());
    }

    @Override
    public FantazicBossEventPacket.OperationType getType() {
        return FantazicBossEventPacket.OperationType.UPDATE_PROGRESS;
    }

    @Override
    public void dispatch(UUID id) {
        FantazicBossEventHandler.updateProgress(id, this.progress, this.barrier);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(this.progress);
        buffer.writeFloat(this.barrier);
    }
}
