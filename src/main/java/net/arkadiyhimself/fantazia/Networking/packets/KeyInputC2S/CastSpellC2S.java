package net.arkadiyhimself.fantazia.Networking.packets.KeyInputC2S;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.Items.MagicCasters.SpellCaster;
import net.arkadiyhimself.fantazia.Networking.NetworkHandler;
import net.arkadiyhimself.fantazia.Networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.api.SoundRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class CastSpellC2S implements IPacket {
    private final int slot;
    public CastSpellC2S(int slot) {
        this.slot = slot;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) { return; }
            Optional<SlotResult> result = CuriosApi.getCuriosHelper().findCurio(player, "spellcaster", slot);
            if (result.isEmpty()) { return; }
            Item spellCaster = result.get().stack().getItem();
            if (spellCaster instanceof SpellCaster selfCaster) {
                if (!selfCaster.tryCast(player)) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DENIED.get()), context.getSender());
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeInt(slot);
    }
    public static CastSpellC2S read(FriendlyByteBuf packetBuf) {
        return new CastSpellC2S(packetBuf.readInt());
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, CastSpellC2S.class, CastSpellC2S::read);
    }
}
