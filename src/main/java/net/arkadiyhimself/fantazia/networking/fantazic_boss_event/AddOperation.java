package net.arkadiyhimself.fantazia.networking.fantazic_boss_event;

import net.arkadiyhimself.fantazia.common.api.FantazicBossEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.BossEvent;

import java.util.UUID;

class AddOperation implements FantazicBossEventPacket.Operation {
    private final Component name;
    private final float progress;
    private final float barrier;
    private final BossEvent.BossBarColor color;
    private final BossEvent.BossBarOverlay overlay;
    private final boolean darkenScreen;
    private final boolean playMusic;
    private final boolean createWorldFog;

    AddOperation(FantazicBossEvent event) {
        this.name = event.getName();
        this.progress = event.getProgress();
        this.barrier = event.getBarrier();
        this.color = event.getColor();
        this.overlay = event.getOverlay();
        this.darkenScreen = event.shouldDarkenScreen();
        this.playMusic = event.shouldPlayBossMusic();
        this.createWorldFog = event.shouldCreateWorldFog();
    }

    protected AddOperation(RegistryFriendlyByteBuf buffer) {
        this.name = ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buffer);
        this.progress = buffer.readFloat();
        this.barrier = buffer.readFloat();
        this.color = buffer.readEnum(BossEvent.BossBarColor.class);
        this.overlay = buffer.readEnum(BossEvent.BossBarOverlay.class);
        int i = buffer.readUnsignedByte();
        this.darkenScreen = (i & 1) > 0;
        this.playMusic = (i & 2) > 0;
        this.createWorldFog = (i & 4) > 0;
    }

    public FantazicBossEventPacket.OperationType getType() {
        return FantazicBossEventPacket.OperationType.ADD;
    }

    public void dispatch(UUID id) {
        FantazicBossEventHandler.add(id, this.name, this.progress, this.barrier, this.color, this.overlay, this.darkenScreen, this.playMusic, this.createWorldFog);
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buffer, this.name);
        buffer.writeFloat(this.progress);
        buffer.writeFloat(this.barrier);
        buffer.writeEnum(this.color);
        buffer.writeEnum(this.overlay);
        buffer.writeByte(FantazicBossEventPacket.encodeProperties(this.darkenScreen, this.playMusic, this.createWorldFog));
    }
}
