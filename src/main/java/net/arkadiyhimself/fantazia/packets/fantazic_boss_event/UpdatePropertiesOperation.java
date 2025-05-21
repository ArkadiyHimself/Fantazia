package net.arkadiyhimself.fantazia.packets.fantazic_boss_event;

import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.UUID;

public class UpdatePropertiesOperation implements FantazicBossEventPacket.Operation {

    private final boolean darkenScreen;
    private final boolean playMusic;
    private final boolean createWorldFog;

    UpdatePropertiesOperation(boolean darkenScreen, boolean playMusic, boolean createWorldFog) {
        this.darkenScreen = darkenScreen;
        this.playMusic = playMusic;
        this.createWorldFog = createWorldFog;
    }

    UpdatePropertiesOperation(RegistryFriendlyByteBuf buffer) {
        int i = buffer.readUnsignedByte();
        this.darkenScreen = (i & 1) > 0;
        this.playMusic = (i & 2) > 0;
        this.createWorldFog = (i & 4) > 0;
    }

    public FantazicBossEventPacket.OperationType getType() {
        return FantazicBossEventPacket.OperationType.UPDATE_PROPERTIES;
    }

    public void dispatch(UUID id) {
        FantazicBossEventHandler.updateProperties(id, this.darkenScreen, this.playMusic, this.createWorldFog);
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeByte(FantazicBossEventPacket.encodeProperties(this.darkenScreen, this.playMusic, this.createWorldFog));
    }
}
