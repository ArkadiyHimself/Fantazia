package net.arkadiyhimself.fantazia.packets.fantazic_boss_event;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.UUID;

public record UpdateNameOperation(Component name) implements FantazicBossEventPacket.Operation {

    UpdateNameOperation(RegistryFriendlyByteBuf p_323813_) {
        this(ComponentSerialization.TRUSTED_STREAM_CODEC.decode(p_323813_));
    }

    public FantazicBossEventPacket.OperationType getType() {
        return FantazicBossEventPacket.OperationType.UPDATE_NAME;
    }

    public void dispatch(UUID id) {
        FantazicBossEventHandler.updateName(id, this.name);
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buffer, this.name);
    }
}
