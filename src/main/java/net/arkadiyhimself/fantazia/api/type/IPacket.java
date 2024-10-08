package net.arkadiyhimself.fantazia.api.type;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IPacket extends CustomPacketPayload {
    void handle(IPayloadContext context);
}
