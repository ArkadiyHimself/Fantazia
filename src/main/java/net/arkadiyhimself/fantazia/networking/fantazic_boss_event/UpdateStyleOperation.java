package net.arkadiyhimself.fantazia.networking.fantazic_boss_event;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.BossEvent;

import java.util.UUID;

public class UpdateStyleOperation implements FantazicBossEventPacket.Operation {

    private final BossEvent.BossBarColor color;
    private final BossEvent.BossBarOverlay overlay;

    UpdateStyleOperation(BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
        this.color = color;
        this.overlay = overlay;
    }

    UpdateStyleOperation(RegistryFriendlyByteBuf buffer) {
        this.color = buffer.readEnum(BossEvent.BossBarColor.class);
        this.overlay = buffer.readEnum(BossEvent.BossBarOverlay.class);
    }

    public FantazicBossEventPacket.OperationType getType() {
        return FantazicBossEventPacket.OperationType.UPDATE_STYLE;
    }

    public void dispatch(UUID id) {
        FantazicBossEventHandler.updateStyle(id, this.color, this.overlay);
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum(this.color);
        buffer.writeEnum(this.overlay);
    }
}
